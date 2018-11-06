package com.tangxiaopeng.videoeditdemo.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;

import java.io.File;

public class BaseViewUtils {

    /**
     * 获取屏幕宽度
     *
     * @param activity
     * @return
     */
    public static int getWindowsWidth(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static int getMinItemWidth(Activity activity) {
        return (int) (getWindowsWidth(activity) * 0.1f);
    }

    public static int getMaxItemWidth(Activity activity) {
        return (int) (getWindowsWidth(activity) * 0.7f);
    }

    /**
     * 获取屏幕高度
     *
     * @param activity
     * @return
     */
    public static int getWindowsHeight(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * 显示或者隐藏软键盘
     *
     * @param activity
     */
    public static void showOrHideSoftInput(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 强制隐藏软键盘
     */
    public static void hideSoftInput(Activity activity, View editText) {
        InputMethodManager imm = ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE));
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);//InputMethodManager.HIDE_NOT_ALWAYS
        }
    }

    /**
     * 强制显示软键盘
     */
    public static void showSoftInput(Activity activity, View editText) {
        editText.requestFocus();
        InputMethodManager imm = ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE));
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 重新设置listView的高度
     * ScrollView中嵌套ListView 需要重新设置ListView高度,否则显示不全
     * 内部TextView数据多行时可行
     *
     * @param listView
     */
    public static void setListViewHeightOnScrollView(ListView listView) {
        if (listView == null) {
            return;
        }

        BaseAdapter listAdapter = (BaseAdapter) listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.AT_MOST);
            listItem.measure(desiredWidth, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * 重新设置GridView的高度
     *
     * @param gridView
     */
    @SuppressLint("NewApi")
    public static void setGridViewHeightBasedOnChildren(Context context, GridView gridView) {
        if (gridView == null) {
            return;
        }

        BaseAdapter listAdapter = (BaseAdapter) gridView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int count = listAdapter.getCount();        //总数据条数
        int columns =/*gridView.getNumColumns()*/2;            //总列数
        int rownum = count / columns + (count % columns > 0 ? 1 : 0);    //总行数
        for (int i = 0; i < rownum; i++) {
            View listItem = listAdapter.getView(i, null, gridView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight() + dip2px(context, 10);//item高度45dp+gridView的行高10dp
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);
    }

    /**
     * 重新设置listView的高度  评论列表
     * ListView中嵌套ListView
     * 前提是ListView中可变的TextView需要自定义TextView并重写onMeasure方法
     *
     * @param listView
     */
    public static void setListViewHeightOnListView(ListView listView) {
        if (listView == null) {
            return;
        }

        BaseAdapter listAdapter = (BaseAdapter) listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static String getFileSavePath(Context context) {
        String path;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            //有SD卡
            path = Environment.getExternalStorageDirectory()
                    + File.separator
                    + "ImageCache"
                    + File.separator
                    + "qingchundao"
                    + File.separator;
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        } else {
            //无SD卡
            path = context.getFilesDir().getAbsolutePath() + File.separator;
        }
        return path;
    }
//
//    public static void setIndicator(TabLayout tabs, int leftDip, int rightDip) {
//        Class<?> tabLayout = tabs.getClass();
//        Field tabStrip = null;
//        try {
//            tabStrip = tabLayout.getDeclaredField("mTabStrip");
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//
//        assert tabStrip != null;
//        tabStrip.setAccessible(true);
//        LinearLayout llTab = null;
//        try {
//            llTab = (LinearLayout) tabStrip.get(tabs);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//
//        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
//        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());
//
//        Log.d("print", "setIndicator: " + llTab.getChildCount());
//        for (int i = 0; i < llTab.getChildCount(); i++) {
//            View child = llTab.getChildAt(i);
//            child.setPadding(0, 0, 0, 0);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
//            params.leftMargin = left;
//            params.rightMargin = right;
//            child.setLayoutParams(params);
//            child.invalidate();
//        }
//    }

    public static int getStatusBarHeight(Activity activity) {
        /**
         * 获取状态栏高度——方法3
         * 应用区的顶端位置即状态栏的高度
         * *注意*该方法不能在初始化的时候用
         * */
        Rect rectangle = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);
        return rectangle.top;
    }

    /**
     * 获取底部导航栏高度
     *
     * @param act
     * @return
     */
    private static int getNavigationBarHeight(Context act) {
        Resources resources = act.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }
}
