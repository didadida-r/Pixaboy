package com.example.groovemax.splashimg;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.listeners.OnDismissCallback;
import com.dexafree.materialList.listeners.RecyclerItemClickListener;
import com.dexafree.materialList.view.MaterialListView;
import com.example.groovemax.splashimg.MaterialList.MyCardProvider;
import com.example.groovemax.splashimg.SQLite.DataBaseHelper;



/**
 *
 */
public class FavoriteActivity extends AppCompatActivity {
    private static final String TAG = "debug";

    private MaterialListView materialListView;
    private Card card;
    private RelativeLayout backgroundLayout;

    private DataBaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_layout);

        initUi();

    }

    private void initUi(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Favorite");
        toolbar.setTitleTextColor(Color.BLACK);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        materialListView = (MaterialListView) findViewById(R.id.materialListView);
        materialListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(Card card, int position) {
                Log.v(TAG, "tag " + card.getTag().toString());
                String[] data = (String[]) card.getTag();
                Intent intent = new Intent();
                intent.putExtra("user", data[0]);
                intent.putExtra("largeImageURL", data[1]);
                intent.putExtra("fullHDURL", data[2]);
                intent.setClass(FavoriteActivity.this, ImageDetailActivity.class);
                startActivity(intent);

            }

            @Override
            public void onItemLongClick(Card card, int position) {
                card.setDismissible(true);
                materialListView.getAdapter().remove(card, true);
            }
        });

        materialListView.setOnDismissCallback(new OnDismissCallback() {
            @Override
            public void onDismiss(Card card, int position) {
                String[] data = (String[]) card.getTag();
                helper.getReadableDatabase().delete("image_table", "imageUrl=?", new String[]{data[1]});
            }
        });

        helper = new DataBaseHelper(getApplicationContext(), "FavoriteSQ", 1);
        Cursor cursor = helper.getReadableDatabase().rawQuery("select * from image_table", null);
        while(cursor.moveToNext()){
            card = new Card.Builder(this)
                    .setTag(new String[]{cursor.getString(1), cursor.getString(2), cursor.getString(3)})
                    .withProvider(new CardProvider())
                    .setLayout(R.layout.card_favorite_layout)
                    .setDrawable(cursor.getString(2))
                    .endConfig()
                    .build();
            materialListView.getAdapter().add(card);
        }
        cursor.close();
        backgroundLayout = (RelativeLayout) findViewById(R.id.backgroundLayout);

        if(getSharedPreferences("theme", MODE_PRIVATE).getBoolean("themeColor", false)){
            backgroundLayout.setBackgroundColor(getResources().getColor(R.color.themeColor));
            toolbar.setBackgroundColor(getResources().getColor(R.color.themeColor));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(helper != null)
            helper.close();
    }
}
