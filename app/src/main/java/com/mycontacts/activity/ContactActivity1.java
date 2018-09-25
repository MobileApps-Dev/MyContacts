package com.mycontacts.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.mycontacts.R;
import com.mycontacts.controller.ContactAdapter1;
import com.mycontacts.localstorage.LocalStorageHandler;
import com.mycontacts.model.M_Contact1;

import java.util.ArrayList;

public class ContactActivity1 extends AppCompatActivity {
    private static final String TAG = "Contact";
    ArrayList<M_Contact1> mContacts;
    RecyclerView recycle;
    ContactAdapter1 adapter;
    LocalStorageHandler localStorageHandler;
    ImageView img_tool;
    String phoneNumberSet;
    ProgressDialog progressDialog;
    private static final int PERMISSIONS_REQUEST = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        try {
            // Declare Toolbar fav view
            img_tool = findViewById(R.id.img_tool);
            // Declare ArrayList
            mContacts = new ArrayList<>();
            // Declare RecycleView
            recycle = (RecyclerView) findViewById(R.id.recycle);
            LinearLayoutManager layoutManager = new LinearLayoutManager(ContactActivity1.this);
            recycle.setLayoutManager(layoutManager);

            new AsynContactsLoad().execute();


//            // Declare LocalStorageHandler
//            localStorageHandler = new LocalStorageHandler(this);
//            //get data from LocalStorageHandler to arraylist
//            mContacts = localStorageHandler.getAllContacts();
//            // total  count of  LocalStorageHandler
//            int iCount = mContacts.size();
//            // check size
//            if (iCount > 0) { // size greater than Zero
//                Log.e(TAG, "iCount" + iCount);
//                // Load data to adapter view
//                adapter = new ContactAdapter(ContactActivity.this, mContacts);
//                recycle.setAdapter(adapter);
//                adapter.notifyDataSetChanged();
//            } else { // size less than Zero
//                // load Contacts
//                new AsynContactsLoad().execute();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // onclick on Toolbar image
        img_tool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactActivity1.this, FavContactActivity.class);
                startActivity(intent);
            }
        });

        try {
            // check permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getPermission();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                        PERMISSIONS_REQUEST
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults.length > 0) {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new AsynContactsLoad().execute(); // call contact load
                }
            }
        }
    }


    class AsynContactsLoad extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                progressDialog = new ProgressDialog(ContactActivity1.this);
                progressDialog.setMessage("Loading");
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            Cursor cursor = null;
            try {
                ContentResolver contentResolver = getContentResolver();
                String[] PROJECTION = new String[]{
                        ContactsContract.RawContacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Photo.CONTACT_ID};


                Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String order = ContactsContract.Contacts.SORT_KEY_PRIMARY + " ASC";
//                cursor = contentResolver.query(uri, PROJECTION, null, null, order);

                cursor = contentResolver.query(uri,
                        PROJECTION,
                        null,
                        null,
                        order);

                int cueCount = cursor.getCount(); // get cursor count

                if (cueCount > 0) {
                    //   localStorageHandler.deleteContact(); // Remove Previous database

                    // get contact details from cursor
                    int contactIdIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);
                    int nameIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY);
                    int phoneNumberIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int photoIdIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID);
                    int photoUriIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI);
                    cursor.moveToFirst();

                    while (cursor.moveToNext()) {

                        String idContact = cursor.getString(contactIdIdx);
                        String name = cursor.getString(nameIdx);
                        String phoneNumber = cursor.getString(phoneNumberIdx);
                        String photoURI = cursor.getString(photoUriIndex);

                        Log.e(TAG, name + "  " + phoneNumber + "   " + photoURI);
                        // create Model class
                        M_Contact1 M_Contact1 = new M_Contact1();
                        M_Contact1.setContact_ID(idContact);

                        // check name
                        if (name.equals("") || name.equals("null")) {
                            M_Contact1.setContact_Name("No Name");
                        } else {
                            M_Contact1.setContact_Name(name);
                        }
                        // check phone number
                        String ddphoneNumber = phoneNumber.replaceAll("[^0-9]", "");

                        //** Check Mobile number length**/
                        if (ddphoneNumber.length() == 10) {
                            phoneNumberSet = ddphoneNumber;
                        }
                        //** Check Mobile number length**/
                        if (ddphoneNumber.length() == 12) {
                            String numbar = ddphoneNumber.replaceFirst("91", "");
                            phoneNumberSet = numbar;
                        }
                        //** Check Mobile number length**/
                        if (ddphoneNumber.length() == 11) {
                            String numbar = ddphoneNumber.replaceFirst("0", "");
                            phoneNumberSet = numbar;
                        }
                        // check photo of contact
                        if (photoURI != null) {
                            M_Contact1.setContact_Image_uri(photoURI);
                        } else {
                            M_Contact1.setContact_Image_uri(null);
                        }
                        M_Contact1.setContact_Number(phoneNumberSet);
                        M_Contact1.setContact_Status("0");

                        // add data to local storage
                        //localStorageHandler.addContacts(M_Contact1);

                        mContacts.add(M_Contact1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
//                // get data from local storage
//                mContacts = localStorageHandler.getAllContacts();
//                int iCount = mContacts.size();
//                Log.e(TAG, "iCount" + iCount);
                // crate adapter
                adapter = new ContactAdapter1(ContactActivity1.this, mContacts);
                recycle.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                // dismiss dialog
                progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
