package com.mycontacts.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.mycontacts.R;
import com.mycontacts.controller.ContactListAdapter;
import com.mycontacts.model.Contact_Model;

import java.util.ArrayList;

public class AllContacts extends AppCompatActivity {

    ArrayList<Contact_Model> contactArrayList;
    RecyclerView recycle;
    ContactListAdapter adapter;

    ProgressDialog pd;
    String contactNumbers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        recycle = (RecyclerView) findViewById(R.id.recycle);
        LinearLayoutManager layoutManager = new LinearLayoutManager(AllContacts.this);
        recycle.setLayoutManager(layoutManager);

        try {
            // check permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getPermission();
            }else{
                new LoadContacts().execute();// Execute the async task
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        //  fetchContacts();
    }


    public void getPermission() {
        try {
            int readContactsFlag = 0;
            int permissionCount = 0;
            int permissionCountNew = 0;
            int flag = 0;

            //** check permission is GRANTED or Not in Marshmallow */
            int readContacts = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);

            if (readContacts != PackageManager.PERMISSION_GRANTED) {
                readContactsFlag = 1;
                permissionCount += 1;
                flag = 1;
            }

            String[] permissionCArr = new String[permissionCount];

            if (readContactsFlag == 1) {
                permissionCArr[permissionCountNew] = Manifest.permission.READ_CONTACTS;
                permissionCountNew += 1;
            }

            if (flag == 1) {
                ActivityCompat.requestPermissions(
                        this,
                        permissionCArr,
                        155
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 155) {
            if (grantResults.length > 0) {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new LoadContacts().execute();// Execute the async task
                }
            }
        }
    }

