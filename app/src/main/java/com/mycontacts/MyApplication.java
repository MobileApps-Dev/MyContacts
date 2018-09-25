package com.mycontacts;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class MyApplication extends Application {

   public static int flag=0;

   @Override
   public void onCreate() {
      super.onCreate();
      try {
         // Initialize the SDK before executing any other operations,
         FacebookSdk.sdkInitialize(getApplicationContext());
         AppEventsLogger.activateApp(this);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
