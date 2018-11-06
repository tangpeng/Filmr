package com.tangxiaopeng.videoeditdemo.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

/**
 * 存储一些用户头像和基本信息
 */
public class ShareFileUtils {
    private static SharedPreferences pref;
    private static final String TAG = "SHAREFILEUTILS";

    public static void setContext(Context paramContext) {
        pref = paramContext.getSharedPreferences("sjkj_sjkjyl", Context.MODE_PRIVATE);
    }

    // 判断是否已经登录，如果说ture，则自动登录
    public static boolean getBoolean(String paramString, boolean paramBoolean) {
        return pref.getBoolean(paramString, paramBoolean);
    }

    public static int getInt(String paramString, int paramInt) {
        return pref.getInt(paramString, paramInt);
    }

    public static String getString(String paramString1, String paramString2) {
        return pref.getString(paramString1, paramString2);
    }

    public static void setBoolean(String paramString, boolean paramBoolean) {
        pref.edit().putBoolean(paramString, paramBoolean).commit();
    }

    public static void setInt(String paramString, int paramInt) {
        pref.edit().putInt(paramString, paramInt).commit();
    }

    public static void setString(String paramString1, String paramString2) {
        pref.edit().putString(paramString1, paramString2).commit();
    }

    public static float getFloat(String paramString, float paramFloat) {
        return pref.getFloat(paramString, paramFloat);
    }

    public static void setFloat(String paramString, float paramFloat) {
        pref.edit().putFloat(paramString, paramFloat).commit();
    }

    public static void clear() {
        pref.edit().clear().commit();
    }

    // 保存2维数组
    @SuppressLint("NewApi")
    public static void setArrayInt(String paramString1, int[][] paramString2) {
        Set<String> siteno = new HashSet<String>();
        if (!paramString2.equals(null)) {
            for (int i = 0; i < paramString2.length; i++) {

                siteno.add(paramString2[i][0] + "," + paramString2[i][1]);
            }
//			Log.i("tangpeng", "set_siteno" + siteno.toString());// Set<String>,这个方法是随机排序的，要注意

            pref.edit().putStringSet(paramString1, siteno).commit();

        }

    }

    // 保存2维数组
    @SuppressLint("NewApi")
    public static int[][] getArrayInt(String paramString1) {
        Set<String> siteno = new HashSet<String>();

        siteno = pref.getStringSet(paramString1, siteno);
//		Log.i("tangpeng", "getsiteno=" + siteno.toString());

        if (siteno.size() > 0) {

            int[][] paramString2 = new int[siteno.size()][2];

            String[] data = (String[]) siteno.toArray(new String[siteno.size()]); // 將SET轉換為數組

            for (int i = 0; i < data.length; i++) {

                // Unit_PublicVar.arr_DeatilContent[i] =data[i].trim().split(",");

                paramString2[Integer.parseInt(data[i].trim().split(",")[0])][0] = Integer
                        .parseInt(data[i].trim().split(",")[0]);
                paramString2[Integer.parseInt(data[i].trim().split(",")[0])][1] = Integer
                        .parseInt(data[i].trim().split(",")[1]);
            }

            return paramString2;

        }
        return null;
    }

    /**
     * 用分割字符串来保存数组，但是不能为空，所以先判断如果为空，添加一个“空”字段，取的时候再去掉
     *
     * @param key
     * @return
     */
    public static String[] getArray(String key) {
        String regularEx = "#";
        String[] str = null;
        String values;
        values = pref.getString(key, "");
        String[] newStr;

        if (values.contains(regularEx)) {//如果有包含“#”，那么则获取
            str = values.split(regularEx);
            newStr = new String[str.length];
            for (int i = 0; i < str.length; i++) {
                newStr[i] = str[i];
            }
        }else{
            newStr=new String[0];
        }

        Log.i(TAG, "values=" + values);
        Log.i(TAG, "newStr" + newStr.length);

        return newStr;
    }

    /**
     * 用分割字符串来保存数组，但是不能为空，所以先判断如果为空，添加一个“空”字段，取的时候再去掉
     *
     * @param key
     * @param values
     */
    public static void setArray(String key, String[] values) {
        String regularEx = "#";
        String str = "";
        if (values != null && values.length > 0) {
            for (int i = 0; i < values.length; i++) {
                if (values[i].equals("")) {//如果每个字段的值为空
                    values[i] = "empty";
                }
                str = str + values[i] + regularEx;
            }
//			Log.i("tangpeng", "str=" + str);
            Editor et = pref.edit();
            et.putString(key, str);
            et.commit();
        }
    }

    // 保存一维String数组，同样不能为空
    @SuppressLint("NewApi")
    public static void setArrayString(String paramString1, String[] paramString2) {
        Set<String> siteno = new HashSet<String>();
        if (!paramString2.equals("")) {
            for (int i = 0; i < paramString2.length; i++) {
                siteno.add(paramString2[i]);
            }
//			Log.i("tangpeng", "set_siteno" + siteno.toString());// Set<String>,这个方法是随机排序的，要注意
            pref.edit().putStringSet(paramString1, siteno).commit();
        }

    }

    // 获取一维String数组，同样不能为空
    @SuppressLint("NewApi")
    public static String[] getArrayString(String paramString1) {
        Set<String> siteno = new HashSet<String>();

        siteno = pref.getStringSet(paramString1, siteno);
//		Log.i("tangpeng", "getsiteno=" + siteno.toString());

        if (siteno.size() > 0) {
            String[] paramString2 = new String[siteno.size()];

            String[] data = (String[]) siteno
                    .toArray(new String[siteno.size()]); // 將SET轉換為數組

            for (int i = 0; i < data.length; i++) {
                // Unit_PublicVar.arr_DeatilContent[i] =
                // data[i].trim().split(",");
                paramString2[Integer.parseInt(data[i])] = data[i].trim();
            }
            return paramString2;

        }
        return null;
    }

}
