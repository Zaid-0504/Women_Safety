package com.example.womensafetyapp;

import android.app.Application;

public class MyApplication extends Application {

    private static MyAppsNotificationManager myAppsNotificationManager;
    @Override
    public void onCreate() {
        super.onCreate();
        myAppsNotificationManager = MyAppsNotificationManager.getInstance(this);
        myAppsNotificationManager.registerNotificationChannelChannel(
                "example.permanence",
                "BackgroundService",
                "BackgroundService");
    }
    public static MyAppsNotificationManager getMyAppsNotificationManager(){
        return myAppsNotificationManager;
    }

}
