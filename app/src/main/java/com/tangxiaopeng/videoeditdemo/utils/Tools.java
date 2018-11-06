package com.tangxiaopeng.videoeditdemo.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tangxiaopeng.videoeditdemo.bean.EditChartbean;
import com.tangxiaopeng.videoeditdemo.bean.EditTextbean;
import com.tangxiaopeng.videoeditdemo.bean.Musicbean;
import com.tangxiaopeng.videoeditdemo.bean.videobean;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;

import static com.tangxiaopeng.videoeditdemo.BekidMainActivity.delayMillisCurrent;


public class Tools {


    public static void main(String args[]) {
        int a=MathSwitch(6652);
        System.out.println("a="+a);
    }


    public static ArrayList<videobean> getlocalVideo = new ArrayList<>();//记录每一段视频的信息
    public static ArrayList<videobean> getlocalVideoDelete = new ArrayList<>();//记录每一段视频的信息，删除的时候用到

//    public static ArrayList<Musicbean> getlocalMusic = new ArrayList<>();//记录每一段音频的信息
//    public static ArrayList<Musicbean> getlocalMusicDelete = new ArrayList<>();//记录每一段音频的信息

    public static ArrayList<Musicbean> getlocalVoices = new ArrayList<>();//记录每一段音频的信息
    public static ArrayList<Musicbean> getlocalVoiceDelete = new ArrayList<>();//记录每一段音频的信息

    /**
     * @dec 添加文字
     * @author fanqie
     * @date 2018/8/24 17:58
     */
    public static ArrayList<EditTextbean> mAddtextView = new ArrayList<>();//需要将记录保存起来
    /**
     * @dec 添加贴图
     * @author fanqie
     * @date 2018/8/24 17:58
     */
    public static ArrayList<EditChartbean> mAddImageView = new ArrayList<>();//需要将记录保存起来

    private final static long minute = 60 * 1000;// 1分钟
    private final static long hour = 60 * minute;// 1小时
    private final static long day = 24 * hour;// 1天
    private final static long month = 31 * day;// 月
    private final static long year = 12 * month;// 年

    private static final String TAG = "Tools";
    private static final String NOT_LOGIN = "notlogin";
    private static Context context;
    private static Toast toast;

    public Tools(Context context) {
        this.context = context;
    }


    /**
     * 防止每一次点击,都创建一个新的,静态内部类在外面,那么activity,相当于单列模式,需要使用,context.getApplicationContext()
     *
     * @param context
     * @param msg
     */
    public static void showToast(Context context, String msg) {
        if (toast != null) {
            toast.setText(msg);
            toast.setDuration(Toast.LENGTH_SHORT);
        } else {
            toast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
        }
        if (context != null) {
            if (context instanceof Activity && !((Activity) context).isFinishing()) {
                toast.show();
            }
        }
    }

    /**
     * 显示图片，从url转换
     *
     * @param url
     * @param mImageView
     */
//    public static void setImageByUrl(String url, ImageView mImageView) {
//        ImageLoader.getInstance().displayImage(url, mImageView);
//    }

    /**
     * 使用glide加载网络数据
     *
     * @param context
     * @param url
     * @param mImageView
     */
    public static void setImageByUrlGlide(Context context, String url, ImageView mImageView, int resourceId) {
        Glide.with(context)
                .load(url)
                .asBitmap()//必须要有的，不然会导致图片显示在控件上面，位置有问题
                .placeholder(resourceId)
                .centerCrop()
//                .skipMemoryCache(true)//跳过内存缓存。
//                .diskCacheStrategy(DiskCacheStrategy.NONE)//不要在disk硬盘中缓存。
                .into(mImageView);
    }

    /**
     * 使用缩略图就好了，无需使用原图
     *
     * @param context
     * @param url
     * @param mImageView
     */
    public static void setImageThumbByUrlGlide(Context context, String url, ImageView mImageView, int resourceId) {
        //用原图的1/10作为缩略图
        Glide.with(context)
                .load(url)
                .asBitmap()//必须要有的，不然会导致图片显示在控件上面，位置有问题
                .thumbnail(0.1f)
//               .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                .placeholder(resourceId).error(resourceId)
                .into(mImageView);
    }

