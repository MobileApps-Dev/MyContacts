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
import com.mycontacts.controller.ContactAdapter;
import com.mycontacts.localstorage.LocalStorageHandler;
import com.mycontacts.model.M_Contact;
import com.mycontacts.model.M_Contact1;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity {
    private static final String TAG = "Contact";
    ArrayList<M_Contact> mContacts;
    ArrayList<M_Contact1> m_contact1s;
    RecyclerView recycle;
    ContactAdapter adapter;
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
            m_contact1s = new ArrayList<>();
            // Declare RecycleView
            recycle = (RecyclerView) findViewById(R.id.recycle);
            LinearLayoutManager layoutManager = new LinearLayoutManager(ContactActivity.this);
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
                Intent intent = new Intent(ContactActivity.this, FavContactActivity.class);
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
                progressDialog = new ProgressDialog(ContactActivity.this);
                progressDialog.setMessage("Loading");
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params) {


            //getAllContacts();

            getContactsDetails();


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
                adapter = new ContactAdapter(ContactActivity.this, m_contact1s);
                recycle.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                // dismiss dialog
                progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getContactsDetails() {
        String DISPLAY_NAME = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME;

        String FILTER = DISPLAY_NAME + " NOT LIKE '%@%'";
        String ORDER = String.format("%1$s COLLATE NOCASE", DISPLAY_NAME);

        String[] PROJECTION = {
                ContactsContract.Contacts._ID,
                DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER
        };
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, PROJECTION, FILTER, null, ORDER);

        if (cursor != null && cursor.moveToFirst()) {
            do {

                // get the contact's information
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                Integer hasPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                // get the user's email address
                String email = null;
                Cursor ce = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                if (ce != null && ce.moveToFirst()) {
                    email = ce.getString(ce.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    ce.close();
                }

                // get the user's phone number
                String phone = null;
                if (hasPhone > 0) {
                    Cursor cp = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    if (cp != null && cp.moveToFirst()) {
                        phone = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        cp.close();
                    }
                }

                M_Contact1 contact = new M_Contact1();
                contact.setContact_Name(name);
                contact.setContact_Number(phone);
                m_contact1s.add(contact);

            } while (cursor.moveToNext());

            // clean up cursor
            cursor.close();
        }


    }


    public List<M_Contact> getAllContacts() {

//     mContacts = new ArrayList<>();
        try {
            // Get all raw contacts id list.
            List<Integer> rawContactsIdList = getRawContactsIdList();
            int contactListSize = rawContactsIdList.size();
            ContentResolver contentResolver = getContentResolver();

            for (int i = 0; i < contactListSize; i++) {
                Integer rawContactId = rawContactsIdList.get(i);
                Uri dataContentUri = ContactsContract.Data.CONTENT_URI;
                List<String> queryColumnList = new ArrayList<String>();
                queryColumnList.add(ContactsContract.Data.CONTACT_ID);
                queryColumnList.add(ContactsContract.Data.MIMETYPE);

                queryColumnList.add(ContactsContract.Data.DATA1);
                queryColumnList.add(ContactsContract.Data.DATA2);
                queryColumnList.add(ContactsContract.Data.DATA3);
                queryColumnList.add(ContactsContract.Data.DATA4);
                queryColumnList.add(ContactsContract.Data.DATA5);
                queryColumnList.add(ContactsContract.Data.DATA6);
                queryColumnList.add(ContactsContract.Data.DATA7);
                queryColumnList.add(ContactsContract.Data.DATA8);
                queryColumnList.add(ContactsContract.Data.DATA9);
                queryColumnList.add(ContactsContract.Data.DATA10);
                queryColumnList.add(ContactsContract.Data.DATA11);
                queryColumnList.add(ContactsContract.Data.DATA12);
                queryColumnList.add(ContactsContract.Data.DATA13);
                queryColumnList.add(ContactsContract.Data.DATA14);
                queryColumnList.add(ContactsContract.Data.DATA15);

                String queryColumnArr[] = queryColumnList.toArray(new String[queryColumnList.size()]);
                // Build query condition string. Query rows by contact id.
                StringBuffer whereClauseBuf = new StringBuffer();
                whereClauseBuf.append(ContactsContract.Data.RAW_CONTACT_ID);
                whereClauseBuf.append("=");
                whereClauseBuf.append(rawContactId);

                // Query data table and return related contact data.
                Cursor cursor = contentResolver.query(dataContentUri, queryColumnArr, whereClauseBuf.toString(), null, null);

                if (cursor != null && cursor.getCount() > 0) {
                    StringBuffer lineBuf = new StringBuffer();
                    cursor.moveToFirst();
                    lineBuf.append("Raw Contact Id : ");
                    lineBuf.append(rawContactId);

                    long contactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID));
                    lineBuf.append(" , Contact Id : ");
                    lineBuf.append(contactId);
                    do {
                        String mimeType = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE));
                        lineBuf.append(" \r\n , MimeType : ");
                        lineBuf.append(mimeType);

                        List<String> dataValueList = getColumnValueByMimetype(cursor, mimeType);
                        int dataValueListSize = dataValueList.size();

                        for (int j = 0; j < dataValueListSize; j++) {
                            String dataValue = dataValueList.get(j);
                            lineBuf.append(" , ");
                            lineBuf.append(dataValue);
                        }
                    } while (cursor.moveToNext());
                    Log.d(TAG, lineBuf.toString());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mContacts;
    }

    private List<String> getColumnValueByMimetype(Cursor cursor, String mimeType) {
        List<String> ret = new ArrayList<>();
        switch (mimeType) {
            case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE:
                try {
                    String emailAddress = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                    int emailType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                    String emailTypeStr = getEmailTypeString(emailType);
                    ret.add("Email Address : " + emailAddress);
                    ret.add("Email Int Type : " + emailType);
                    ret.add("Email String Type : " + emailTypeStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE:
                try {
                    String imProtocol = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.PROTOCOL));
                    String imId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
                    ret.add("IM Protocol : " + imProtocol);
                    ret.add("IM ID : " + imId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE:
                try {
                    String nickName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Nickname.NAME));
                    ret.add("Nick name : " + nickName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE:
                try {
                    String company = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));
                    String department = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DEPARTMENT));
                    String title = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
                    String jobDescription = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.JOB_DESCRIPTION));
                    String officeLocation = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.OFFICE_LOCATION));
                    ret.add("Company : " + company);
                    ret.add("department : " + department);
                    ret.add("Title : " + title);
                    ret.add("Job Description : " + jobDescription);
                    ret.add("Office Location : " + officeLocation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE:
                try {
                    String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    int phoneTypeInt = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                    String phoneTypeStr = getPhoneTypeString(phoneTypeInt);

                    ret.add("Phone Number : " + phoneNumber);
                    ret.add("Phone Type Integer : " + phoneTypeInt);
                    ret.add("Phone Type String : " + phoneTypeStr);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case ContactsContract.CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE:
                try {
                    String address = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.SipAddress.SIP_ADDRESS));
                    int addressTypeInt = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.SipAddress.TYPE));
                    String addressTypeStr = getEmailTypeString(addressTypeInt);

                    ret.add("Address : " + address);
                    ret.add("Address Type Integer : " + addressTypeInt);
                    ret.add("Address Type String : " + addressTypeStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE:
                try {
                    String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));
                    String givenName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
                    String familyName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
                    ret.add("Display Name : " + displayName);
                    ret.add("Given Name : " + givenName);
                    ret.add("Family Name : " + familyName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE:
                try {
                    String country = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                    String city = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                    String region = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                    String street = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                    String postcode = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                    int postType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
                    String postTypeStr = getEmailTypeString(postType);

                    ret.add("Country : " + country);
                    ret.add("City : " + city);
                    ret.add("Region : " + region);
                    ret.add("Street : " + street);
                    ret.add("Postcode : " + postcode);
                    ret.add("Post Type Integer : " + postType);
                    ret.add("Post Type String : " + postTypeStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case ContactsContract.CommonDataKinds.Identity.CONTENT_ITEM_TYPE:
                try {
                    String identity = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Identity.IDENTITY));
                    String namespace = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Identity.NAMESPACE));

                    ret.add("Identity : " + identity);
                    ret.add("Identity Namespace : " + namespace);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

