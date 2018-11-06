package com.tangxiaopeng.videoeditdemo.utils;

import android.media.MediaPlayer;
import android.util.Log;

/**
 *  @dec  Created
 *  @author fanqie
 *  @date  2018/10/16 14:32
 */
public class UPlayer implements IVoiceManager {

    private final String TAG = "UPlayer";

    private MediaPlayer mPlayer;

    public UPlayer() {

    }

    public interface OnCompletionListener {
        void onCompletion();
    }

    private OnCompletionListener completionListener;

    public void setCompletionListener(OnCompletionListener completionListener) {
        this.completionListener = completionListener;
    }

    @Override
    public boolean start(String path, final int seek) {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        } else {
            mPlayer.reset();
        }
        if (mPlayer.isPlaying()) {
            stop();
        }
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d("tag", "播放完毕");
                //根据需要添加自己的代码。。。
                if (completionListener != null) {
                    completionListener.onCompletion();
                }
            }
        });

        try {
            //设置要播放的文件
            mPlayer.setDataSource(path);
            //防止ANR
            mPlayer.prepareAsync();

            MyLog.i(TAG,"seek="+seek);
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    mPlayer.seekTo(seek);
                }
            });
            mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    Log.d(TAG, "OnError - Error code: " + i + " Extra code: " + i1);
                    switch (i) {
                        case -1004:
                            Log.d(TAG, "MEDIA_ERROR_IO");
                            break;
                        case -1007:
                            Log.d(TAG, "MEDIA_ERROR_MALFORMED");
                            break;
                        case 200:
                            Log.d(TAG, "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK");
                            break;
                        case 100:
                            Log.d(TAG, "MEDIA_ERROR_SERVER_DIED");
                            break;
                        case -110:
                            Log.d(TAG, "MEDIA_ERROR_TIMED_OUT");
                            break;
                        case 1:
                            Log.d(TAG, "MEDIA_ERROR_UNKNOWN");
                            break;
                        case -1010:
                            Log.d(TAG, "MEDIA_ERROR_UNSUPPORTED");
                            break;
                        default:
                            break;
                    }
                    switch (i1) {
                        case 800:
                            Log.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING");
                            break;
                        case 702:
                            Log.d(TAG, "MEDIA_INFO_BUFFERING_END");
                            break;
                        case 701:
                            Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE");
                            break;
                        case 802:
                            Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE");
                            break;
                        case 801:
                            Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE");
                            break;
                        case 1:
                            Log.d(TAG, "MEDIA_INFO_UNKNOWN");
                            break;
                        case 3:
                            Log.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START");
                            break;
                        case 700:
                            Log.d(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING");
                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "prepare() failed");
        }
        return false;
    }

    public String duration(String path) {
        MediaPlayer player = new MediaPlayer();
        try {
            //设置要播放的文件
            player.setDataSource(path);
            player.prepare();
        } catch (Exception e) {
            Log.e(TAG, "prepare() failed");
            return "0";
        }
        return String.valueOf(player.getDuration() );
    }

    @Override
    public String stop() {
        if (mPlayer == null) {
            return "";
        }
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
        return null;
    }

}
