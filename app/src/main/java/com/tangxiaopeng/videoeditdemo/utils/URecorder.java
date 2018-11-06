package com.tangxiaopeng.videoeditdemo.utils;

import android.media.MediaRecorder;

import com.czt.mp3recorder.MP3Recorder;

import java.io.File;
import java.io.IOException;

/**
 * Created by JY
 * Date：2017/3/25
 * Time：下午4:12
 */
public class URecorder implements IVoiceManager {

    private final String TAG = URecorder.class.getName();
    private MediaRecorder mRecorder;

    private MP3Recorder mMP3Recorder;

    private String mPath;

    public URecorder() {
        //mRecorder = new MediaRecorder();

    }

    /*
     * 开始录音
     * @return boolean
     */
    @Override
    public boolean start(String path,int seek) {
        if(mMP3Recorder==null){
            this.mPath=Constans.MENGMENGCHICKFILEPATH + Tools.getRandomFileName("_video_transcoded") + ".mp3";
//            this.mPath = FileHelper.getInstance().getCurrentVoicePath();
            mMP3Recorder = new MP3Recorder(new File(mPath));
            try {
                mMP3Recorder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /*
     * 停止录音
     * @return boolean
     */
    @Override
    public String stop() {
        if (mMP3Recorder != null) {
            mMP3Recorder.stop();
            mMP3Recorder = null;
        }
        return mPath;
    }

}
