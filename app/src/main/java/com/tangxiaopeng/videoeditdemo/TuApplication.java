package com.tangxiaopeng.videoeditdemo;

import com.android.volley.RequestQueue;
import com.bumptech.glide.request.target.ViewTarget;
import com.tangxiaopeng.videoeditdemo.utils.CrashHandler;
import com.tangxiaopeng.videoeditdemo.utils.ShareFileUtils;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkApplication;


public class TuApplication extends TuSdkApplication {

    private static final String TAG = "TuApplication";
    private static TuApplication app;

    private RequestQueue mRequestQueue;


    public static TuApplication getInstance() {
        return app;
    }

    /**
     * 应用程序创建
     */
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        TuSdk.setResourcePackageClazz(R.class);
        this.setEnableLog(true);
        /**
         *  初始化SDK，应用密钥是您的应用在 TuSDK 的唯一标识符。每个应用的包名(Bundle Identifier)、密钥、资源包(滤镜、贴纸等)三者需要匹配，否则将会报错。
         *  @param appkey 应用秘钥 (当前密钥只能用于短视频 demo ，如用户需要在自己的应用中接入，需要另外申请密钥，请联系七牛销售申请密钥)
         */
        this.initPreLoader(this.getApplicationContext(), "ce51161476868602-03-bshmr1");
        ShareFileUtils.setContext(this);

        /**
         * 当出现异常，退出app
         *  * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
         */
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());

        ViewTarget.setTagId(R.id.glide_tag);//glide和TAG有冲突
    }



}