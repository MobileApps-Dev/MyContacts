package com.mycontacts.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mycontacts.R;
import com.mycontacts.controller.ContactFavAdapter;
import com.mycontacts.localstorage.LocalStorageHandler;
import com.mycontacts.model.M_Contact1;
import com.mycontacts.model.M_ContactFAV;

import java.util.ArrayList;

public class FavContactActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView txt_noData;

    LocalStorageHandler localStorageHandler;
    ArrayList<M_Contact1> mContactArrayList;
    ArrayList<M_ContactFAV> m_contactFAVS;
    ContactFavAdapter contactFavAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_contact);

        try {
            // Declare view Details
            recyclerView = findViewById(R.id.recycle);
            txt_noData = findViewById(R.id.txt_noData);
            GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
            recyclerView.setLayoutManager(layoutManager);

            // Declare Localstorage and arraylist
            localStorageHandler = new LocalStorageHandler(this);
            mContactArrayList = new ArrayList<>();
            m_contactFAVS = new ArrayList<>();
            //get all data from local storage
            mContactArrayList = localStorageHandler.getAllContacts();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // check data
            for (int i = 0; i < mContactArrayList.size(); i++) {
                String status = mContactArrayList.get(i).getContact_Status();
                //check status
                if (status.equals("1")) { // status is 1 they add in array list
                    // get data
                    String contact_name = mContactArrayList.get(i).getContact_Name();
                    String contactImageUri = mContactArrayList.get(i).getContact_Image_uri();
                    String contactNumber = mContactArrayList.get(i).getContact_Number();
                    String contact_id = mContactArrayList.get(i).getContact_ID();
                    Log.e("Name", contact_name);

                    //create model class of FavContact
                    M_ContactFAV contactFAV = new M_ContactFAV();
                    contactFAV.setContact_Name(contact_name);
                    contactFAV.setContact_Number(contactNumber);
                    contactFAV.setContact_Image_uri(contactImageUri);
                    m_contactFAVS.add(contactFAV);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // check array count of favContact
            int count = m_contactFAVS.size();
            if (count > 0) { // is >0 display Recycle
                recyclerView.setVisibility(View.VISIBLE);
                txt_noData.setVisibility(View.GONE);
                // create adapter of fav
                contactFavAdapter = new ContactFavAdapter(this, m_contactFAVS);
                recyclerView.setAdapter(contactFavAdapter);
            } else {  // is <0 Hide  Recycle
                recyclerView.setVisibility(View.GONE);
                txt_noData.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
