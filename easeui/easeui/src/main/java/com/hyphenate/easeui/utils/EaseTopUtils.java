package com.hyphenate.easeui.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class EaseTopUtils {
    private static SharedPreferences sha;

    public static SharedPreferences getCacheShared(Context context) {
        if (sha == null) {
            sha = context.getSharedPreferences("top", 0);
        }
        return sha;
    }

    /**
     *
     * false为不允许置顶，
     * uid:当前环信用户账号
     * groupId:目标群组id
     */
    public static boolean isEnableMsgTop(Context context, String uid, String groupId) {

        return getCacheShared(context).getBoolean(uid + "_" + groupId, false);

    }

    /**
     * 设置是否允许置顶，true允许
     */
    public static void setEnableMsgTop(Context context, String uid, String groupId, boolean enable) {

        SharedPreferences.Editor editor = getCacheShared(context).edit();
        editor.putBoolean(uid + "_" + groupId, enable);
        editor.apply();
    }

    private static boolean addNewTop;
    public static void setAddNewTop(boolean isNewAdd ){

        addNewTop=isNewAdd;

    }
    public static boolean getAddNewTop(){
        return addNewTop;
    }
}

