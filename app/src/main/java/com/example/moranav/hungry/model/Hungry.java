package com.example.moranav.hungry.model;

import android.app.Application;
import android.content.Context;
import android.util.Log;


public class Hungry extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        if(context!=null)
            Log.d("Tag","Hungry? : By Matan Mohabaty & Yarin Levy");

    }


    public static Context getMyContext() {
        return context;
    }
}