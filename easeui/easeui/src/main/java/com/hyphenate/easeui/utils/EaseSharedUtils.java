package com.hyphenate.easeui.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class EaseSharedUtils {
    private static SharedPreferences sha;

    public static SharedPreferences getCacheShared(Context context) {
        if (sha == null) {
            sha = context.getSharedPreferences("cache", 0);
        }
        return sha;
    }

    /**
     * 群组收到消息是否允许响铃
     * false为不允许响铃，即开启了免打扰
     * uid:当前环信用户账号
     * groupId:目标群组id
     */
    public static boolean isEnableMsgRing(Context context, String uid, String groupId) {

        return getCacheShared(context).getBoolean(uid + "_" + groupId, true);

    }

    /**
     * 设置是否允许响铃，true允许
     */
    public static void setEnableMsgRing(Context context, String uid, String groupId, boolean enable) {

        SharedPreferences.Editor editor = getCacheShared(context).edit();
        editor.putBoolean(uid + "_" + groupId, enable);
        editor.apply();
    }


}

