package com.example.groovemax.splashimg;

import android.database.Cursor;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.transition.Transition;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.groovemax.splashimg.SQLite.DataBaseHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class ImageDetailActivity extends AppCompatActivity implements OnMenuItemClickListener {

    private static final String TAG = "debug";

    private ImageView imageView;
    private FloatingActionButton actionButton;
    private ContextMenuDialogFragment mMenuDialogFragment;
    private FragmentManager fragmentManager;
    private Bundle bundle;
    private Intent serviceIntent;
    private DataBaseHelper helper;

    private String imageUrl;    //the imageUrl to display (*,1280)
    private String imageUrlHD;  //the imageUrl to display (*,1920)
    private String title;       //the title of the image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_detail_layout);

        Intent intent = getIntent();
        imageUrl = intent.getStringExtra("largeImageURL");
        imageUrlHD = intent.getStringExtra("fullHDURL");
        title = intent.getStringExtra("user");

        initUi();

    }

    private void initUi(){
        imageView = (ImageView) findViewById(R.id.imageView);
        Picasso.with(this).load(imageUrl).into(imageView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // add MenuObject target
        MenuObject close = new MenuObject("close");
        close.setResource(R.drawable.ic_check);

        MenuObject setWall = new MenuObject("set wallpaper");
        setWall.setResource(R.mipmap.ic_arrow_back_white_24dp);

        MenuObject download = new MenuObject("download image");
        download.setResource(R.mipmap.ic_arrow_back_white_24dp);

        MenuObject downloadHD = new MenuObject("download HD image");
        downloadHD.setResource(R.mipmap.ic_arrow_back_white_24dp);

        MenuObject addFavorite = new MenuObject("add Favorites");
        addFavorite.setResource(R.mipmap.ic_arrow_back_white_24dp);

        List<MenuObject> menuObjects = new ArrayList<>();
        menuObjects.add(close);
        menuObjects.add(setWall);
        menuObjects.add(download);
        menuObjects.add(downloadHD);
        menuObjects.add(addFavorite);

        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize(150);
        menuParams.setMenuObjects(menuObjects);
        menuParams.setClosableOutside(true);

        // 设置mMenuDialogFragment的大小参数、显示元素以及添加监听
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(this);

        actionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager = getSupportFragmentManager();
                mMenuDialogFragment.show(fragmentManager, "ContextMenuDialogFragment");
            }
        });
    }

    /*
     * listener to the menu in the right side
     */
    @Override
    public void onMenuItemClick(View clickedView, int position) {
        Log.v(TAG, "position" + position);
        switch (position){
            case 0:
                /** close */
                break;
            case 1:
                /** set wallpaper */
                bundle = new Bundle();
                bundle.putInt("key", 1);
                bundle.putString("imageUrl", imageUrl);
                serviceIntent = new Intent("com.example.groovemax.service");
                serviceIntent.putExtras(bundle);
                startService(serviceIntent);
                break;
            case 2:
                /** download image */
                bundle = new Bundle();
                bundle.putInt("key", 2);
                bundle.putString("imageUrl", imageUrl);
                serviceIntent = new Intent("com.example.groovemax.service");
                serviceIntent.putExtras(bundle);
                startService(serviceIntent);
                break;
            case 3:
                /** download imageHD */
                bundle = new Bundle();
                bundle.putInt("key", 3);
                bundle.putString("imageUrl", imageUrlHD);
                serviceIntent = new Intent("com.example.groovemax.service");
                serviceIntent.putExtras(bundle);
                startService(serviceIntent);
                break;
            case 4:
                /** Add Favorite Image */
                helper = new DataBaseHelper(getApplicationContext(), "FavoriteSQ", 1);
                Cursor cursor = helper.getReadableDatabase().rawQuery("select * from image_table where "
                        + "imageUrl=?", new String[]{imageUrl});
                if(cursor.moveToFirst()){
                    Toast.makeText(this, "already added in Favorites", Toast.LENGTH_SHORT).show();
                    cursor.close();
                    return;
                }
                cursor.close();

                helper.getReadableDatabase().execSQL("insert into image_table values(NULL, ?, ?, ?)",
                        new String[]{title, imageUrl, imageUrlHD});
                helper.close();

            default:
                break;
        }
    }

    /*
         * Do not use anonymous callback
         * otherwise the instance will get garbage collected
         */
    private Target target = new Target(){

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

            Log.v(TAG, "onBitmapLoaded");
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(ImageDetailActivity.this);
            try{
                wallpaperManager.setBitmap(bitmap);
                Toast.makeText(ImageDetailActivity.this, "set wallpaper successfully", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
