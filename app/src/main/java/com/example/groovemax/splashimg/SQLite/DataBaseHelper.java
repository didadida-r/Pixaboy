package com.example.groovemax.splashimg.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DataBase name:FavoriteSQ
 * table name:image_table
 * key:_id
 * user: the provider of the image
 * imageUrl: the large url of the image
 * imageUrlHD: the HD url of the image
 */
public class DataBaseHelper extends SQLiteOpenHelper{
    final String SQL_CREATE_TABLE = "create table image_table (" +
            "_id integer primary key autoincrement," +
            "user varchar(50),imageUrl varchar(100),imageUrlHD varchar(100))";

    public DataBaseHelper(Context context, String name, int version) {
        super(context, name, null, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("create a database");
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("update a database");
    }

    public boolean deleteDataBase(Context context){
        return context.deleteDatabase("cmd");
    }

}
