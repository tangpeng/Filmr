package com.tangxiaopeng.videoeditdemo.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

/**
 * ProjectName:PLDroidShortVideoDemo
 * Date:2018/7/31 14:36
 *
 * @author fanqiejiang
 */

public class MediaDecoder {
    private static final String TAG = "MediaDecoder";
    public static   int LOCAL=1;
    public  static int NETWORK=2;

    //根据url获取音视频时长，返回毫秒
    public long getDurationLong(String url,int type){
        String duration = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //如果是网络路径
            if(type == NETWORK){
                retriever.setDataSource(url,new HashMap<String, String>());
            }else if(type == LOCAL){//如果是本地路径
                retriever.setDataSource(url);
            }
            duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception ex) {
            Log.i(TAG,ex.getMessage());
            Log.i(TAG,"获取音频时长失败");
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                Log.i(TAG,ex.getMessage());
                Log.i(TAG,"释放MediaMetadataRetriever资源失败");
            }
        }
        if(!TextUtils.isEmpty(duration)){
            return Long.parseLong(duration);
        }else{
            return 0;
        }
    }

    //获取视频缩略图
    public  static  Bitmap createVideoThumbnail(String url, int type,int timeUs) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //将网络文件以及本地文件区分开来设置
            if (type == NETWORK) {
                retriever.setDataSource(url, new HashMap<String, String>());
            } else if(type == LOCAL){
                retriever.setDataSource(url);
            }
            bitmap = retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_NEXT_SYNC);
        } catch (IllegalArgumentException ex) {
            Log.i(TAG,ex.getMessage());
            Log.i(TAG, "获取视频缩略图失败");
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                Log.i(TAG,ex.getMessage());
                Log.i(TAG,"释放MediaMetadataRetriever资源失败");
            }
        }
        return bitmap;
    }
}