    // Async task to load contacts
    private class LoadContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            contactArrayList = fetchContacts();// Get contacts array list from this
            // method
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);

            // If array list is not null and is contains value
            if (contactArrayList != null && contactArrayList.size() > 0) {

                adapter = null;
                if (adapter == null) {
                    adapter = new ContactListAdapter(AllContacts.this, contactArrayList);
                    recycle.setAdapter(adapter);
                    // adapter.notifyDataSetChanged();
                }
                adapter.notifyDataSetChanged();
            } else {

                // If adapter is null then show toast
                Toast.makeText(AllContacts.this, "There are no contacts.",
                        Toast.LENGTH_LONG).show();
            }

            // Hide dialog if showing
            if (pd.isShowing())
                pd.dismiss();

        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            // Show Dialog
            pd = ProgressDialog.show(AllContacts.this, "Loading Contacts",
                    "Please Wait...");
        }

    }


    private ArrayList<Contact_Model> fetchContacts() {
        contactArrayList = new ArrayList<>();

        Uri uri = ContactsContract.Contacts.CONTENT_URI; // Contact URI
        String order = ContactsContract.Contacts.SORT_KEY_PRIMARY + " ASC";
        String[] projection = new String[]{ContactsContract.RawContacts._ID, ContactsContract.RawContacts.ACCOUNT_TYPE};

//        String selection = ContactsContract.RawContacts.ACCOUNT_TYPE + " <> 'com.anddroid.contacts.sim' "
//                + " AND " + ContactsContract.RawContacts.ACCOUNT_TYPE + " <> 'com.google' ";

        String selection = ContactsContract.RawContacts.ACCOUNT_TYPE + " <> 'com.anddroid.contacts.sim' ";

        ContentResolver contentResolver = getContentResolver();

        Cursor contactsCursor = getContentResolver().query(
                uri,
                null,
                null,
                null,
                order); //ContactsContract.Contacts.DISPLAY_NAME + " ASC ");


        int count = contactsCursor.getCount();
        Log.e("Count", "Contact Count " + count);
        // Move cursor at starting
        if (contactsCursor.moveToNext()) {

            do {

                long contactId = contactsCursor.getLong(contactsCursor.getColumnIndex("_ID")); // Get contact ID
                Uri dataUri = ContactsContract.Data.CONTENT_URI; // URI to get data of contacts

                Cursor dataCursor = getContentResolver().query(
                        dataUri,
                        null,
                        ContactsContract.Data.CONTACT_ID + " = " + contactId,
                        null,
                        null);// Return data cursor Re-presentation to contact ID

                // Strings to get all details
                String displayName = "";
                String nickName = "";
                String homePhone = "";
                String mobilePhone = "";
                String workPhone = "";
                String homeEmail = "";
                String workEmail = "";
                String companyName = "";
                String title = "";
                String photoURL = "";

                // Now start the cusrsor
                if (dataCursor.moveToFirst()) {
                    displayName = dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));// get the contact name
                    int photoUriIndex = dataCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI);
                    photoURL = dataCursor.getString(photoUriIndex);

                    do {
                        String mimeType = dataCursor.getString(dataCursor.getColumnIndex("mimetype"));
                        String contentTypeName = ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE;
                        String contentTypePhone = ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE;
                        String contentTypeEmail = ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE;
                        String contentTypeOrganization = ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE;
                        String contentTypePhoto = ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE;

                        if (mimeType.equals(contentTypeName)) {
                            nickName = dataCursor.getString(dataCursor.getColumnIndex("data1")); // Get Nick Name
                        }

                        // In this get All contact numbers like home,mobile, work, etc and add them to numbers string
//                        if (mimeType.equals(contentTypePhone)) {
                        if (contentTypePhone.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                            int phoneType = dataCursor.getInt(dataCursor.getColumnIndex("data2")); // Get Nick Name

                            switch (dataCursor.getInt(dataCursor.getColumnIndex("data2"))) {

                                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                    homePhone = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                    contactNumbers += "Home Phone : " + homePhone + "\n";
                                    break;

                                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                    workPhone = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                    contactNumbers += "Work Phone : " + workPhone + "\n";
                                    break;

                                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                    mobilePhone = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                    contactNumbers += "Mobile Phone : " + mobilePhone + "\n";
                                    break;
                            }
                        }

                        // In this get all Emails like home, work etc and add them to email string
                        if (mimeType.equals(contentTypeEmail)) {
                            int emailType = dataCursor.getInt(dataCursor.getColumnIndex("data2"));

                            switch (emailType) {
                                case ContactsContract.CommonDataKinds.Email.TYPE_HOME:
                                    homeEmail = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                    break;

                                case ContactsContract.CommonDataKinds.Email.TYPE_WORK:
                                    workEmail = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                    break;
                            }
                        }

                        if (mimeType.equals(contentTypeOrganization)) {
                            companyName = dataCursor.getString(dataCursor.getColumnIndex("data1"));// get company name
                            title = dataCursor.getString(dataCursor.getColumnIndex("data4"));// get Company title
                        }
                        if (mimeType.equals(contentTypePhoto)) {
                            //  photoURL = dataCursor.getString(dataCursor.getColumnIndex("data15")); // get photo in byte
                        }
                    } while (dataCursor.moveToNext());

                    Contact_Model contactModel = new Contact_Model();
                    contactModel.setContactId(Long.toString(contactId));

                    contactModel.setContactNumberHome(homePhone);
                    contactModel.setContactNumberWork(workPhone);
                    contactModel.setContactNumberMobile(mobilePhone);
                    contactModel.setContactEmailHome(homeEmail);
                    contactModel.setContactEmailWork(workEmail);
                    contactModel.setContactOtherDetails(companyName);
                    contactModel.setContactPhoto(photoURL);

                    if (!mobilePhone.equals("") || !mobilePhone.equals("null")) {
                        contactModel.setContactName(displayName);
                        contactModel.setContactNikName(nickName);
                        contactModel.setContactNumberMobile(mobilePhone);
                    }

                    contactArrayList.add(contactModel);
                }
            } while (contactsCursor.moveToNext());
        }
        return contactArrayList;
    }
}
