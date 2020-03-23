package com.example.yiting.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.yiting.bean.RecordInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户搜索记录表操作
 */

public class DbDao {
    private final DBHelper mHelper;

    public DbDao(Context context) {
        mHelper = new DBHelper(context);//创建数据库
    }

    //添加搜索记录到数据库
    public void addRecord(int userId, String content) {
        // 获取数据库对象
        SQLiteDatabase db = mHelper.getReadableDatabase();
        db.execSQL("insert into search_record(userId,content) values (?,?)", new Object[]{userId, content});
        db.close();
    }


    public List<RecordInfo> queryByUserId(int userId) {
        List<RecordInfo> records = new ArrayList<>();
        // 获取数据库对象
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String sql = "select * from search_record where userId =? order by createtime DESC";
        Cursor cursor = db.rawQuery(sql, new String[]{userId + ""});

        while (cursor.moveToNext()) {
            RecordInfo record = new RecordInfo();
            record.setContent(cursor.getString(cursor.getColumnIndex("content")));
            record.setTime(cursor.getString(cursor.getColumnIndex("createtime")));
            records.add(record);
        }

        // 关闭资源
        cursor.close();
        db.close();
        return records;
    }

    public void deleteAll(int userId) {
        // 获取数据库对象
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String sql = "delete from search_record where userId =?";
        db.execSQL(sql, new String[]{userId+""});
        db.close();
    }


}
