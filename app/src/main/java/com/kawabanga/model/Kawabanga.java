package com.kawabanga.model;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Created by bugsec on 20/08/2017.
 */

public class Kawabanga  extends Application {
        private static Context context;

        @Override
        public void onCreate() {
            super.onCreate();
            context = getApplicationContext();
            if(context!=null)
                Log.d("tag","");


        }


        public static Context getMyContext() {
            return context;
        }
    }
