package com.jijc.guessplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Description:保存历史数据（包括关卡、积分）
 * Created by jijc on 2016/8/17.
 * PackageName: com.jijc.guessplayer.utils
 */
public class ShareUtil {
    private static final String FILENAME="guessplayer_jijc_v1";

    public static void saveIntegerCommon(Context context, String key, int value) {
        SharedPreferences preferences =context.getSharedPreferences(FILENAME,
                Context.MODE_PRIVATE);
        preferences.edit().putInt(key, value).apply();
    }

    public static int getIntegerCommon(Context context,String key) {
        SharedPreferences preferences =context.getSharedPreferences(FILENAME,
                Context.MODE_PRIVATE);
        return preferences.getInt(key, -1);
    }

    public static int getIntegerCommon(Context context, String key,  int defaultValue) {
        SharedPreferences preferences =context.getSharedPreferences(FILENAME,
                Context.MODE_PRIVATE);
        return preferences.getInt(key, defaultValue);
    }

    public static void saveBooleanCommon(Context context,String key, boolean value) {
        SharedPreferences preferences =context.getSharedPreferences(FILENAME,
                Context.MODE_PRIVATE);
        preferences.edit().putBoolean(key, value).apply();
    }

    public static boolean getBooleanCommon(Context context,String key) {
        SharedPreferences preferences =context.getSharedPreferences(FILENAME,
                Context.MODE_PRIVATE);

        return preferences.getBoolean(key, false);
    }

    public static boolean getBooleanCommon(Context context,String key, boolean defaultValue) {
        SharedPreferences preferences =context.getSharedPreferences(FILENAME,
                Context.MODE_PRIVATE);

        return preferences.getBoolean(key, defaultValue);
    }
}
