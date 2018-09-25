package com.mycontacts.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.mycontacts.R;

public class MainActivity extends AppCompatActivity {

    Button btn_contacts, btn_social;
    private static final int PERMISSIONS_REQUEST = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Declare View
        btn_contacts = (Button) findViewById(R.id.btn_contacts);
        btn_social = (Button) findViewById(R.id.btn_social);

        // onclick on Contact Button
        btn_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(MainActivity.this, ContactActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // onclick on Social Button
        btn_social.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(MainActivity.this, SocialActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        try {
            //check run time permission
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
                //permission grant
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        try {
            //**** close  application from backPress ****/
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            //finish();
            finishAffinity();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