    /**
     * 取得当前时间
     *
     * @return
     */
    public static String getNowTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar
                .getInstance().getTime());
    }

    /**
     * 檢查是否有网络连接
     *
     * @return
     */
    public static boolean hasNetwork(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }


    /**
     * 获取设备diviceID
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        String mGetDeviceId = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return mGetDeviceId;
            }
            mGetDeviceId = tm.getDeviceId();
        } catch (RuntimeException e) {
            mGetDeviceId = "手机权限未开启，获取失败";
        }
        return mGetDeviceId;
    }

    /**
     * 获取设备的宽度 // android获取屏幕的高度和宽度用到WindowManager
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

    /**
     * 获取设备的高度// android获取屏幕的高度和宽度用到WindowManager
     *
     * @param context
     * @return
     */
    public static int getScreenHight(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        Log.v("dbw", "Status height:" + height);
        return height;
    }

    //版本名
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    //版本号
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    //获取到包名
    public static String getPackName(Context context) {
        return getPackageInfo(context).packageName;
    }


    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }

    public static String getUUID() {
        //格式bbdffdda-1e72-4d41-9ae3-77057ea4c5e3//一共36位，去掉4个“-”刚好32 作为加密的key
        String uuidStr = UUID.randomUUID().toString().replace("-", "");
//		uuidStr = uuidStr.substring(0, 8) + uuidStr.substring(9, 13)
//				+ uuidStr.substring(14, 18) + uuidStr.substring(19, 23)
//				+ uuidStr.substring(24);
        return uuidStr;
    }


    public static String getAgentsId() {
        String agemtId = "";
        return agemtId;
    }

    ;

    /**
     * 生成随机文件名：当前年月日时分秒+五位随机数
     *
     * @return
     */
    public static String getRandomFileName(String type) {
        SimpleDateFormat simpleDateFormat;
        simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String str = simpleDateFormat.format(date);
        Random random = new Random();
        int rannum = (int) (random.nextDouble() * (99999 - 10000 + 1)) + 10000;// 获取5位随机数
        return "android_" + str + rannum + type;// 当前时间
    }


    /**
     * 获取输入框的长度
     *
     * @param text
     * @return
     */
    public static int getEditTextLength(EditText text) {
        return text.getText().toString().trim().length();
    }


    /*
     * 将时间转换为时间戳
     */
    public static String StampTodate(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }

    /*
 * 将时间戳转换为时间
 */
    public static String dateToStampNormal(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long lt = Long.parseLong(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /*
     * 将时间戳转换为时间
     */
    public static String dateToStamp(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long lt = Long.parseLong(s) * 1000;
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /*
     * 将时间戳转换为时间 ,带上时分秒 暂时不用
     */
    public static String dateToStampTime(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = Long.parseLong(s) * 1000;
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }


//    public static String TimeStamp2Date(String timestampString) {
//        Long timestamp = Long.parseLong(timestampString) * 1000;
//        String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(timestamp));
//        return date;
//    }

    /**
     * 设置获取到图片的名字 时间戳+五位随机数
     *
     * @return
     */
    public static String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());// 获取当前的系统的时间
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyyMMddHHmmss");
        return "android_" + dateFormat.format(date) + (int) ((Math.random() * 9 + 1) * 10000) + ".jpg";
    }


    /**
     * 设置获取到图片的名字 时间戳+五位随机数
     *
     * @return
     */
    public static String getVideoFileName() {
        Date date = new Date(System.currentTimeMillis());// 获取当前的系统的时间
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyyMMddHHmmss");
        return "android_" + dateFormat.format(date) + (int) ((Math.random() * 9 + 1) * 10000) + ".mp4";
    }

    /**
     * 数字超过一万，保留一位有效数字
     *
     * @param d
     * @return
     */
    public static String KeepValidNumber(int d) {
        if (d < 10000) {
            return d + "";
        } else {
            DecimalFormat df = new DecimalFormat("#.0");
            return df.format(d / 10000) + "万";
        }

    }

    /**
     * h5页面如果后面 Android加1 iOS加2
     *
     * @return
     */
    public static String typeDevice() {
        return "/1";
    }

    /**
     * 是否已经验证了权限
     *
     * @param grantResults
     * @return
     */
    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }
        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    /**
     * utf-8编码
     *
     * @return
     */
    public static String toUtf8(String getString) {
        String result = null;
        try {
            result = new String(getString.getBytes("UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
//    /**
//     * utf-8编码
//     *
//     * @return
//     */
//    public static String toUtf8(String getString) {
//        try {
//            return new String(getString.getBytes("ISO-8859-1"), "utf-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            return e.getMessage();
//        }
//    }

    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null){
            data = uri.getPath();
        }
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 重命名文件
     *
     * @param oldPath 原来的文件地址
     * @param newPath 新的文件地址
     */
    public static void renameFile(String oldPath, String newPath) {
        File oleFile = new File(oldPath);
        File newFile = new File(newPath);
        //执行重命名
        oleFile.renameTo(newFile);
    }

    /**
     * @dec 将毫秒转化成秒，求四舍五入
     * @author fanqie
     * @date 2018/9/25 11:04
     */
    public static int MathRound(long CurrentPosition) {
        return Math.round((CurrentPosition / 1000));
    }

    public static int MathSwitch(long CurrentPosition) {
        return (int) (CurrentPosition/delayMillisCurrent);
    }

    /**
     * @dec Java将毫秒转成时分秒
     * @author fanqie
     * @date 2018/9/21 10:32
     */
    public static String getTimeZone(long ms) {
//        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String hms = formatter.format(ms);
        return hms;
    }

}
