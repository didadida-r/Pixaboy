package com.example.groovemax.splashimg;

import android.app.Service;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 *
 */
public class DownLoadService extends Service {

    private static final String TAG = "debug";

    private String imageUrl;            // image url
    private int key;                    // use the key to discriminate actions

    private static final String imageDir = Environment.getExternalStorageDirectory() + "/imageDownLoad/";
    private String imageName;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "onCreate");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.v(TAG, "onStart");
        if(intent != null){
            Bundle bundle = intent.getExtras();
            imageUrl = bundle.getString("imageUrl");
            key = bundle.getInt("key");
            Picasso.with(this).load(imageUrl).into(target);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Target target = new Target(){

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

            switch (key){
                case 1:
                    /** set wallpaper */
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(DownLoadService.this);
                    //int newWidth = wallpaperManager.getDesiredMinimumWidth();
                    //int newHeight = wallpaperManager.getDesiredMinimumHeight();
                    Bitmap resizedBitmap = Bitmap.createBitmap(
                            bitmap,
                            bitmap.getWidth()/2 - bitmap.getHeight()/2,
                            0,
                            bitmap.getHeight(),
                            bitmap.getHeight()
                    );

                    //Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
                    try{
                        wallpaperManager.setBitmap(resizedBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:

                case 3:
                    /** save the image weather it's HDImage or not */

                    // Caution: Be sure the Sdcard is free to use
                    if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                        Log.v(TAG, "Sdcard is busy");
                        return;
                    }

                    try{
                        // make dir to save image
                        File destDir = new File(Environment.getExternalStorageDirectory() + "/imageDownLoad/");
                        if (!destDir.exists()) {
                            destDir.mkdirs();
                        }
                        imageName = System.currentTimeMillis() + ".jpg";
                        File file = new File(imageDir, imageName);

                        // transfer bitmap to stream and save
                        FileOutputStream outputStream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        outputStream.flush();
                        outputStream.close();

                        // remind the System Gallery to update
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri uri = Uri.fromFile(file);
                        intent.setData(uri);
                        sendBroadcast(intent);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.v(TAG, "image save");
                    break;
                default:
                    break;
            }
            stopSelf();

        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Log.v(TAG, "onBitmapFailed");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            Log.v(TAG, "onPrepareLoad");
        }
    };

}