//            case ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE:
//                try {
//                    String photo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO));
//                    String photoFileId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_FILE_ID));
//                    ret.add("Photo : " + photo);
//                    ret.add("Photo File Id: " + photoFileId);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;

            case ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE:
                try {
                    int groupId = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID));
                    ret.add("Group ID : " + groupId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE:
                try {
                    String websiteUrl = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));
                    int websiteTypeInt = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Website.TYPE));
                    String websiteTypeStr = getEmailTypeString(websiteTypeInt);

                    ret.add("Website Url : " + websiteUrl);
                    ret.add("Website Type Integer : " + websiteTypeInt);
                    ret.add("Website Type String : " + websiteTypeStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;


            case ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE:
                try {
                    String note = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                    ret.add("Note : " + note);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return ret;
    }

    private List<Integer> getRawContactsIdList() {
        List<Integer> ret = new ArrayList<>();

        try {
            ContentResolver contentResolver = getContentResolver();
            String[] PROJECTION = new String[]{ContactsContract.RawContacts._ID};
            Uri uri = ContactsContract.RawContacts.CONTENT_URI;
            Cursor cursor = contentResolver.query(uri, PROJECTION, null, null, null);
            int cueCount = cursor.getCount(); // get cursor count

            if (cursor != null) {
                cursor.moveToFirst();
                do {
                    int contactIdIdx = cursor.getColumnIndex(ContactsContract.RawContacts._ID);
                    int rawContactsId = cursor.getInt(contactIdIdx);
                    ret.add(new Integer(rawContactsId));
                } while (cursor.moveToNext());
            }
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    private String getEmailTypeString(int dataType) {
        String ret = "";

        if (ContactsContract.CommonDataKinds.Email.TYPE_HOME == dataType) {
            ret = "Home";
        } else if (ContactsContract.CommonDataKinds.Email.TYPE_WORK == dataType) {
            ret = "Work";
        }
        return ret;
    }

    private String getPhoneTypeString(int dataType) {
        String ret = "";

        if (ContactsContract.CommonDataKinds.Phone.TYPE_HOME == dataType) {
            ret = "Home";
        } else if (ContactsContract.CommonDataKinds.Phone.TYPE_WORK == dataType) {
            ret = "Work";
        } else if (ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE == dataType) {
            ret = "Mobile";
        }
        return ret;
    }
}
