package com.kawabanga.model;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class Kawabanga  extends Application {
        private static Context context;

        @Override
        public void onCreate() {
            super.onCreate();
            context = getApplicationContext();
            if(context != null)
                Log.d("TAG","Kawabanga oncreate");

            else
                context = this;

        }


        public static Context getMyContext() {
            return context;
        }
    }
