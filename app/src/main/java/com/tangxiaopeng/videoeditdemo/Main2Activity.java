package com.tangxiaopeng.videoeditdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.qiniu.pili.droid.shortvideo.PLImageView;
import com.qiniu.pili.droid.shortvideo.PLShortVideoEditor;
import com.qiniu.pili.droid.shortvideo.PLVideoEditSetting;
import com.qiniu.pili.droid.shortvideo.PLVideoPlayerListener;
import com.qiniu.pili.droid.shortvideo.PLVideoSaveListener;
import com.tangxiaopeng.videoeditdemo.mvpview.MvpUserActivityView;
import com.tangxiaopeng.videoeditdemo.presenter.impl.UserPresenter;
import com.tangxiaopeng.videoeditdemo.utils.Config;
import com.tangxiaopeng.videoeditdemo.utils.MyLog;
import com.tangxiaopeng.videoeditdemo.utils.ToastUtils;
import com.tangxiaopeng.videoeditdemo.view.CustomProgressDialog;
import com.tangxiaopeng.videoeditdemo.view.SquareGLSurfaceView;
import com.tangxiaopeng.videoeditdemo.view.StrokedTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tangxiaopeng.videoeditdemo.fragment.BekidChartFragment.mDataChartList;
import static com.tangxiaopeng.videoeditdemo.fragment.BekidMusicFragment.mDataMusicList;
import static com.tangxiaopeng.videoeditdemo.fragment.BekidTextFragment.mDataTextList;

/**
 * @author fanqie
 * @dec 视频合成页面
 * @date 2018/8/31 16:01
 */
public class Main2Activity extends AppCompatActivity implements View.OnClickListener, MvpUserActivityView {
    private static final String TAG = "Main2Activity";
    private Context context = Main2Activity.this;

    @BindView(R.id.back_button)
    ImageView mBackButton;
    @BindView(R.id.save_button)
    Button mSaveButton;
    @BindView(R.id.pause_playback_filter)
    ImageButton mPausePlaybackFilter;
    @BindView(R.id.preview_qiniu_new_save)
    SquareGLSurfaceView mPreviewQiniuNew;
    private CustomProgressDialog mProcessingDialog;

    public PLShortVideoEditor mShortVideoEditor;
    private PLVideoEditSetting setting = new PLVideoEditSetting();

