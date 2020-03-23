package com.example.yiting.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    //创建数据库
    public DBHelper(@Nullable Context context) {
        super(context, "yiting.db", null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //用户搜索记录的表
        //插入的时候默认值为当前时间
        db.execSQL("create table search_record (userId int, " +
            "content varchar,createtime timestamp NOT NULL DEFAULT  (datetime('now','localtime')));");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
