package com.kawabanga.model;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * this class manage local image handling
 */

public class ModelFiles {

    static void saveImageToFile(Bitmap imageBitmap, String imageFileName){
        try {
            File dir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            //if the file does'nt exist
            if (!dir.exists())
            {
                boolean ret = dir.mkdirs();
            }

            File imageFile = new File(dir,imageFileName);
            imageFile.createNewFile();

            OutputStream out = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

            addPictureToGallery(imageFile);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    interface LoadImageFromFileAsync {
        void onComplete(Bitmap bitmap);
    }
    static void loadImageFromFileAsync(String imageFileName,
                                       final LoadImageFromFileAsync callback) {
        AsyncTask<String,String,Bitmap> task = new AsyncTask<String,String,Bitmap>(){
            @Override
            protected Bitmap doInBackground(String... params) {
                return loadImageFromFile(params[0]);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                callback.onComplete(bitmap);
            }
        };
        task.execute(imageFileName);
    }


    private static Bitmap loadImageFromFile(String imageFileName){
        Bitmap bitmap = null;
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File imageFile = new File(dir,imageFileName);
            InputStream inputStream = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(inputStream);
            Log.d("tag","got image from cache: " + imageFileName);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private static void addPictureToGallery(File imageFile){
        //add the picture to the gallery so we dont need to manage the cache size
        Intent mediaScanIntent = new
                Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(imageFile);
        mediaScanIntent.setData(contentUri);
        Kawabanga.getMyContext().sendBroadcast(mediaScanIntent);
    }

}
