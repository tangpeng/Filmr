package com.tangxiaopeng.videoeditdemo.presenter.impl;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.qiniu.pili.droid.shortvideo.PLShortVideoComposer;
import com.qiniu.pili.droid.shortvideo.PLShortVideoTrimmer;
import com.qiniu.pili.droid.shortvideo.PLVideoEncodeSetting;
import com.qiniu.pili.droid.shortvideo.PLVideoSaveListener;
import com.tangxiaopeng.videoeditdemo.mvpview.MvpUserActivityView;
import com.tangxiaopeng.videoeditdemo.presenter.BasePresenter;
import com.tangxiaopeng.videoeditdemo.utils.Config;
import com.tangxiaopeng.videoeditdemo.utils.MyLog;
import com.tangxiaopeng.videoeditdemo.utils.RecordSettings;
import com.tangxiaopeng.videoeditdemo.utils.ToastUtils;
import com.tangxiaopeng.videoeditdemo.view.CustomProgressDialog;

import java.util.ArrayList;
import java.util.List;

import static com.tangxiaopeng.videoeditdemo.fragment.BekidListFragment.mDataVideoList;


/**
 * MVP,对于逻辑处理模块，应该分离出来，以及需要多次多次使用的方法，提取出来，像登陆注册使用mvp开发，就会发现MVP的高超之处
 * https://segmentfault.com/a/1190000003927200
 */
public class UserPresenter extends BasePresenter {
    private static String TAG = "USERPRESENTER";
    MvpUserActivityView activityView;

    private PLShortVideoTrimmer mShortVideoTrimmer; //视频裁切 ，视频剪辑
    private PLShortVideoComposer mShortVideoComposer;

    private CustomProgressDialog mProcessingDialog;

    private List<String> videos = new ArrayList<>();//视频裁切后的视频地址

    public UserPresenter(Context context, MvpUserActivityView activityView) {
        this.activityView = activityView;
        this.context = context;
        mShortVideoComposer = new PLShortVideoComposer(context);

        mProcessingDialog = new CustomProgressDialog(context);
        mProcessingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mShortVideoTrimmer.cancelTrim();
                mShortVideoComposer.cancelComposeVideos();
            }
        });
    }

    @Override
    public void attach(Context context) {
        super.attach(context);
    }

    /**
     * @dec 裁切拼接
     * @author fanqie
     * @date 2018/8/14 16:51
     */
    public void tvClickTrim() {

        trim(0, mDataVideoList.get(0).getVideoUrl(), mDataVideoList.get(0).getStartTime(), mDataVideoList.get(0).getEndTime());
    }

    /**
     * @dec 七牛的裁切的方法
     * @author fanqie
     * @date 2018/8/14 17:04
     */
    private void trim(int i, String url, long beginMs, long endMs) {
        mProcessingDialog.show();
        final int[] j = {i};
        String TRIM_FILE_PATH = Config.VIDEO_STORAGE_DIR + (int) ((Math.random() * 9 + 1) * 10000) + "trimmed.mp4";
        mShortVideoTrimmer = new PLShortVideoTrimmer(context, url, TRIM_FILE_PATH);

        //??????裁切可以使用快速，还是精准
        mShortVideoTrimmer.trim(beginMs, endMs, PLShortVideoTrimmer.TRIM_MODE.ACCURATE, new PLVideoSaveListener() {
            @Override
            public void onSaveVideoSuccess(String path) {
                mProcessingDialog.dismiss();
                Log.i(TAG, "trim=path=" + path);
                videos.add(path);//视频地址
                Message message = Message.obtain();
                message.what = 1;
                message.arg1 = j[0];
                getTrimHandler.sendMessageDelayed(message, 1000);
            }

            @Override
            public void onSaveVideoFailed(final int errorCode) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProcessingDialog.dismiss();
                        ToastUtils.toastErrorCode(context, errorCode);
                    }
                });
            }

            @Override
            public void onSaveVideoCanceled() {
                mProcessingDialog.dismiss();
            }

            @Override
            public void onProgressUpdate(final float percentage) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProcessingDialog.setMessage("正在裁切第" + (j[0] + 1) + "段视频...");
                        mProcessingDialog.setProgress((int) (100 * percentage));
                    }
                });
            }
        });
    }

    /**
     * 回到主线程中裁切
     */
    @SuppressLint("HandlerLeak")
    private Handler getTrimHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (msg.arg1 == mDataVideoList.size() - 1) {//如果已经裁切了最后一个
                        onClickCompose();
                    } else {
                        int i = msg.arg1 + 1;
                        trim(i, mDataVideoList.get(i).getVideoUrl(), mDataVideoList.get(i).getStartTime(), mDataVideoList.get(i).getEndTime());
                    }

                    break;
                default:
                    break;
            }
        }
    };


    /**
     * @dec 视频拼接
     * @author fanqie
     * @date 2018/8/14 18:10
     */
    public void onClickCompose() {
        if (videos.size() == 1) {
            MyLog.i(TAG, "不需要拼接视频");
            activityView.MVPSuccess(0,videos.get(0));
        } else {
            PLVideoEncodeSetting setting = new PLVideoEncodeSetting(context);
            MyLog.i(TAG, "可以修改视频的分辨率和码率");
            setting.setEncodingSizeLevel(getEncodingSizeLevel(14));
            setting.setEncodingBitrate(getEncodingBitrateLevel(6));
            if (mShortVideoComposer.composeVideos(videos, Config.COMPOSE_FILE_PATH, setting, mVideoSaveListener)) {
                mProcessingDialog.show();
            } else {
                ToastUtils.s(context, "开始拼接失败！");
            }
        }

    }

    private PLVideoSaveListener mVideoSaveListener = new PLVideoSaveListener() {
        @Override
        public void onSaveVideoSuccess(final String filepath) {
            mProcessingDialog.dismiss();
            Log.i(TAG, "filepath=" + filepath);
            activityView.MVPSuccess(0,filepath);

//            ((Activity) context).runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    onSaveEdit(filepath);
//                }
//            });
        }

        @Override
        public void onSaveVideoFailed(final int errorCode) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProcessingDialog.dismiss();
                    ToastUtils.toastErrorCode(context, errorCode);
                }
            });
        }

        @Override
        public void onSaveVideoCanceled() {
            mProcessingDialog.dismiss();
        }

        @Override
        public void onProgressUpdate(final float percentage) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProcessingDialog.setMessage("视频正在合并...");
                    mProcessingDialog.setProgress((int) (100 * percentage));
                }
            });
        }
    };

    private PLVideoEncodeSetting.VIDEO_ENCODING_SIZE_LEVEL getEncodingSizeLevel(int position) {
        return RecordSettings.ENCODING_SIZE_LEVEL_ARRAY[position];
    }

    private int getEncodingBitrateLevel(int position) {
        return RecordSettings.ENCODING_BITRATE_LEVEL_ARRAY[position];
    }


}
