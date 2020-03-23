package com.example.yiting.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtils {
    public static void putString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }

    public static void putInt(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(key, value).commit();
    }

    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }


    public static String getString(Context context, String key) {
        return context.getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE).getString(key, "");
    }

    public static int getInt(Context context, String key) {
        return context.getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE).getInt(key, 1);
    }

    public static boolean getBoolean(Context context, String key) {
        return context.getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE).getBoolean(key, false);
    }
}
