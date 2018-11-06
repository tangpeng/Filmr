package com.tangxiaopeng.videoeditdemo.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class CommonUtil {
    private static final String TAG = "COMMONUTIL";

    /**
     * 显示或者隐藏输入法
     */
    public static void hideOrOpenSoftInput(Context context) {
        InputMethodManager imm = (InputMethodManager) context
                .getApplicationContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        // 显示或者隐藏输入法
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * // 隐藏焦点把
     */
    public static void hideSoftInput(Context context, EditText edittext) {
        ((InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE)).
                hideSoftInputFromWindow(edittext.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * // 获取焦点
     */
    public static void showSoftInput(Context context, EditText edittext) {
        ((InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(
                edittext, InputMethodManager.SHOW_FORCED);
    }

    //隐藏虚拟键盘
    public static void HideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
    }

    //显示虚拟键盘
    public static void ShowKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);

    }




}
