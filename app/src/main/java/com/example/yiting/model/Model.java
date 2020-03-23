package com.example.yiting.model;

import android.content.Context;

/**
 * 数据模型层全局类
 */
public class Model {
    private Context mContext;
    private DbDao dbDao;//搜索记录操作数据库操作对象

    // 创建对象
    private static Model model = new Model();
    // 私有化构造
    private Model() {}

    // 获取单例对象
    public static Model getInstance(){
        return model;
    }

    // 初始化的方法
    public void init(Context context){
        mContext = context;
        dbDao = new DbDao(mContext);
    }

    public DbDao getDbDao(){
        return dbDao;
    }
}