    private String mSelectedFilter ="";//选择的滤镜
    private String getEditUrl = "";
    public double mSpeed = 1;//选择的速度
    //mpv的框架,
    UserPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);

        initData();
        initShortVideoEditor(getEditUrl);

        initQiniuShortVideoEditor();
        EditVideo();

        mShortVideoEditor.startPlayback();

    }

    private void initData() {

        mSelectedFilter = getIntent().getStringExtra("mSelectedFilter")+"";
        getEditUrl = getIntent().getStringExtra("getEditUrl");
        mSpeed = getIntent().getIntExtra("mSpeed", 1);

        MyLog.i(TAG, "getEditUrl=" + getEditUrl);
        MyLog.i(TAG, "mSelectedFilter=" + mSelectedFilter);
        MyLog.i(TAG, "mSpeed=" + mSpeed);

        presenter = new UserPresenter(this, this);
        presenter.attach(this);

        mProcessingDialog = new CustomProgressDialog(context);
        mProcessingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mShortVideoEditor.cancelSave();
            }

        });
    }


    /**
     * @dec 视频编辑页面的数据初始化, 可能会多次用到
     * @author fanqie
     * @date 2018/8/28 16:28
     */
    private void initShortVideoEditor(String mMp4path) {
        MyLog.i(TAG, "editing file: " + mMp4path);
        setting.setSourceFilepath(mMp4path);
        // 视频源文件路径
        setting.setDestFilepath(Config.EDITED_FILE_PATH);
        // 编辑保存后，是否保留源文件
        setting.setKeepOriginFile(true);
        // 编辑后保存的目标文件路径
        mShortVideoEditor = new PLShortVideoEditor(mPreviewQiniuNew, setting);

    }

    /**
     * @dec 参数配置
     * @author fanqie
     * @date 2018/8/30 18:29
     */
    void initQiniuShortVideoEditor() {
        mShortVideoEditor.setVideoSaveListener(new PLVideoSaveListener() {
            @Override
            public void onSaveVideoSuccess(String s) {
                mProcessingDialog.dismiss();
//                PlaybackActivity.start(Main2Activity.this, s);
                MyLog.i(TAG, "参数配置=onSaveVideoSuccess=" + s);
            }

            @Override
            public void onSaveVideoFailed(final int i) {
                MyLog.e(TAG, "save edit failed errorCode:" + i);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProcessingDialog.dismiss();
                        ToastUtils.toastErrorCode(context, i);
                    }
                });
            }

            @Override
            public void onSaveVideoCanceled() {
                mProcessingDialog.dismiss();
                MyLog.e(TAG, "onSaveVideoCanceled");
            }

            @Override
            public void onProgressUpdate(final float v) {
                MyLog.i(TAG, "(int) (100 * v)=" + (int) (100 * v));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProcessingDialog.setProgress((int) (100 * v));
                    }
                });
            }
        });
        mShortVideoEditor.setVideoPlayerListener(mVideoPlayerListener);

    }

    private void editSave() {
        mProcessingDialog.show();
        mShortVideoEditor.stopPlayback();
        // 监听保存状态和结果
        mShortVideoEditor.save();
    }

    PLVideoPlayerListener mVideoPlayerListener = new PLVideoPlayerListener() {
        @Override
        public void onCompletion() {
            mShortVideoEditor.pausePlayback();
        }
    };

    @OnClick({R.id.save_button, R.id.pause_playback_filter})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.save_button:
                mShortVideoEditor.stopPlayback();
                editSave();
                break;
            case R.id.pause_playback_filter:
                mShortVideoEditor.startPlayback();
                EditVideo();
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void MVPFail(String data) {

    }

    @Override
    public void MVPSuccess(int type, Object data) {

    }


    /**
     * 编辑视频
     */
    private void EditVideo() {

        // 1.14.0终于有了这个功能  指定特效的时间范围（文字特效，贴图和涂鸦），单位为 Ms
        for (int i = 0; i < mDataTextList.size(); i++) {
            addTextView(i);
        }
        for (int i = 0; i < mDataChartList.size(); i++) {
            addImageView(i);
        }

        /**
         * 这里暂时就放第一个音频
         */
//        addVoice(0);

        MyLog.i(TAG, "mSelectedFilter=" + mSelectedFilter+"111");
        MyLog.i(TAG, "mSpeed=" + mSpeed);
        if (mSelectedFilter.equals("null") ||mSelectedFilter.equals("")||mSelectedFilter.equals(null)) {
            mShortVideoEditor.setBuiltinFilter(null);
        }else{
            mShortVideoEditor.setBuiltinFilter(mSelectedFilter);
        }
        mShortVideoEditor.setSpeed(mSpeed);
    }

    int mSelectcolor = Color.WHITE;

    private void addTextView(int position) {

        StrokedTextView textView = new StrokedTextView(this);
        textView.setText(mDataTextList.get(position).getStrokedTextView().getText().toString());
        textView.setTextSize(15);
        textView.setPadding(10, 10, 10, 10);
        textView.setTextColor(mDataTextList.get(position).getStrokedTextView().getCurrentTextColor());
        textView.setGravity(Gravity.CENTER);
        textView.setRotation(mDataTextList.get(position).getStrokedTextView().getRotation());
        textView.setScaleX(mDataTextList.get(position).getStrokedTextView().getScaleX());
        textView.setScaleY(mDataTextList.get(position).getStrokedTextView().getScaleY());
        textView.setTranslationX(mDataTextList.get(position).getStrokedTextView().getTranslationX());
        textView.setTranslationY(mDataTextList.get(position).getStrokedTextView().getTranslationY());

        MyLog.i(TAG, "getText=" + mDataTextList.get(position).getStrokedTextView().getText().toString());
        MyLog.i(TAG, "getTextColors=" + mDataTextList.get(position).getStrokedTextView().getCurrentTextColor());
        MyLog.i(TAG, "toString=" + mDataTextList.get(position).getStrokedTextView().getTranslationX());
        mShortVideoEditor.addTextView(textView);

        //这里需要修改不能直接赋值，还需要-去前面的被裁切的时间,暂时只处理（假如只有一个视频的情况）？？？？？？？？？？？
        mShortVideoEditor.setViewTimeline(mDataTextList.get(position).getStrokedTextView(), mDataTextList.get(position).getStartTime() ,
                mDataTextList.get(position).getEndTime() - mDataTextList.get(position).getStartTime());

    }

    private void addImageView(int position) {

        PLImageView imageViewNew = new PLImageView(this);
        imageViewNew.setImageResource(mDataChartList.get(position).getIvImageId());
        imageViewNew.setRotation(mDataChartList.get(position).getImageView().getRotation());
        imageViewNew.setScaleX(mDataChartList.get(position).getImageView().getScaleX());
        imageViewNew.setScaleY(mDataChartList.get(position).getImageView().getScaleY());
        imageViewNew.setTranslationX(mDataChartList.get(position).getImageView().getTranslationX());
        imageViewNew.setTranslationY(mDataChartList.get(position).getImageView().getTranslationY());
        mShortVideoEditor.addImageView(imageViewNew);

        MyLog.i(TAG,"getStartTime()="+mDataChartList.get(position).getStartTime());

        //这里需要修改不能直接赋值，还需要-去前面的被裁切的时间,暂时只处理（假如只有一个视频的情况）？？？？？？？？？？？
        mShortVideoEditor.setViewTimeline(mDataChartList.get(position).getImageView(), mDataChartList.get(position).getStartTime(),
                mDataChartList.get(position).getEndTime() - mDataChartList.get(position).getStartTime());
    }

    private void addVoice(int position) {
        if (mDataMusicList.size() > 0) {
            MyLog.i(TAG, "save=" + mDataMusicList.get(0).getMusicUrl());
            mShortVideoEditor.setAudioMixFile(mDataMusicList.get(0).getMusicUrl());
            mShortVideoEditor.setAudioMixFileRange(2000, 4000);//截取音频文件 2-4
            mShortVideoEditor.setAudioMixLooping(false);
        }
    }

    @Override
    protected void onStop() {
        mShortVideoEditor.stopPlayback();
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            MyLog.i(TAG, "onKeyDown");
            finish();
        }
        return false;
    }
}
