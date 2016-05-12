package com.example.groovemax.splashimg;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardLayout;
import com.dexafree.materialList.listeners.RecyclerItemClickListener;
import com.dexafree.materialList.view.MaterialListView;
import com.example.groovemax.splashimg.Application.MyApplication;
import com.example.groovemax.splashimg.Provider.MyCardProvider;
import com.example.groovemax.splashimg.net.ThreadPoolTaskLoadImg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;


public class MainActivity extends AppCompatActivity implements ThreadPoolTaskLoadImg.CallBack{

    private final static String TAG = "debug";

    private DrawerLayout drawerLayout;
    private MenuItem preMenuItem;
    private SearchView searchView = null;

    private PullRefreshLayout swipeRefreshLayout;
    private com.dexafree.materialList.view.MaterialListView materialListView;
    private CardView cardView;

    private final MyHandler myHandler = new MyHandler(this); //handler用于传递数据

    private ArrayList<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        initUi();

        /*
        LayoutInflater inflater = getLayoutInflater();
        CardLayout cardLayout = (CardLayout) inflater.inflate(R.layout.card_layout, null);
        cardView = (CardView) cardLayout.findViewById(R.id.cardView);
        if(cardView != null){
            Log.v(TAG, "not null");
            cardView.setContentPadding(0, 0, 0, 0);
        }
        */


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

        if(navigationView != null){
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    // 点击侧栏时
                    if (preMenuItem != null && preMenuItem != item)
                        preMenuItem.setChecked(false);
                    preMenuItem = item;
                    item.setChecked(true);
                    drawerLayout.closeDrawers();

                    if (item.getGroupId() == R.id.Categories) {
                        String httpArg = "key=2531387-55d5028d6a38d3b6659f59101" +
                                "&q=" + item.getTitle() +
                                "&image_type=photo&orientation=horizontal&lang=zh&response_group=high_resolution";
                        MyApplication.getThreadPoolManager().addAsyncTask(new ThreadPoolTaskLoadImg(httpArg, MainActivity.this));
                    }
                    return false;
                }
            });
        }

        materialListView = (MaterialListView) findViewById(R.id.materialListView);

        materialListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(Card card, int position) {
                Log.v(TAG, "tag " + card.getTag().toString());
                String[] data = (String[])card.getTag();
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

        swipeRefreshLayout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);
        swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    String httpArg = "key=2531387-55d5028d6a38d3b6659f59101" +
                            "&q=" + URLEncoder.encode("伦敦", "UTF-8") +
                            "&image_type=photo&orientation=horizontal&lang=zh&response_group=high_resolution";
                    MyApplication.getThreadPoolManager().addAsyncTask(new ThreadPoolTaskLoadImg(httpArg, MainActivity.this));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        });

        // the display in Homepage
        try {
            String httpArg = "key=2531387-55d5028d6a38d3b6659f59101" +
                    "&q=" + URLEncoder.encode("灯光", "UTF-8") +
                    "&image_type=photo&orientation=horizontal&lang=zh&response_group=high_resolution";
            MyApplication.getThreadPoolManager().addAsyncTask(new ThreadPoolTaskLoadImg(httpArg, MainActivity.this));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

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
                                "&image_type=photo&orientation=horizontal&lang=zh&response_group=high_resolution";
                        MyApplication.getThreadPoolManager().addAsyncTask(new ThreadPoolTaskLoadImg(httpArg, MainActivity.this));
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()){
            case R.id.action_settings:
                Toast.makeText(this, "action_settings", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;

    }

    /*
     * the CallBack in ThreadPoolTaskLoadImg
     */
    @Override
    public void onReady(String result) {

        Message message = new Message();
        message.what = 0x00;
        Bundle date = new Bundle();
        date.putString("result", result);
        message.setData(date);
        myHandler.sendMessage(message);
    }

    /*
     * 这里是对MainActivity的弱引用，不是对AppComPatActivity的弱引用
     */
    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> outerClass;

        MyHandler(MainActivity activity) {
            outerClass = new WeakReference<MainActivity>(activity);
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
                                    .setShadowImage(R.mipmap.shadow_image)
                                    .setTitle(user)
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
                default:
            }

            // scroll the listView to top
            activity.materialListView.smoothScrollToPosition(0);
            //refresh complete
            activity.swipeRefreshLayout.setRefreshing(false);
        }

    }

}


