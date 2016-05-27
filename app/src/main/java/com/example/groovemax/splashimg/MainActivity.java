package com.example.groovemax.splashimg;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.listeners.RecyclerItemClickListener;
import com.dexafree.materialList.view.MaterialListView;
import com.example.groovemax.splashimg.Application.MyApplication;
import com.example.groovemax.splashimg.MaterialList.MyCardProvider;
import com.example.groovemax.splashimg.SQLite.DataBaseHelper;
import com.example.groovemax.splashimg.net.ThreadPoolTaskLoadImg;
import com.yalantis.phoenix.PullToRefreshView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity implements ThreadPoolTaskLoadImg.CallBack{

    private final static String TAG = "debug";

    private DrawerLayout drawerLayout;
    private MenuItem preMenuItem;
    private SearchView searchView = null;
    private PullToRefreshView swipeRefreshLayout;
    private com.dexafree.materialList.view.MaterialListView materialListView;

    private final MyHandler myHandler = new MyHandler(this); //handler用于传递数据
    private SharedPreferences sharedPreferences;
    private String imageType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        initUi();

        DataBaseHelper helper = new DataBaseHelper(getApplicationContext(), "FavoriteSQ", 1);
        helper.deleteDataBase(this);

        sharedPreferences = getSharedPreferences("theme", MODE_PRIVATE);
        imageType = sharedPreferences.getString("imageType", "photo");

    }

    private void initUi(){

        // add toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // add Navigation View and DrawerLayout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawLayout);
        final NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open,
                R.string.close);
        mDrawerToggle.syncState();
        drawerLayout.setDrawerListener(mDrawerToggle);

        // navigationView
        if(navigationView != null){
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {

                    if(item.getItemId() == R.id.nav_theme){
                        sharedPreferences = getSharedPreferences("theme", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        if(!sharedPreferences.getBoolean("themeColor", false)){
                            editor.putBoolean("themeColor", true);
                            editor.putString("imageType", "illustration");
                        }
                        else{
                            editor.putBoolean("themeColor", false);
                            editor.putString("imageType", "photo");
                        }
                        editor.commit();
                        recreate();
                    }

                    if(item.getItemId() == R.id.nav_favorite)
                        startActivity(new Intent(MainActivity.this, FavoriteActivity.class));

                    if (item.getGroupId() == R.id.Categories) {

                        if (preMenuItem != null && preMenuItem != item)
                            preMenuItem.setChecked(false);
                        preMenuItem = item;
                        item.setChecked(true);

                        String httpArg = "key=2531387-55d5028d6a38d3b6659f59101" +
                                "&q=" + item.getTitle() +
                                "&image_type=" + imageType + "&orientation=horizontal&lang=zh&response_group=high_resolution";
                        MyApplication.getThreadPoolManager().addAsyncTask(new ThreadPoolTaskLoadImg(httpArg, MainActivity.this, MainActivity.this));
                    }
                    drawerLayout.closeDrawers();
                    return false;
                }
            });
        }

        materialListView = (MaterialListView) findViewById(R.id.materialListView);
        materialListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(Card card, int position) {

                String[] data = (String[]) card.getTag();
                Intent intent = new Intent();
                intent.putExtra("largeImageURL", data[0]);
                intent.putExtra("fullHDURL", data[1]);
                intent.putExtra("user", data[2]);
                intent.setClass(MainActivity.this, ImageDetailActivity.class);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(Card card, int position) {

            }
        });

        // add the swipeRefreshLayout
        swipeRefreshLayout = (PullToRefreshView) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    String httpArg = "key=2531387-55d5028d6a38d3b6659f59101" +
                            "&q=" + URLEncoder.encode("伦敦", "UTF-8") +
                            "&image_type=" + imageType + "&orientation=horizontal&lang=zh&response_group=high_resolution";
                    MyApplication.getThreadPoolManager().addAsyncTask(new ThreadPoolTaskLoadImg(httpArg, MainActivity.this, MainActivity.this));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        // the display in Homepage
        try {
            String httpArg = "key=2531387-55d5028d6a38d3b6659f59101" +
                    "&q=" + URLEncoder.encode("灯光", "UTF-8") +
                    "&image_type=" + imageType + "&orientation=horizontal&lang=zh&response_group=high_resolution";
            MyApplication.getThreadPoolManager().addAsyncTask(new ThreadPoolTaskLoadImg(httpArg, MainActivity.this, MainActivity.this));
            swipeRefreshLayout.setRefreshing(true);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if(getSharedPreferences("theme", MODE_PRIVATE).getBoolean("themeColor", false)){
            drawerLayout.setBackgroundColor(getResources().getColor(R.color.themeColor));
            navigationView.setBackgroundColor(getResources().getColor(R.color.themeColor));
            toolbar.setBackgroundColor(getResources().getColor(R.color.themeColor));
        }

    }

    /*
         * create Search Menu and deal search
         */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if(searchView.getQuery().toString() == null | searchView.getQuery().toString().equals("")){
                        Toast.makeText(MainActivity.this, "请使用中文检索", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    try{
                        searchView.clearFocus();
                        String httpArg = "key=2531387-55d5028d6a38d3b6659f59101" +
                                "&q=" + URLEncoder.encode(searchView.getQuery().toString(), "UTF-8") +
                                "&image_type=" + imageType + "&orientation=horizontal&lang=zh&response_group=high_resolution";
                        MyApplication.getThreadPoolManager().addAsyncTask(new ThreadPoolTaskLoadImg(httpArg, MainActivity.this, MainActivity.this));
                        swipeRefreshLayout.setRefreshing(true);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }

        return super.onCreateOptionsMenu(menu);

    }

    /*
     * the CallBack in ThreadPoolTaskLoadImg
     */
    @Override
    public void onReady(String result) {

        if(result.equals("Net Error")){
            Message message = new Message();
            message.what = 0x01;
            Bundle date = new Bundle();
            date.putString("result", result);
            message.setData(date);
            myHandler.sendMessage(message);
        }else{
            Message message = new Message();
            message.what = 0x00;
            Bundle date = new Bundle();
            date.putString("result", result);
            message.setData(date);
            myHandler.sendMessage(message);
        }

    }

    /*
     * 这里是对MainActivity的弱引用，不是对AppComPatActivity的弱引用
     */
    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> outerClass;

        MyHandler(MainActivity activity) {
            outerClass = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = outerClass.get();
            if(activity == null){
                super.handleMessage(msg);
                return;
            }

            switch (msg.what) {
                case 0x00:
                    activity.materialListView.getAdapter().clearAll();
                    activity.swipeRefreshLayout.setRefreshing(true);

                    String result = msg.getData().getString("result");
                    Card card = null;
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        JSONArray jsonArray = new JSONArray(jsonObject.getString("hits"));
                        int i = 0;
                        while (jsonArray.get(i) != null) {
                            JSONObject item = jsonArray.getJSONObject(i);

                            String user = item.getString("user")==null ? "None" : item.getString("user");
                            card = new Card.Builder(activity)
                                    .setTag(new String[]{item.getString("largeImageURL"), item.getString("fullHDURL"), user})
                                    .withProvider(new MyCardProvider())
                                    .setLayout(R.layout.card_layout)
                                    .setImageTitle(user)
                                    .setDrawable(item.getString("webformatURL"))
                                    .endConfig()
                                    .build();

                            activity.materialListView.getAdapter().add(card);
                            i++;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 0x01:
                    //refresh complete
                    activity.swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(activity, "Net error", Toast.LENGTH_SHORT).show();
                    break;
                default:
            }

            // scroll the listView to top
            activity.materialListView.smoothScrollToPosition(0);

            //refresh complete
            activity.swipeRefreshLayout.setRefreshing(false);
        }

    }

}


