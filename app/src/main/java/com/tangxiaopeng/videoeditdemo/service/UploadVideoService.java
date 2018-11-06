package com.tangxiaopeng.videoeditdemo.service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.qiniu.android.common.FixedZone;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.KeyGenerator;
import com.qiniu.android.storage.UploadManager;
import com.tangxiaopeng.videoeditdemo.mvpview.MvpUserActivityView;
import com.tangxiaopeng.videoeditdemo.utils.MyLog;

import java.io.File;

/**
 * 文件上传，所有的上传
 */
public class UploadVideoService extends Service {
    private final String TAG = "UploadVideoService";
    private Context mContext;

    public static final String ACTION_UPLOAD = "action_upload";

    public static final String FILE_NAME = "file_name";

    public static final String FILE_PERCENT = "file_percent";

    private MyBinder mBinder = new MyBinder();

    private UploadManager uploadManager;

    private Intent mIntent;

    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        MyLog.i(TAG, "onCreate");
        mContext = getApplicationContext();
        mIntent = new Intent();
        handler = new Handler();

//        //<断点记录文件保存的文件夹位置>
//        String dirPath = FileHelper.getInstance().getRecorderPath();
//        Recorder recorder = null;
//        try {
//            recorder = new FileRecorder(dirPath);
//        } catch (Exception ignored) {
//        }


        //默认使用key的url_safe_base64编码字符串作为断点记录文件的文件名
        //避免记录文件冲突（特别是key指定为null时），也可自定义文件名(下方为默认实现)：
        KeyGenerator keyGen = new KeyGenerator() {
            @Override
            public String gen(String s, File file) {
                // 不必使用url_safe_base64转换，uploadManager内部会处理
                // 该返回值可替换为基于key、文件内容、上下文的其它信息生成的文件名
                return s + "_._" + new StringBuffer(file.getAbsolutePath()).reverse();
            }
        };

        Configuration config = new Configuration.Builder()
                .chunkSize(256 * 1024)  //分片上传时，每片的大小。 默认256K
                .putThreshhold(512 * 1024)  // 启用分片上传阀值。默认512K
                .connectTimeout(10) // 链接超时。默认10秒
                .responseTimeout(60) // 服务器响应超时。默认60秒
                //.recorder(recorder)  // recorder分片上传时，已上传片记录器。默认null
//                .recorder(recorder, keyGen)  // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
                .zone(FixedZone.zone0) // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
                .build();
        // 重用uploadManager。一般地，只需要创建一个uploadManager对象
        uploadManager = new UploadManager(config);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyBinder extends Binder {

        // 初始化、执行上传
        private volatile boolean isCancelled = false;
        private MvpUserActivityView activityView;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}