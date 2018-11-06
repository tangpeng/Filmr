package com.tangxiaopeng.videoeditdemo.utils;

import android.util.Log;

/**
 * Created by Administrator on 2016/8/5.
 * 使用自定义Log，在发布的时候不打印数据，防止泄漏数据
 */
public class MyLog {

    public static final boolean DEBUG = true;

    //默认的TAG，建议后面加下划线
    private static final String DEFAULT_TAG = "tag_";

    //创建TAG
    public static String createTag(Object o) {
        return o == null ? createTag() : DEFAULT_TAG + o.getClass().getSimpleName();
    }

    //生成默认TAG
    public static String createTag() {
        return DEFAULT_TAG;
    }

    public static void d(String TAG, String info) {

        if (DEBUG) {
            Log.d(TAG, info);
        }
    }

    public static void v(String TAG, String info) {

        if (DEBUG) {
            Log.v(TAG, info);
        }
    }

    public static void i(String TAG, String info) {
        if (DEBUG) Log.i(TAG, info);
    }

    /**
     * 容易出错的地方，用error，可以定位到行数
     *
     * @param TAG
     * @param info
     */
    public static void e(String TAG, String info) {
        if (DEBUG) {
            StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
            Log.e(TAG, "(" + targetStackTraceElement.getFileName() + ":" + targetStackTraceElement.getLineNumber() + ")");
            Log.e(TAG, info);
        }
    }

    public static void w(String TAG, String info) {

        if (DEBUG){
            Log.w(TAG, info);
        }
    }

    //来自鸿洋大神的手笔
    private static StackTraceElement getTargetStackTraceElement() {
        // find the target invoked method
        StackTraceElement targetStackTrace = null;
        boolean shouldTrace = false;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            boolean isLogMethod = stackTraceElement.getClassName().equals(MyLog.class.getName()) || stackTraceElement.getClassName().startsWith("java.lang");
            if (shouldTrace && !isLogMethod) {
                targetStackTrace = stackTraceElement;
                break;
            }
            shouldTrace = isLogMethod;
        }
        return targetStackTrace;
    }
}
