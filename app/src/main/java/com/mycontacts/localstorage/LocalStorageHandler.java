package com.mycontacts.localstorage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mycontacts.model.M_Contact1;

import java.util.ArrayList;

public class LocalStorageHandler extends SQLiteOpenHelper {

    //Table  Fields
    public static final String ID = "id";
    public static final String CONTACT_ID = "contact_id";
    public static final String CONTACT_NAME = "contact_name";
    public static final String CONTACT_NUMBER = "contact_number";
    public static final String CONTACT_IMAGE = "contact_image";
    public static final String CONTACT_STATUS = "contact_status";

    private static final String TAG = LocalStorageHandler.class.getSimpleName();
    private static final String DATABASE_NAME = "contact.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DB_TABLE_NAME_CONTACT = "contact_details";

    // create table
    String CREATE_TABLE_CONTACT = "CREATE TABLE " + DB_TABLE_NAME_CONTACT + "( "
            + ID + " INTEGER PRIMARY KEY,"
            + CONTACT_ID + " TEXT,"
            + CONTACT_NAME + " TEXT,"
            + CONTACT_NUMBER + " TEXT,"
            + CONTACT_IMAGE + " TEXT,"
            + CONTACT_STATUS + " TEXT )";


    public LocalStorageHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CONTACT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrade  DB " + oldVersion + " older version:" + newVersion
                + "; new version");
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_NAME_CONTACT);
        onCreate(db);
    }

    //add data
    public void addContacts(M_Contact1 mContact) {
        try {
            long rows = 0;
            SQLiteDatabase conDB = this.getWritableDatabase();
            ContentValues expValues = new ContentValues();
            expValues.put(CONTACT_ID, mContact.getContact_ID());
            expValues.put(CONTACT_NAME, mContact.getContact_Name());
            expValues.put(CONTACT_NUMBER, mContact.getContact_Number());
            expValues.put(CONTACT_IMAGE, mContact.getContact_Image_uri());
            expValues.put(CONTACT_STATUS, mContact.getContact_Status());
            conDB.insert(DB_TABLE_NAME_CONTACT, null, expValues);
            conDB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //get all data
    public ArrayList<M_Contact1> getAllContacts() {
        SQLiteDatabase conDB = this.getReadableDatabase();
        ArrayList<M_Contact1> contactList = null;

        try {
            contactList = new ArrayList<>();
            String QUERY = "SELECT * FROM " + DB_TABLE_NAME_CONTACT;

            Cursor cursor = conDB.rawQuery(QUERY, null);
            if (!cursor.isLast()) {
                while (cursor.moveToNext()) {

                    M_Contact1 mContact = new M_Contact1();
                    mContact.setID(cursor.getInt(0));
                    mContact.setContact_ID(cursor.getString(1));
                    mContact.setContact_Name(cursor.getString(2));
                    mContact.setContact_Number(cursor.getString(3));
                    mContact.setContact_Image_uri(cursor.getString(4));
                    mContact.setContact_Status(cursor.getString(5));
                    contactList.add(mContact);
                }
            }
            conDB.close();
        } catch (Exception e) {
            Log.e("error", e + "");
        }
        return contactList;
    }

    // update table data
    public boolean updateData(String contact_Id, String contact_name, String contactNumber, String imagePath, String status) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(CONTACT_ID, contact_Id);
        contentValues.put(CONTACT_NAME, contact_name);
        contentValues.put(CONTACT_NUMBER, contactNumber);
        contentValues.put(CONTACT_IMAGE, imagePath);
        contentValues.put(CONTACT_STATUS, status);
        db.insert(DB_TABLE_NAME_CONTACT, null, contentValues);

        return true;
    }

    // Re-Create table
    public void createContact() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(CREATE_TABLE_CONTACT);
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // delete table
    public void deleteContact() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            //delete all rows in a table
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_NAME_CONTACT);
            db.close();
            createContact();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
