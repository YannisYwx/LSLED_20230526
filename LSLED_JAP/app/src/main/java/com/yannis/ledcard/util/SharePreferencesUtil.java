package com.yannis.ledcard.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.ref.WeakReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by yannis on 2017/6/14.
 */

public class SharePreferencesUtil {
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private static Lock lock = new ReentrantLock();

    private static SharePreferencesUtil instance;

    static WeakReference<Context> wc = null;

    /**
     * 初始化
     *
     * @param context
     */
    public static void init(Context context) {
        wc = new WeakReference<>(context);
    }

    private static SharePreferencesUtil getInstance(Context context) {
        if (instance == null) {
            lock.lock();
            if (instance == null) {
                instance = new SharePreferencesUtil(context);
            }
            lock.unlock();
        }
        return instance;
    }

    private SharePreferencesUtil(Context context) {
        if (context == null && wc == null) {
            throw new NullPointerException("the context can not be null or you must init ");
        }
        if (wc == null || wc.get() == null) {
            wc = new WeakReference<>(context);
        }
        mSharedPreferences = wc.get().getSharedPreferences("UStorage", Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    /**
     * 获得int
     *
     * @param context
     * @param key
     * @return
     */
    public static int getInt(Context context, String key) {
        return getInstance(context).mSharedPreferences.getInt(key, -1);
    }

    /**
     * 获得int
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static int getInt(Context context, String key, int defValue) {
        return getInstance(context).mSharedPreferences.getInt(key, defValue);
    }

    /**
     * 存入int
     *
     * @param context
     * @param key
     * @param value
     * @return
     */
    public static boolean putInt(Context context, String key, int value) {
        return getInstance(context).mEditor.putInt(key, value).commit();
    }

    /**
     * 获得String
     *
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context, String key) {
        return getInstance(context).mSharedPreferences.getString(key, null);
    }

    /**
     * 获得String
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static String getString(Context context, String key, String defValue) {
        return getInstance(context).mSharedPreferences.getString(key, defValue);
    }

    /**
     * 获得String
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean putString(Context context, String key, String value) {
        return getInstance(context).mEditor.putString(key, value).commit();
    }

    /**
     * 获得Boolean
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean getBoolean(Context context, String key) {
        return getInstance(context).mSharedPreferences.getBoolean(key, false);
    }

    /**
     * 获得Boolean
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static boolean getBoolean(Context context, String key, boolean defValue) {
        return getInstance(context).mSharedPreferences.getBoolean(key, defValue);
    }

    /**
     * 存入Boolean
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean putBoolean(Context context, String key, boolean value) {
        return getInstance(context).mEditor.putBoolean(key, value).commit();
    }

    /**
     * 获得Long
     *
     * @param context
     * @param key
     * @return
     */
    public static long getLong(Context context, String key) {
        return getInstance(context).mSharedPreferences.getLong(key, -1L);
    }

    /**
     * 获得Long
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static long getLong(Context context, String key, long defValue) {
        return getInstance(context).mSharedPreferences.getLong(key, defValue);
    }

    /**
     * 存入Long
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean putLong(Context context, String key, long value) {
        return getInstance(context).mEditor.putLong(key, value).commit();
    }

    /**
     * 获得Float
     *
     * @param context
     * @param key
     * @return
     */
    public static float getFloat(Context context, String key) {
        return getInstance(context).mSharedPreferences.getFloat(key, 0.0f);
    }

    /**
     * 获得Float
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static float getFloat(Context context, String key, float defValue) {
        return getInstance(context).mSharedPreferences.getFloat(key, defValue);
    }

    /**
     * 存入Float
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean putFloat(Context context, String key, float value) {
        return getInstance(context).mEditor.putFloat(key, value).commit();
    }
}
