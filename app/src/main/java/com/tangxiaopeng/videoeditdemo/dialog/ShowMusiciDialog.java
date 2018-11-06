package com.tangxiaopeng.videoeditdemo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;

import com.tangxiaopeng.videoeditdemo.R;
import com.tangxiaopeng.videoeditdemo.utils.MyLog;
import com.tangxiaopeng.videoeditdemo.utils.UPlayer;

/**
 * @author fanqie
 * @date 2018/6/4 10:43
 */
public class ShowMusiciDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = "ShowMusiciDialog";

    Chronometer chtTimerMusic;
    TextView tvPalyMusic;
    private Context context;
    private String url = "";

    private UPlayer mUPlayer;

    OnSureClickListener mListener;

    public ShowMusiciDialog(Context context) {
        super(context);
        this.context = context;
    }

    public ShowMusiciDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    public ShowMusiciDialog(Context context, int theme, String url, OnSureClickListener mListener) {
        super(context, theme);
        this.context = context;
        this.mListener = mListener;
        this.url = url;
    }

    public interface OnSureClickListener {
        void onHide(boolean isCancle, String picvcode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_music);
        initView();
        initData(url);
    }

    private void initView() {

        chtTimerMusic = (Chronometer) findViewById(R.id.chtTimer);
        tvPalyMusic = (TextView) findViewById(R.id.tvPalyAllMusic);

    }

    public void initShowData(String url) {
        initData(url);
    }

    private void initData(String url) {
        mUPlayer = new UPlayer();
        mUPlayer.start(url,0);
        MyLog.i(TAG,"url="+url);
        mUPlayer.setCompletionListener(new UPlayer.OnCompletionListener() {
            @Override
            public void onCompletion() {
                MyLog.i(TAG,"setCompletionListener");
                mUPlayer.stop();
                mListener.onHide(true,"");
            }
        });
        chtTimerMusic.setBase(SystemClock.elapsedRealtime());
        //计时器清零
        chtTimerMusic.start();
    }


    @Override
    public void onClick(View v) {

    }



    @Override
    protected void onStop() {
        super.onStop();
        mUPlayer.stop();
        chtTimerMusic.stop();
    }
}