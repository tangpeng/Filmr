package com.tangxiaopeng.videoeditdemo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tangxiaopeng.videoeditdemo.R;
import com.tangxiaopeng.videoeditdemo.utils.ShareFileUtils;
import com.tangxiaopeng.videoeditdemo.utils.UPlayer;

/**
 * @author fanqie
 * @date 2018/6/4 10:43
 */
public class SendPictureVeriDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = "SendPictureVeriDialog";

    Button btn;
    Button btnPlayContinuity;
    TextView text;
    private Context context;
    private int type = 1;
    //1代表文字，2代表视频 3.代表其他

    private UPlayer mUPlayer;

    OnSureClickListener mListener;

    public SendPictureVeriDialog(Context context) {
        super(context);
        this.context = context;
    }

    public SendPictureVeriDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    public SendPictureVeriDialog(Context context, int theme, int type, OnSureClickListener mListener) {
        super(context, theme);
        this.context = context;
        this.mListener = mListener;
        this.type = type;
    }

    public interface OnSureClickListener {
        void onHide(boolean isCancle, String picvcode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_send_picture_veri);
        initView();
        initData();
    }

    private void initView() {

        text = (TextView) findViewById(R.id.text);
        btn = (Button) findViewById(R.id.btn);
        btnPlayContinuity = (Button) findViewById(R.id.btnPlayContinuity);

        btn.setOnClickListener(this);
        btnPlayContinuity.setOnClickListener(this);
    }

    private void initData() {
        showPicURL(type);
        mUPlayer = new UPlayer();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                //播放声音
                break;
            case R.id.btnPlayContinuity:
                //继续播放
                mListener.onHide(true,"");
                break;
            default:

                break;
        }
    }


    /**
     * 地址绑定控件
     */
    public void showPicURL(int type) {
        switch (type) {
            case 1:
                text.setVisibility(View.VISIBLE);
                btn.setVisibility(View.GONE);
                text.setText(ShareFileUtils.getString("editText", ""));

                break;
            case 2:
                mUPlayer.start(ShareFileUtils.getString("isEditVoiceString", ""),0);
                text.setVisibility(View.GONE);
                btn.setVisibility(View.VISIBLE);
                break;
            case 3:
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mUPlayer.stop();
    }
}