package com.tangxiaopeng.videoeditdemo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.qiniu.pili.droid.shortvideo.PLBuiltinFilter;
import com.qiniu.pili.droid.shortvideo.PLImageView;
import com.qiniu.pili.droid.shortvideo.PLMediaFile;
import com.qiniu.pili.droid.shortvideo.PLPaintView;
import com.qiniu.pili.droid.shortvideo.PLShortVideoEditor;
import com.qiniu.pili.droid.shortvideo.PLTextView;
import com.qiniu.pili.droid.shortvideo.PLVideoPlayerListener;
import com.qiniu.pili.droid.shortvideo.PLVideoSaveListener;
import com.tangxiaopeng.videoeditdemo.bean.EditChartbean;
import com.tangxiaopeng.videoeditdemo.bean.EditTextbean;
import com.tangxiaopeng.videoeditdemo.fragment.BekidChartFragment;
import com.tangxiaopeng.videoeditdemo.fragment.BekidFilterFragment;
import com.tangxiaopeng.videoeditdemo.fragment.BekidListFragment;
import com.tangxiaopeng.videoeditdemo.fragment.BekidMusicFragment;
import com.tangxiaopeng.videoeditdemo.fragment.BekidPanelFragment;
import com.tangxiaopeng.videoeditdemo.fragment.BekidSpeedFragment;
import com.tangxiaopeng.videoeditdemo.fragment.BekidSwitchFragment;
import com.tangxiaopeng.videoeditdemo.fragment.BekidTextFragment;
import com.tangxiaopeng.videoeditdemo.fragment.BekidVoiceFragment;
import com.tangxiaopeng.videoeditdemo.fragment.BekidZoomFragment;
import com.tangxiaopeng.videoeditdemo.fragment.FragmentVideo;
import com.tangxiaopeng.videoeditdemo.manager.FragmentTabManager;
import com.tangxiaopeng.videoeditdemo.mvpview.MvpUserActivityView;
import com.tangxiaopeng.videoeditdemo.presenter.impl.UserPresenter;
import com.tangxiaopeng.videoeditdemo.utils.MyLog;
import com.tangxiaopeng.videoeditdemo.utils.ToastUtils;
import com.tangxiaopeng.videoeditdemo.utils.Tools;
import com.tangxiaopeng.videoeditdemo.utils.UPlayer;
import com.tangxiaopeng.videoeditdemo.utils.UnitConversionTool;
import com.tangxiaopeng.videoeditdemo.view.CustomProgressDialog;
import com.tangxiaopeng.videoeditdemo.view.PaintSelectorPanel;
import com.tangxiaopeng.videoeditdemo.view.StrokedTextView;
import com.tangxiaopeng.videoeditdemo.view.TextSelectorPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.tangxiaopeng.videoeditdemo.fragment.BekidChartFragment.mDataChartList;
import static com.tangxiaopeng.videoeditdemo.fragment.BekidListFragment.mDataVideoList;
import static com.tangxiaopeng.videoeditdemo.fragment.BekidMusicFragment.mDataMusicList;
import static com.tangxiaopeng.videoeditdemo.fragment.BekidTextFragment.mDataTextList;
import static com.tangxiaopeng.videoeditdemo.fragment.BekidVoiceFragment.mDataListVoice;
import static com.tangxiaopeng.videoeditdemo.utils.Tools.MathSwitch;

/**
 * @author fanqie
 * @dec 1，七牛裁切使用的是GLSurfaceView。而他在acivitiy中只能存在一个，并且需要渲染器，所以后面决定了使用fragment，在fragment里面渲染，后面再把PLShortVideoEditor传人到mianActivity
 * 2.切换fragment的时候，需要 mShortVideoEditor.stopPlayback(); 停止视频播放，不然切换会有声音继续
 * 3.需求要求连续播放不同视频，不能使用多个播放器，影响性能，但是可是使用fragment各自有各自的生命周期
 * 4.因为视频有裁切，合成是在最后执行，如果需要判断获取播放的时间点，以及裁切起始结束都应该使用秒为单位，防止偏移
 * 5.需要注意的地方，activty里面添加fragment，他们的生命周期是分开的，并行，并不会说执行了fragment之后再继续往下执行
 * 6.如果有多个状态需要判断，使用枚举比int整型，更加直观
 * 7.视频有多个，每一个需要需要单独判断，不能相加后再判断，比如说总时间 （int）4.5+5.2+5.8）和（int）4.8+（int）5.2+（int）5.8
 * 8.mShortVideoEditor.startPlayback();执行了之后再去执行其他添加编辑方法，七牛那边要求
 * 9.删除recyclerView一个item，再添加一个新的item，会出现旧的item的缓存,记得 mMenuRecyclerView.removeViewAt(position);
 * 10.关于在播放的时候，获取视频播放的时间，是以ms为单位还是以s单位，以ms 单位进度条会流畅很多，但是需要在指定时间点做多重判断，所以最后衡量了一下，使用了0.1秒
 * 11.视频合成的时候，需求要求是能够在指定的点插入音频，而且可以插入多段音频（需要用到ffmpeg混音）
 * @date 2018/9/5 16:24
 */
public class BekidMainActivity extends AppCompatActivity implements View.OnClickListener, MvpUserActivityView {
    private static final String TAG = "BekidMainActivity";

    private Context context = BekidMainActivity.this;

    @BindView(R.id.srlQiqiuVideoInlude)
    RelativeLayout mSrlQiqiuVideoInlude;

    @BindView(R.id.activity_main_tab_radioGroup)
    RadioGroup mActivityMainTabRadioGroup;
    @BindView(R.id.fragments_container)
    FrameLayout mFragmentsContainer;
    @BindView(R.id.back_button)
    ImageView mBackButton;
    @BindView(R.id.save_button_bekid)
    Button mSaveButton;

    @BindView(R.id.tv_qiniu_video_runtime)
    TextView mTvQiniuVideoRuntime;
    @BindView(R.id.seek_bar_id)
    SeekBar mSeekBar;
    @BindView(R.id.llSeekBarAddCut)
    LinearLayout lladdSeekView;
    @BindView(R.id.llVideoHead)//裁切的时候的视频控
            LinearLayout mLlVideoHead;

    private FragmentTabManager manager;
    List<Fragment> mFragments;
    BekidTextFragment mTextFragment;
    BekidChartFragment mChartFragment;
    BekidPanelFragment mPanelFragment;


    private View addViewtopFilmr;


    private long getVideoMsIng = 0;//单个视频播放点
    private long delayMillis = 1;//每0.001秒执行一次 进度条
    public static long delayMillisCurrent = 1 * 100;//每0.1秒执行一次，获取当前播放的时间

    private View addSeekView;

    private int getnowVideo = 0;//当前正在播放是第几段视频
    private int getFragmentVideoPosition = 0;//播放第几个视频

    public static long mDurationMsAll = 0;//所有视频加起来的时间；毫秒计算

    /**
     * @author fanqie
     * @dec 七牛编辑视频模块
     * @date 2018/8/22 18:28
     */
    @BindView(R.id.pause_playback_filter)
    ImageButton mPausePlayback;
    @BindView(R.id.iv_playback_play)
    ImageView mIvPlaybackPlay;

    /**
     * 视频编辑的时候的控件
     */
    @BindView(R.id.srlQiqiuVideo)
    RelativeLayout mSrlQiqiuVideo;

    /**
     * 添加音乐控制器，拖动可以控制播放的起始时间
     */
    @BindView(R.id.rlAddMusicSeek)
    RelativeLayout mRlAddMusicSeek;
    private View mLlAddMusiceSeek;

    /**
     * 添加声音控制器，拖动可以控制播放的起始时间
     */
    @BindView(R.id.rlAddVoiceSeek)
    RelativeLayout rRlAddVoiceSeek;
    private View mLlAddVoiceSeek;

    /**
     * 添加文本控制器，显示文字显示的时间区域
     */
    @BindView(R.id.rlAddTextCutSeek)
    RelativeLayout mRlAddTextCutSeek;
    private View mLlAddTextSeek;


    /**
     * 添加文本控制器，显示文字显示的时间区域
     */
    @BindView(R.id.rlAddChartCutSeek)
    RelativeLayout mrlAddChartCutSeek;
    private View mLlAddChartSeek;

    /**
     * 添加标志控制器，显示标志显示的区域
     */
    @BindView(R.id.rlAddPanelCutSeek)
    RelativeLayout rlAddPanelCutSeek;
    private View mLlAddPanelSeek;

    private int getTextOrChartHeight = 24;


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_button_bekid:
//                editSave();
//                reStartPlay();
                MyLog.i(TAG, "save_button_bekid");
                presenter.tvClickTrim();
                pausePlayback();
                break;
            case R.id.srlQiqiuVideo:
                mPausePlayback.setVisibility(View.VISIBLE);
                break;
            case R.id.back_button:
                finishs();
                break;
            case R.id.pause_playback_filter:
            case R.id.iv_playback_play:

                if (mDataVideoList.size() == 0) {
                    return;
                }
                mPausePlayback.setVisibility(View.VISIBLE);

                //判断当前的视频是否正在播放
                if (mVideoPlayStatus == VideoPlayStatus.playPlay) {
                    Log.i(TAG, "由播放到暂停");
                    pausePlayback();
                    mPausePlayback.setImageResource(R.drawable.exo_controls_play);
                    //停止统计获取当前的时间
                    getCurrentHandler.removeMessages(1);
                } else {
                    mPausePlayback.setVisibility(View.GONE);

                    handler.removeMessages(1);

                    if (mVideoPlayStatus == VideoPlayStatus.reStartPlay) {
                        MyLog.i(TAG, "如果是重新播放，就默认状态");
                        getVideoMsIng = mDataVideoList.get(getnowVideo).getStartTime();
                    } else if (mVideoPlayStatus == VideoPlayStatus.pausePlay) {
                        MyLog.i(TAG, "如果是暂停后继续播放的");
                        getVideoMsIng = mShortVideoEditor.getCurrentPosition();
                    }
                    Log.i(TAG, "由暂停到播放=" + getVideoMsIng);
                    playPlayback();

                    //发送一个消息, 使seekBar , TextView和 VideoView关联
                    Message message = Message.obtain();
                    message.what = 1;
                    //当前播放的时间-裁切的时间
                    message.arg1 = (int) (mShortVideoEditor.getCurrentPosition() - mDataVideoList.get(getnowVideo).getStartTime());
                    message.arg2 = (int) mDurationMsAll;
                    handler.sendMessageDelayed(message, delayMillis);
                }
                mPausePlayback.setImageResource(mIsPlaying ? R.drawable.qa1t : R.drawable.qa1);
                mIvPlaybackPlay.setImageResource(mIsPlaying ? R.drawable.q1t : R.drawable.q1);
                mIsPlaying = !mIsPlaying;
                break;
            default:
                break;
        }
    }

    /**
     * 获取视频播放的时间
     */
    @SuppressLint("HandlerLeak")
    public Handler getCurrentHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //需要监听播放的时间点，播放下一段视频  ，这个需要按照秒来计算获取，毫秒的话，可能精确不到  ？？？？？
                    //采用四舍五入
                    int getCurrentTime = MathSwitch(mShortVideoEditor.getCurrentPosition());
                    int getEndTime = MathSwitch(mDataVideoList.get(getnowVideo).getEndTime());

                    MyLog.i("tangpeng", "getCurrentTime=" + getCurrentTime);
                    MyLog.i("tangpeng", "getEndTime=" + getEndTime);
                    if (getnowVideo < mDataVideoList.size() - 1) {
                        if (getCurrentTime + 1 >= getEndTime) {
                            //如果播放到截取的时间，播放下一个视频，算播放完成
                            getnowVideo++;
                            Log.i(TAG, "播放完成后，继续播放下一段视频=" + getnowVideo);
                            getVideoMsIng = mDataVideoList.get(getnowVideo).getStartTime();

                            MyLog.i(TAG, "后面切换的时候再补上?????????????????????????????");
//                            reIdlePlay();
                            reIdleReStartPlay();

                        } else {
                            //循环发送消息, 携带进度
                            msg = Message.obtain();
                            getCurrentHandler.removeMessages(1);
                            msg.what = 1;
                            getCurrentHandler.sendMessageDelayed(msg, delayMillisCurrent);
                        }
                    } else {
                        if (getCurrentTime + 1 >= getEndTime) {
                            MyLog.i(TAG, "播放到最后一段视频，回到第一段视频暂停");
                            //需要切换视频，通过传递位置，设置播放状态
                            reIdleReStartPlay();
                        } else {
                            //循环发送消息, 携带进度
                            msg = Message.obtain();
                            getCurrentHandler.removeMessages(1);
                            msg.what = 1;
                            getCurrentHandler.sendMessageDelayed(msg, delayMillisCurrent);
                        }
                    }

                    //实时播放音乐
                    if (mDataMusicList.size() > 0) {
                        for (int i = 0; i < mDataMusicList.size(); i++) {
                            //这里暂时是算一个视频
                            int mVideoStartTime = MathSwitch(mDataVideoList.get(0).getStartTime());
                            int voiceStartTime = MathSwitch(mDataMusicList.get(i).getStartInsertTime()) + mVideoStartTime;
                            int voiceEnd = MathSwitch(mDataMusicList.get(i).getEndTime()+mVideoStartTime);
                            MyLog.i(TAG, "voiceStartTime=" + voiceStartTime);
                            MyLog.i(TAG, "voiceEnd=" + voiceEnd);
                            MyLog.i(TAG, "getCurrentTime=" + getCurrentTime);
                            //播放声音
                            if (getCurrentTime == voiceStartTime) {
                                mUPlayerMusic.start(mDataMusicList.get(i).getMusicUrl(), (int) mDataMusicList.get(i).getStartTime());
                            } else if (getCurrentTime == voiceStartTime) {//这里需要注意，结束时间是开始播放的时间+裁切后的结束时间
                                mUPlayerMusic.stop();
                            }
                        }
                    }
                    //实时播放声音
                    if (mDataListVoice.size() > 0) {
                        for (int i = 0; i < mDataListVoice.size(); i++) {
                            //这里暂时是算一个视频
                            int mVideoStartTime = MathSwitch(mDataVideoList.get(0).getStartTime());
                            int voiceStartTime = MathSwitch(mDataListVoice.get(i).getStartInsertTime()) + mVideoStartTime;
                            int voiceEnd = MathSwitch(mDataListVoice.get(i).getEndTime()+mVideoStartTime);
                            //播放声音
                            if (getCurrentTime == voiceStartTime) {
                                mUPlayerVoice.start(mDataListVoice.get(i).getMusicUrl(), (int) mDataListVoice.get(i).getStartTime());
                            } else if (getCurrentTime == voiceStartTime + voiceEnd) {//这里需要注意，结束时间是开始播放的时间+裁切后的结束时间
                                mUPlayerVoice.stop();
                            }
                        }
                    }

                    break;
                default:
                    break;
            }
        }
    };

    /**
     * mSeekBar需要单独开启一个线程修改进度
     */
    @SuppressLint("HandlerLeak")
    public Handler seekhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //初始化，seekbar
                    mSeekBar.setMax(MathSwitch(mDurationMsAll));
                    //设置显示的时长
                    mTvQiniuVideoRuntime.setText("00:00" + " / " + Tools.getTimeZone(mDurationMsAll));
                    mSeekBar.setProgress(0);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 滚动条的进度实时更新
     */
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    int currentPosition = 0;
                    //如果进度条为-1，代表播放完成
                    if (msg.arg1 == -1) {
                        currentPosition = 0;
                    } else {
                        // 获取当前视频播放的总时长和当前播放的进度
                        currentPosition = msg.arg1;
                    }

                    int allLength = (int) mDurationMsAll;

                    long getAllTime = 0;//为了防止闪一下
                    for (int i = 0; i < getnowVideo + 1; i++) {
                        getAllTime = getAllTime + mDataVideoList.get(i).getVideoSize();
                    }
                    MyLog.i("seekbar", "滑动seekbar的时候有一个瞬间为0,或者可能会大于总进度，就会闪一下：currentPosition=" + currentPosition);
                    if (currentPosition > 0 && currentPosition <= getAllTime) {
                        //设置seekBar中的数据  因为每一秒都需要判断，所以不能用毫秒来区分了，虽然毫秒处理起来要顺畅好多??????????
                        mSeekBar.setMax(MathSwitch(allLength));
                        //设置显示的时长
                        mTvQiniuVideoRuntime.setText(Tools.getTimeZone(currentPosition) + " / " + (Tools.getTimeZone(allLength)));
                        mSeekBar.setProgress((MathSwitch(currentPosition)));
                    }

                    //循环发送消息, 携带进度
                    msg = Message.obtain();
                    if (getnowVideo == 0) {
                        if (mDataVideoList.size() > 0) {
                            msg.arg1 = (int) (mShortVideoEditor.getCurrentPosition() - mDataVideoList.get(getnowVideo).getStartTime());
                        }
                    } else {
                        long getCurrentTime = 0;
                        //这个可能会导致点点延迟，获取最新的时间进度
                        for (int i = 0; i < getnowVideo; i++) {
                            getCurrentTime = getCurrentTime + mDataVideoList.get(i).getVideoSize();
                        }
                        //左边多段视频总时间+当前的时间-裁切的初始时间
                        msg.arg1 = (int) (getCurrentTime + mShortVideoEditor.getCurrentPosition() - mDataVideoList.get(getnowVideo).getStartTime());
                    }
                    msg.arg2 = (int) mDurationMsAll;
                    msg.what = 1;
                    if (mVideoPlayStatus == VideoPlayStatus.playPlay || mVideoPlayStatus == VideoPlayStatus.resumePlay) {
                        handler.sendMessageDelayed(msg, delayMillis);
                    }
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public void MVPFail(String data) {


    }

    @Override
    public void MVPSuccess(int type, Object data) {

        /**
         * 跳转下一个页面合成视频
         */
        startActivity(new Intent(context, Main2Activity.class)
                .putExtra("getEditUrl", data.toString())
                .putExtra("mSelectedFilter", mSelectedFilter)
                .putExtra("mSpeed", mSpeed));
        finish();
    }


    private VideoPlayStatus mVideoPlayStatus = VideoPlayStatus.Idle;
    //需要切换视频的状态
    private enum VideoPlayStatus {
        Idle,//默认
        playPlay,//播放
        pausePlay,//暂停播放
        stopPlay,//停止播放
        resumePlay,//从0开始播放
        reStartPlay,//播放完了之后，回到第一段暂停
    }


//    // 视频编辑器预览状态
//    private enum PLShortVideoEditorStatus {
//        Idle,
//        Playing,
//        Paused,
//    }

    //    private PLShortVideoEditorStatus mShortVideoEditorStatus = PLShortVideoEditorStatus.Idle;
    public PLShortVideoEditor mShortVideoEditor;
    ;
    private boolean mIsPlaying = true;
    public static PLBuiltinFilter[] filterNum;//滤镜的个数
    public String mSelectedFilter = null;//选择的滤镜
    public double mSpeed = 1;//选择的速度

    private UPlayer mUPlayerMusic = new UPlayer();
    private UPlayer mUPlayerVoice = new UPlayer();
    /**
     * 文字
     */
    private PLImageView mCurImageView;
    private PLTextView mCurTextView;

    //标识
    private PLPaintView mPaintView;

    //mpv的框架,
    UserPresenter presenter;

    private CustomProgressDialog mProcessingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bekid_main);
        ButterKnife.bind(this);
        initDataMvp();
        initdeal();
        findViews();
        business();

        //默认进来必须要有一个视频
        String getOneUrl = getIntent().getStringExtra("url");
        mFragmentVideos.add(FragmentVideo.getInstance(getOneUrl));
        setDefaultFragment(0);
        MyLog.i("tangpeng", "2");
        getPermissions();

//        initShortVideoEditor(0,getOneUrl);
//        initQiniuShortVideoEditor();

    }

    private boolean isSeting = false;

    public void getShortVideoEditor(PLShortVideoEditor mShortVideoEditors) {
        if (mShortVideoEditor != null) {
            mShortVideoEditor = null;
        }
        this.mShortVideoEditor = mShortVideoEditors;
        MyLog.i(TAG, "fragment调用方法=getShortVideoEditor()");
        if (!isSeting) {
            isSeting = true;
            initQiniuShortVideoEditor();
        }
        switchPlayStatus();
    }

    private void switchPlayStatus() {
        switch (mVideoPlayStatus) {
            case Idle:
                break;
            case playPlay:
                playPlayback();
                break;
            case pausePlay:
                pausePlayback();
                break;
            case stopPlay:
                stopPlayback();

                break;
            case resumePlay:
                resumePlay();

                break;
            case reStartPlay:
                reStartPlay();

                break;
            default:
                break;
        }
    }

    public void initDataMvp() {
        presenter = new UserPresenter(this, this);
        presenter.attach(this);
        //之前忘记了，搞了好久，后来查看源代码才知道
        mProcessingDialog = new CustomProgressDialog(context);
        mProcessingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mShortVideoEditor.cancelSave();
            }

        });
    }

    private void initdeal() {

        String url = getIntent().getStringExtra("url");
        BekidListFragment mBekidNewFragment = BekidListFragment.getInstance(url);
        mTextFragment = new BekidTextFragment();
        mChartFragment = new BekidChartFragment();
        mChartFragment = new BekidChartFragment();
        mPanelFragment = new BekidPanelFragment();
        mFragments = new ArrayList<>();
        mFragments.add(mBekidNewFragment);
        mFragments.add(new BekidFilterFragment());
        mFragments.add(new BekidSpeedFragment());
        mFragments.add(new BekidMusicFragment());
        mFragments.add(new BekidZoomFragment());
        mFragments.add(mTextFragment);
        mFragments.add(new BekidVoiceFragment());
        mFragments.add(mChartFragment);
        mFragments.add(mPanelFragment);
        mFragments.add(new BekidSwitchFragment());
        MyLog.i(TAG, "mActivityMainTabRadioGroup="+mActivityMainTabRadioGroup);
        manager = new FragmentTabManager(mFragments,
                mActivityMainTabRadioGroup,
                getSupportFragmentManager(),
                R.id.fragments_container, this);
        manager.managerTab();
        MyLog.i(TAG, "setViews");
        manager.getInitCount();
        manager.setResulter(new FragmentTabManager.OnResultener() {
            @Override
            public void setInt(int position) {

                rRlAddVoiceSeek.setVisibility(View.GONE);
                mRlAddMusicSeek.setVisibility(View.GONE);
                mRlAddTextCutSeek.setVisibility(View.GONE);
                mrlAddChartCutSeek.setVisibility(View.GONE);
                if (position == 3) {
                    //代表音乐
                    mRlAddMusicSeek.setVisibility(View.VISIBLE);
                    cancelPaintView();
                } else if (position == 6) {
                    //代表音频
                    rRlAddVoiceSeek.setVisibility(View.VISIBLE);
                    cancelPaintView();
                } else if (position == 5) {
                    mRlAddTextCutSeek.setVisibility(View.VISIBLE);
                    cancelPaintView();
                } else if (position == 7) {
                    mrlAddChartCutSeek.setVisibility(View.VISIBLE);
                    cancelPaintView();
                } else if (position == 8) {
                    getPanelVisibility();
                } else {
                    cancelPaintView();
                    setPaintEnable();
                }
                MyLog.i(TAG, "position=" + position);
            }
        });
    }

    public static List<FragmentVideo> mFragmentVideos = new ArrayList<>();

    /**
     * @dec 需要播放那一段，传递那一段的值
     * @author fanqie
     * @date 2018/9/5 16:37
     */
    private void setDefaultFragment(int position) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragmentsContainerVideo, mFragmentVideos.get(position));
        transaction.commit();
        transaction.show(mFragmentVideos.get(position));
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
                MyLog.i(TAG, "onSaveVideoSuccess=" + s);
//                PlaybackActivity.start(BekidMainActivity.this, s);
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProcessingDialog.setProgress((int) (100 * v));
                    }
                });
            }
        });
        mShortVideoEditor.setVideoPlayerListener(mVideoPlayerListener);
        filterNum = mShortVideoEditor.getBuiltinFilterList();
    }

    PLVideoPlayerListener mVideoPlayerListener = new PLVideoPlayerListener() {
        @Override
        public void onCompletion() {
            mShortVideoEditor.pausePlayback();
        }
    };

    /**
     * 选择的滤镜
     */
    public void SelectedFilter(String mSelectedFilters) {
        //这种主题的滤镜会报错，所以先过滤掉
        if(mSelectedFilters!=null&&mSelectedFilters.equals("none.png")){
            this.mSelectedFilter = null;
        }else{
            this.mSelectedFilter = mSelectedFilters;
        }
        MyLog.i(TAG, "mSelectedFilter=" + mSelectedFilter);
        mShortVideoEditor.setBuiltinFilter(mSelectedFilter);
    }

    /**
     * 选择的速度
     */
    public void SelectedSpeed(double mSpeed) {
        MyLog.i(TAG, "setSpeed=" + mSpeed);
        this.mSpeed = mSpeed;
        mShortVideoEditor.setSpeed(mSpeed);
        resumePlay();
    }


    public void pausePlaybackView() {
        mPausePlayback.setImageResource(R.drawable.qa1);
        mIvPlaybackPlay.setImageResource(R.drawable.q1);
    }


    /**
     * 播放
     */
    public void playPlayback() {


//        initShortVideoEditor(getlocalVideo.get(getnowVideo).getVideoUrl());

        mIsPlaying = true;
        mPausePlayback.setImageResource(R.drawable.qa1t);
        mIvPlaybackPlay.setImageResource(R.drawable.q1t);
        Log.i(TAG, "playPlayback()-----------------------------------------------");
        Log.i(TAG, "getnowVideo=" + getnowVideo);
        Log.i(TAG, "VideoUrl=" + mDataVideoList.get(getnowVideo).getVideoUrl());
        Log.i(TAG, "getVideoMsIng---=" + getVideoMsIng);

//        mShortVideoEditor.setViewTimeline();//1.14.0终于有了这个功能  指定特效的时间范围（文字特效，贴图和涂鸦），单位为 Ms

        updateSeekBar(getnowVideo);//

        getCurrentHandler.removeMessages(1);
        Message message = Message.obtain();
        message.what = 1;
        getCurrentHandler.sendMessageDelayed(message, delayMillis);

        mShortVideoEditor.startPlayback();
        mShortVideoEditor.seekTo((int) getVideoMsIng);//设置起始播放时间
        EditVideo();

        MyLog.i(TAG, "playPlayback=getCurrentPosition()=" + mShortVideoEditor.getCurrentPosition());

        mVideoPlayStatus = VideoPlayStatus.playPlay;

    }

    /**
     * 在其他的视频编辑页面，添加一些素材需要先初始化页面
     */
    public void reIdleReStartPlay() {
        MyLog.i(TAG, "reIdleReStartPlay");
        getnowVideo = 0;
        //需要切换视频，通过传递位置，设置播放状态
        mVideoPlayStatus = VideoPlayStatus.reStartPlay;
        isReplaceFragment();

        onStopVoice();
    }

    /**
     * 总的进度条时间需要修改
     */
    public void resumeDurationMsAll() {
        mDurationMsAll = 0;
        for (int i = 0; i < mDataVideoList.size(); i++) {
            mDurationMsAll = mDurationMsAll + mDataVideoList.get(i).getVideoSize();
        }
        mTvQiniuVideoRuntime.setText("00:00" + " / " + Tools.getTimeZone(mDurationMsAll));
    }

    /**
     * 在其他的视频编辑页面，添加一些素材需要先初始化页面
     */
    public void reIdleResumePlay() {
        getnowVideo = 0;
        //需要切换视频，通过传递位置，设置播放状态
        mVideoPlayStatus = VideoPlayStatus.resumePlay;
        isReplaceFragment();

    }

    /**
     * 在其他的视频编辑页面，添加一些素材需要先初始化页面
     */
    public void reIdlePlay() {
        //需要切换视频，通过传递位置，设置播放状态
        mVideoPlayStatus = VideoPlayStatus.playPlay;
        isReplaceFragment();
    }

    /**
     * 滤镜这里只有如果播放才能起到效果
     */
    public void isPlayfilter() {
        MyLog.i(TAG, "mVideoPlayStatus=" + mVideoPlayStatus);
        if (mVideoPlayStatus != VideoPlayStatus.playPlay) {
            reIdleResumePlay();
        }
    }

    /**
     * 这里暂时就先用第一个视频，后面sdk出来了就改
     */
    private void isReplaceFragment() {
        switchPlayStatus();

//        if (getnowVideo == getFragmentVideoPosition) {
//            MyLog.i(TAG, "如果播放的是就是第一段.不需要切换fragment播放器");
//            switchPlayStatus();
//        } else {
//            getFragmentVideoPosition = getnowVideo;
//            MyLog.i(TAG, "切换fragment播放器=" + getnowVideo);
//            setDefaultFragment(getnowVideo);
//        }
    }


    /**
     * 播放完了之后，回到第一段暂停
     */
    private void reStartPlay() {
        mShortVideoEditor.pausePlayback();
        mPausePlayback.setVisibility(View.VISIBLE);

        mPausePlayback.setImageResource(R.drawable.qa1);
        mIvPlaybackPlay.setImageResource(R.drawable.q1);
        getnowVideo = 0;

        //需要切换视频
//        initShortVideoEditor(getlocalVideo.get(getnowVideo).getVideoUrl());

        if (mDataVideoList.size() > 0) {
            getVideoMsIng = mDataVideoList.get(getnowVideo).getStartTime();//从截取时间点
        } else {
            mDurationMsAll = 0;//如果没有视频，那么总时间为0
        }
        getCurrentHandler.removeMessages(1);//停止统计获取当前的时间
        MyLog.i(TAG, "reStartPlay+getVideoMsIng=" + mDurationMsAll);

        Message message = Message.obtain();
        message.what = 1;
        seekhandler.sendMessageDelayed(message, delayMillis);

    }

    /**
     * 从0开始播放
     */
    public void resumePlay() {

        mPausePlayback.setVisibility(View.VISIBLE);

        mPausePlayback.setImageResource(R.drawable.qa1);
        mIvPlaybackPlay.setImageResource(R.drawable.q1);
        getnowVideo = 0;
        getVideoMsIng = mDataVideoList.get(getnowVideo).getStartTime();//从截取时间点

        //需要切换视频
//        initShortVideoEditor(getlocalVideo.get(getnowVideo).getVideoUrl());


        handler.removeMessages(1);
        //视频开启播放后, 触发的监听器
        Message messageHandle = Message.obtain();
        messageHandle.what = 1;
        messageHandle.arg1 = 0;
        messageHandle.arg2 = (int) mDurationMsAll;
        handler.sendMessageDelayed(messageHandle, delayMillis);

        getCurrentHandler.removeMessages(1);//暂停记录播放的时间
        playPlayback();

    }

    /**
     * 停止预览
     */
    private void stopPlayback() {
        MyLog.i(TAG, "停止预览---------------------");
        mShortVideoEditor.stopPlayback();
        mVideoPlayStatus = VideoPlayStatus.stopPlay;
        mIsPlaying = false;
        mPausePlayback.setImageResource(mIsPlaying ? R.drawable.qa1t : R.drawable.qa1);
        mIvPlaybackPlay.setImageResource(mIsPlaying ? R.drawable.q1t : R.drawable.q1);

        onStopVoice();
    }

    /**
     * 暂停预览
     */
    public void pausePlayback() {
        mIsPlaying = false;
        mShortVideoEditor.pausePlayback();
        mVideoPlayStatus = VideoPlayStatus.pausePlay;
        mIsPlaying = false;
        onStopVoice();
    }

    public void onStopVoice() {
        if (mDataMusicList.size() > 0) {
            mUPlayerMusic.stop();
        }
        if (mDataListVoice.size() > 0) {
            mUPlayerVoice.stop();
        }
    }


    private void business() {
    }

    private void findViews() {
        mSrlQiqiuVideo.setOnClickListener(this);
        mBackButton.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);

        //设置滑块颜色
//        mSeekBar.getThumb().setColorFilter(Color.parseColor("#ec6a88"), PorterDuff.Mode.SRC_ATOP);

        //为VideoView设置控制器
        //设置暂停和播放的按钮
        mPausePlayback.setOnClickListener(this);
        mIvPlaybackPlay.setOnClickListener(this);


//        mPreview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                handler.removeMessages(1);
//
//                //视频开启播放后, 触发的监听器
//                Message message = Message.obtain();
//                message.what = 1;
//                if (getnowVideo == 0) {
//                    message.arg1 = 0;
//                } else {
//                    //需要从裁切后的时间点开始
//                    message.arg1 = Integer.parseInt(String.valueOf(getlocalVideo.get(getnowVideo - 1).getVideoSize()) + getlocalVideo.get(getnowVideo).getStartTime());
//                }
//                Log.i(TAG, "setOnPreparedListener+VideoSize=" + message.arg1);
//                message.arg2 = (int) mDurationMsAll;
//                handler.sendMessageDelayed(message, delayMillis);
//            }
//        });

        //设置进度条拖拽的监听
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int getProgress = (int) (progress * delayMillisCurrent);
                //判断是否是由用户拖拽发生的变化
                if (fromUser) {
                    mPausePlayback.setImageResource(R.drawable.qa1);
                    mIvPlaybackPlay.setImageResource(R.drawable.q1);

                    Log.i(TAG, "这里是进度条是按照s的，但是视频是按照ms的所以需要转换=" + getProgress);
                    //需要判断是拖动到了第几段视频了
                    for (int i = 0; i < mDataVideoList.size(); i++) {
                        if (getProgress < mDataVideoList.get(i).getVideoSize()) {
                            getnowVideo = i;//一旦小于，就代表第几段，退出
                            if (getnowVideo == 0) {
                                getVideoMsIng = getProgress + mDataVideoList.get(i).getStartTime();//播放起始时间，需要加上裁切的时间
                            } else {
                                //选择从第几段的，多少秒开始播放视频
                                getVideoMsIng = getProgress + mDataVideoList.get(i).getStartTime() - mDataVideoList.get(getnowVideo - 1).getVideoSize();//
                            }
                            MyLog.i(TAG, "reIdlePlay=拖动后需要关闭声音");

                            mShortVideoEditor.seekTo((int) getVideoMsIng);

                            if (mVideoPlayStatus == VideoPlayStatus.playPlay) {
                                pausePlayback();
                            }

                            //暂时如果拖动的话，就先暂停视频
//                            onStopVoice();
//                            reIdlePlay();
//                            if (getProgress != 0) {
//                                handler.removeMessages(1);
//                                Message message = Message.obtain();
//                                message.what = 1;
//                                message.arg1 = getProgress;
//                                message.arg2 = (int) mDurationMsAll;
//                                handler.sendMessageDelayed(message, delayMillis);
//                            }
                            return;
                        }
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

//        mFrameListView.setVideoPath(mMp4path);
//        mFrameListView.setOnVideoFrameScrollListener(new FrameListView.OnVideoFrameScrollListener() {
//            @Override
//            public void onVideoFrameScrollChanged(long timeMs) {
//                if (mShortVideoEditorStatus == VideoEditActivity.PLShortVideoEditorStatus.Playing) {
//                    pausePlayback();
//                }
//                mShortVideoEditor.seekTo((int) timeMs);
//            }
//        });

    }


    /**
     * @dec //把进度条分割成多个片段
     * @author fanqie
     * @date 2018/8/20 17:16
     */
    public void AddLocalDivisionVideo(PLMediaFile mMediaFile) {

        addSeekView = View.inflate(context, R.layout.including_seek_bar_video_cut, null);
        RelativeLayout rlCutSelectSplit = addSeekView.findViewById(R.id.rlCutSelectSplit);//选中的视频需要标志选中
        addSeekView.setLayoutParams(new LinearLayout.LayoutParams(0, MATCH_PARENT, mMediaFile.getDurationMs()));
        if (mDataVideoList.size() == 1) {
            rlCutSelectSplit.setBackgroundResource(R.color.index_color);
        }
        lladdSeekView.addView(addSeekView);

        mDurationMsAll = mDurationMsAll + mMediaFile.getDurationMs();//计算总时间
        mTvQiniuVideoRuntime.setText("00:00" + " / " + Tools.getTimeZone(mDurationMsAll));
        MyLog.i(TAG, "AddLocalDivisionVideo=" + mTvQiniuVideoRuntime.getText().toString());
    }

    /**
     * @dec 修改进度条的比例
     * mSelectPosition：选中的是那段视频
     * @author fanqie
     * @date 2018/8/20 17:20
     */
    public void updateSeekBar(int mSelectPosition) {
        //进度条记录每一个片段的比例
        lladdSeekView.removeAllViews();//先移除
        for (int i = 0; i < mDataVideoList.size(); i++) {
            Log.i(TAG, "把进度条分割成多个片段=" + mDataVideoList.get(i).getVideoSize());
            addSeekView = View.inflate(context, R.layout.including_seek_bar_video_cut, null);
            RelativeLayout rlCutSelectSplit = addSeekView.findViewById(R.id.rlCutSelectSplit);//选中的视频需要标志选中
            addSeekView.setLayoutParams(new LinearLayout.LayoutParams(0, MATCH_PARENT, mDataVideoList.get(i).getVideoSize()));
            if (mSelectPosition == i) {
                rlCutSelectSplit.setBackgroundResource(R.color.index_color);
            } else {
                rlCutSelectSplit.setBackgroundResource(R.color.transparent);
            }
            lladdSeekView.addView(addSeekView);
        }
    }


    /**
     * 修改七牛添加文字，只需要改变颜色
     *
     * @param mSelectColor
     */
    public void AddText(int mSelectColor, String getEditText) {
        MyLog.i(TAG, "mSelectColor=" + mSelectColor);
        MyLog.i(TAG, "getEditText=" + getEditText);

        mLlAddTextSeek = View.inflate(context, R.layout.include_add_text_seek, null);
        TextView tvAddTextSeek = (TextView) mLlAddTextSeek.findViewById(R.id.tvAddTextSeek);
        mRlAddTextCutSeek.addView(mLlAddTextSeek);

        getRandomColor(tvAddTextSeek);

        final StrokedTextView textView = new StrokedTextView(this);
        textView.setText(getEditText);
        textView.setTextSize(15);
        textView.setPadding(10, 10, 10, 10);
        textView.setTextColor(mSelectColor);
        textView.setGravity(Gravity.CENTER);
        mShortVideoEditor.addTextView(textView);
        showTextViewBorder(textView);

        ToastUtils.s(this, "触摸文字右下角控制缩放与旋转，双击移除。");

        EditTextbean mEditTextbean = new EditTextbean();//添加参数
        mEditTextbean.setStrokedTextView(textView);
        mEditTextbean.setStartTime(0);
        mEditTextbean.setEndTime(mDurationMsAll);
        mDataTextList.add(mEditTextbean);

        textView.setOnTouchListener(new ViewTouchListener(mDataTextList.size() - 1, textView));

        mTextFragment.initAddText(textView);
//        EditVideo();
    }

    /**
     * 删除某一段文字
     *
     * @param textView
     */
    public void DeleteCurrentText(int position, StrokedTextView textView) {
        MyLog.i(TAG, "DeleteCurrentText=" + position);
        mShortVideoEditor.removeTextView(textView);
        mRlAddTextCutSeek.removeViewAt(position);//删掉进度条显示view
    }

    /**
     * @dec 添加贴图
     * @author fanqie
     * @date 2018/9/6 18:39
     */
    public void AddCurrentChart(int imageId) {

        mLlAddChartSeek = View.inflate(context, R.layout.include_add_chart_seek, null);
        TextView tvAddChartSeek = mLlAddChartSeek.findViewById(R.id.tvAddChartSeek);
        mrlAddChartCutSeek.addView(mLlAddChartSeek);
        getRandomColor(tvAddChartSeek);

        PLImageView imageViewNew = new PLImageView(this);
        imageViewNew.setImageResource(imageId);

        mShortVideoEditor.addImageView(imageViewNew);

        showImageViewBorder(imageViewNew);

        EditChartbean mEditChartbean = new EditChartbean();//添加参数
        mEditChartbean.setImageView(imageViewNew);
        mEditChartbean.setStartTime(0);
        mEditChartbean.setIvImageId(imageId);
        mEditChartbean.setEndTime(mDurationMsAll);
        mDataChartList.add(mEditChartbean);

        imageViewNew.setOnTouchListener(new ViewTouchListener(mDataChartList.size() - 1, imageViewNew));

        mChartFragment.initAddChart(imageViewNew, imageId);

//        EditVideo();

    }

    /**
     * 删除贴图
     */
    public void DeleteCurrentChart(int position, PLImageView imageView) {
        MyLog.i(TAG, "DeleteCurrentText=" + position);
        mShortVideoEditor.removeImageView(imageView);//移除在播放器中显示的贴图
        mrlAddChartCutSeek.removeViewAt(position);//删掉进度条显示view
    }

    /**
     * @dec 添加标志
     * @author fanqie
     * @date 2018/9/6 18:39
     */
    public void AddCurrentPanel(PLPaintView mPaintView) {

    }

    /**
     * @dec 删除标志
     * @author fanqie
     * @date 2018/9/6 18:39
     */
    public void DeleteCurrentPanel(int positon, PLPaintView mPaintView) {

    }


    /**
     * @dec
     * @author fanqie
     * @date 2018/8/27 10:29
     */
    public void getPanelVisibility() {
        if (mPaintView == null) {
            mPaintView = new PLPaintView(this, mSrlQiqiuVideo.getWidth(), mSrlQiqiuVideo.getHeight());
            mShortVideoEditor.addPaintView(mPaintView);
        }
        mPaintView.setPaintEnable(true);
        MyLog.i(TAG, "mSrlQiqiuVideo.getHeight()" + mSrlQiqiuVideo.getHeight());
    }

    public void setPaintEnable() {
        if (mPaintView != null) {
            mPaintView.setPaintEnable(false);
        }
    }

    public void setPaintColor(int color) {
        mPaintView.setPaintColor(color);
    }

    public void setPaintSize(int size) {
        mPaintView.setPaintSize(size);
    }

    public void undo() {
        if (mPaintView != null) {
            mPaintView.undo();
        }
    }

    public void cancelPaintView() {
        setPaintEnable();
//        undo();
    }

    public void clear() {
        mPaintView.clear();
    }


    public void setPanelVisibility(View panel, boolean isVisible, boolean isEffect) {
        if (panel instanceof TextSelectorPanel || panel instanceof PaintSelectorPanel) {
            if (isVisible) {
                panel.setVisibility(View.VISIBLE);
            } else {
                panel.setVisibility(View.GONE);
            }
        } else {
            if (isVisible) {
            }
            panel.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }

        if (isEffect) {
            mShortVideoEditor.setPlaybackLoop(false);
        } else {
            mShortVideoEditor.setPlaybackLoop(true);
//            playPlayback();
            mIsPlaying = true;

            mPausePlayback.setImageResource(mIsPlaying ? R.drawable.qa1t : R.drawable.qa1);
            mIvPlaybackPlay.setImageResource(mIsPlaying ? R.drawable.q1t : R.drawable.q1);
        }
    }

    private void showTextViewBorder(PLTextView textView) {
        hideViewBorder();
        mCurTextView = textView;
        mCurTextView.setBackgroundResource(R.drawable.border_text_view);
    }

    private void hideViewBorder() {
        if (mCurTextView != null) {
            mCurTextView.setBackgroundResource(0);
            mCurTextView = null;
        }
        if (mCurImageView != null) {
            mCurImageView.setBackgroundResource(0);
            mCurImageView = null;
        }
    }

    private void showImageViewBorder(PLImageView imageView) {
        hideViewBorder();
        mCurImageView = imageView;
        mCurImageView.setBackgroundResource(R.drawable.border_text_view);
    }

    /**
     * @dec 在进度条中添加选取的音频起始时间
     * @author fanqie
     * @date 2018/8/29 15:27
     */
    public void addIndexMusic(final int mMusicPosition, int mSeekWidth) {
        mLlAddMusiceSeek = View.inflate(context, R.layout.include_add_music_seek, null);

        final ImageView ivAddMusicSeek = mLlAddMusiceSeek.findViewById(R.id.ivAddMusicSeek);
        final TextView tvAddMusicSeeks = mLlAddMusiceSeek.findViewById(R.id.tvAddMusicSeek);
        mRlAddMusicSeek.addView(mLlAddMusiceSeek);

        selectMusicPosition(0);//添加后，还是默认为第一个
        updateMusicSeekTime(mMusicPosition, mSeekWidth);
        getRandomColor(tvAddMusicSeeks);

        ivAddMusicSeek.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                float viewX = v.getX();
                float movedX = event.getX();
                float finalX = viewX + movedX;
                updateHandlerLeftPosition(ivAddMusicSeek, tvAddMusicSeeks, mMusicPosition, finalX);
                if (action == MotionEvent.ACTION_UP) {
                    /**
                     * 需要计算滑动到的时间点 ，父类View的宽度/子类view的margin=总时间/滑动的时间
                     */
                    int mLlAddMusiceSeekwidth = mRlAddMusicSeek.getWidth();
                    int ivAddMusicSeekWidth = (int) ivAddMusicSeek.getLeft();
                    MyLog.i(TAG, "mLlAddMusiceSeekwidth=" + mLlAddMusiceSeekwidth);
                    MyLog.i(TAG, "ivAddMusicSeekWidth=" + ivAddMusicSeekWidth);
                    long seekTime = mDurationMsAll * ivAddMusicSeekWidth / mLlAddMusiceSeekwidth;
                    MyLog.i(TAG, "总时间：mDurationMsAllTime=" + mDurationMsAll);
                    MyLog.i(TAG, "起始点：seekTime=" + seekTime);

                    mDataMusicList.get(mMusicPosition).setStartInsertTime(seekTime);//设置开始的位置

                    reIdleReStartPlay();
                }
                return true;
            }
        });

    }


    /**
     * @dec 有多段需要颜色分割
     * @author fanqie
     * @date 2018/9/18 10:37
     */

    public void getRandomColor(TextView mTextView) {
        int getRandom = new Random().nextInt(5);
        MyLog.i(TAG, "getRandom=" + getRandom);
        switch (getRandom) {
            case 0:
                mTextView.setBackgroundResource(R.color.color_5f2800);
                break;
            case 1:
                mTextView.setBackgroundResource(R.color.text11);
                break;
            case 2:
                mTextView.setBackgroundResource(R.color.text10);
                break;
            case 3:
                mTextView.setBackgroundResource(R.color.text9);
                break;
            case 4:
                mTextView.setBackgroundResource(R.color.text8);
                break;
            case 5:
                mTextView.setBackgroundResource(R.color.text7);
                break;
            case 6:
                mTextView.setBackgroundResource(R.color.text6);
                break;
            case 7:
                mTextView.setBackgroundResource(R.color.text5);
                break;
            case 8:
                mTextView.setBackgroundResource(R.color.text4);
                break;
            case 9:
                mTextView.setBackgroundResource(R.color.text3);
                break;
            default:
                mTextView.setBackgroundResource(R.color.text2);
                break;

        }
    }

    /**
     * @dec 进度条中只显示一个选中的音乐，默认为0
     * @author fanqie
     * @date 2018/9/18 10:10
     */
    public void selectMusicPosition(int position) {
        reIdleReStartPlay();
//        for (int i = 0; i < mRlAddMusicSeek.getChildCount(); i++) {
//            ImageView getIvBeforePosition = mRlAddMusicSeek.getChildAt(i).findViewById(R.id.ivAddMusicSeek);
//            if (position == i) {
//                getIvBeforePosition.setVisibility(View.VISIBLE);
//            } else {
//                getIvBeforePosition.setVisibility(View.INVISIBLE);
//            }
//        }
    }

    /**
     * @dec mSeekWidth 截取的音乐宽度，修改
     * @author fanqie  毫秒计算
     * @date 2018/8/29 17:07
     */
    public void updateMusicSeekTime(int mMusicPosition, int mGetMusicLenth) {
        MyLog.i(TAG, "mMusicePosition=" + mMusicPosition);
        MyLog.i(TAG, "mSeekWidth=" + mGetMusicLenth);
        //通过获取子view 来改变子item的宽度
        RelativeLayout getChildView = (RelativeLayout) mRlAddMusicSeek.getChildAt(mMusicPosition);
        TextView tvAddMusicSeeks = getChildView.findViewById(R.id.tvAddMusicSeek);
        ImageView ivAddMusicSeek = getChildView.findViewById(R.id.ivAddMusicSeek);

        ViewGroup.LayoutParams mParams = tvAddMusicSeeks.getLayoutParams();
        //音频在进度条中的长度=进度条总长度*当前音频的时间/视频总时间（mDurationMsAll）
        int mSeekwidth = (int) (rRlAddVoiceSeek.getWidth() * mGetMusicLenth / mDurationMsAll);
        mParams.width = mSeekwidth;
        tvAddMusicSeeks.setLayoutParams(mParams);


        //因为添加声音这一块不能够重复，所以必须知道上一段声音的起始点
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivAddMusicSeek.getLayoutParams();
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        if (mMusicPosition > 0) {
            ImageView getIvBeforePosition = mRlAddMusicSeek.getChildAt(mMusicPosition - 1).findViewById(R.id.ivAddMusicSeek);
            TextView getTvBeforePosition = mRlAddMusicSeek.getChildAt(mMusicPosition - 1).findViewById(R.id.tvAddMusicSeek);
            lp.leftMargin = getIvBeforePosition.getLeft() + getTvBeforePosition.getWidth();
        } else {
            lp.leftMargin = 0;
        }
        ivAddMusicSeek.setLayoutParams(lp);

    }

    /**
     * @dec mSeekWidth 截取的音频宽度，删除音频，不冲突
     * @author fanqie
     * @date 2018/8/29 17:07
     */
    public void deleteMusicSeekTime(int mMusicePosition) {
        MyLog.i(TAG, "mMusicePosition=" + mMusicePosition);
        mRlAddMusicSeek.removeViewAt(mMusicePosition);
    }


    /**
     * @dec 在进度条中添加选取的音频起始时间
     * @author fanqie
     * @date 2018/8/29 15:27
     */
    public void addIndexVoice(final int mVoicePosition, int mSeekWidth) {
        mLlAddVoiceSeek = View.inflate(context, R.layout.include_add_voice_seek, null);
        final ImageView ivAddVoiceSeek = mLlAddVoiceSeek.findViewById(R.id.ivAddVoiceSeek);
        final TextView tvAddVoiceSeek = mLlAddVoiceSeek.findViewById(R.id.tvAddVoiceSeek);
        rRlAddVoiceSeek.addView(mLlAddVoiceSeek);
        getRandomColor(tvAddVoiceSeek);
        selectVoicePosition(0);
        updateVoiceSeekTime(mVoicePosition, mSeekWidth);

        ivAddVoiceSeek.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                float viewX = v.getX();
                float movedX = event.getX();
                float finalX = viewX + movedX;

                updateHandlerLeftVoicePosition(ivAddVoiceSeek, tvAddVoiceSeek, mVoicePosition, finalX);


                if (action == MotionEvent.ACTION_UP) {
//                    calculateRange(mHandlerLeft, mHandlerRight, mFrameListView, mDurationMs, getVideNum, baseview);
                    /**
                     * 需要计算滑动到的时间点 ，父类View的宽度/子类view的宽度=总时间/滑动的时间
                     */
                    int mLlAddVioceSeekwidth = rRlAddVoiceSeek.getWidth();
                    int ivAddVoiceSeekWidth = (int) ivAddVoiceSeek.getLeft();
                    MyLog.i(TAG, "mLlAddVioceSeekwidth=" + mLlAddVioceSeekwidth);
                    MyLog.i(TAG, "ivAddVoiceSeekWidth=" + ivAddVoiceSeekWidth);
                    long seekTime = mDurationMsAll * ivAddVoiceSeekWidth / mLlAddVioceSeekwidth;
                    MyLog.i(TAG, "总时间：mDurationMsAllTime=" + mDurationMsAll);
                    MyLog.i(TAG, "起始点：seekTime=" + seekTime);

                    mDataListVoice.get(mVoicePosition).setStartInsertTime(seekTime);//设置开始播放的位置

                    reIdleReStartPlay();
                }
                return true;
            }

        });
    }

    /**
     * @dec 进度条中只显示一个选中的音乐，默认为0
     * @author fanqie
     * @date 2018/9/18 10:10
     */
    public void selectVoicePosition(int position) {
        reIdleReStartPlay();
//        for (int i = 0; i < rRlAddVoiceSeek.getChildCount(); i++) {
//            ImageView getIvBeforePosition = rRlAddVoiceSeek.getChildAt(i).findViewById(R.id.ivAddVoiceSeek);
//            if (position == i) {
//                getIvBeforePosition.setVisibility(View.VISIBLE);
//            } else {
//                getIvBeforePosition.setVisibility(View.INVISIBLE);
//            }
//
//        }
    }

    /**
     * 这里要求的是，音乐不能重复,左右2边都需要判断
     */
    private void updateHandlerLeftPosition(ImageView ivAddMusicSeek, TextView tvAddMusicSeeks, int position, float finalX) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivAddMusicSeek.getLayoutParams();
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        MyLog.i(TAG, "移动的x===" + finalX);
        //因为添加声音这一块不能够重复，所以必须知道上一段声音的起始点，
        if (position > 0) {
            ImageView getIvBeforePosition = mRlAddMusicSeek.getChildAt(position - 1).findViewById(R.id.ivAddMusicSeek);
            TextView getTvBeforePosition = mRlAddMusicSeek.getChildAt(position - 1).findViewById(R.id.tvAddMusicSeek);
            MyLog.i(TAG, "上一段时间长度===" + (getIvBeforePosition.getLeft() + getTvBeforePosition.getWidth()));

            if (position < mRlAddMusicSeek.getChildCount() - 1) {
                //如果不是最后一段，也不是第一段
                ImageView getIvAfterPosition = mRlAddMusicSeek.getChildAt(position + 1).findViewById(R.id.ivAddMusicSeek);
                if (finalX > getIvAfterPosition.getLeft() - tvAddMusicSeeks.getWidth()) {
                    lp.leftMargin = getIvAfterPosition.getLeft() - tvAddMusicSeeks.getWidth();

                } else if ((getIvBeforePosition.getLeft() + getTvBeforePosition.getWidth()) >= finalX) {
                    lp.leftMargin = getIvBeforePosition.getLeft() + getTvBeforePosition.getWidth();
                    MyLog.i(TAG, "111111");
                } else if (finalX < 0) {
                    lp.leftMargin = 0;
                } else {
                    MyLog.i(TAG, "222222");
                    lp.leftMargin = (int) finalX;
                }
            } else {
                //如果是最后一段
                if ((getIvBeforePosition.getLeft() + getTvBeforePosition.getWidth()) >= finalX) {
                    lp.leftMargin = getIvBeforePosition.getLeft() + getTvBeforePosition.getWidth();
                    MyLog.i(TAG, "111111");
                } else if (finalX < 0) {
                    lp.leftMargin = 0;
                } else {
                    MyLog.i(TAG, "222222");
                    lp.leftMargin = (int) finalX;
                }
            }
        } else {
            //如果是第一段 还需要判断有没有第二段
            if (mRlAddMusicSeek.getChildCount() > 1) {
                ImageView getIvAfterPosition = mRlAddMusicSeek.getChildAt(position + 1).findViewById(R.id.ivAddMusicSeek);
                if (finalX > getIvAfterPosition.getLeft() - tvAddMusicSeeks.getWidth()) {
                    lp.leftMargin = getIvAfterPosition.getLeft() - tvAddMusicSeeks.getWidth();
                } else if (finalX < 0) {
                    lp.leftMargin = 0;
                } else {
                    lp.leftMargin = (int) finalX;
                }
            } else {//如果只有第一段
                if (finalX < 0) {
                    lp.leftMargin = 0;
                } else {
                    lp.leftMargin = (int) finalX;
                }

            }

        }
        ivAddMusicSeek.setLayoutParams(lp);
    }

    /**
     * 这里要求的是，声音不能重复
     */
    private void updateHandlerLeftVoicePosition(ImageView ivAddVoiceSeek, TextView tvAddVoiceSeek, int position, float finalX) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivAddVoiceSeek.getLayoutParams();
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        MyLog.i(TAG, "移动的x===" + finalX);
        //因为添加声音这一块不能够重复，所以必须知道上一段声音的起始点，
        if (position > 0) {
            ImageView getIvBeforePosition = rRlAddVoiceSeek.getChildAt(position - 1).findViewById(R.id.ivAddVoiceSeek);
            TextView getTvBeforePosition = rRlAddVoiceSeek.getChildAt(position - 1).findViewById(R.id.tvAddVoiceSeek);
            MyLog.i(TAG, "上一段时间长度===" + (getIvBeforePosition.getLeft() + getTvBeforePosition.getWidth()));

            if (position < rRlAddVoiceSeek.getChildCount() - 1) {
                //如果不是最后一段，也不是第一段
                ImageView getIvAfterPosition = rRlAddVoiceSeek.getChildAt(position + 1).findViewById(R.id.ivAddVoiceSeek);
                if (finalX > getIvAfterPosition.getLeft() - tvAddVoiceSeek.getWidth()) {
                    lp.leftMargin = getIvAfterPosition.getLeft() - tvAddVoiceSeek.getWidth();
                    MyLog.i(TAG, "000");
                } else if ((getIvBeforePosition.getLeft() + getTvBeforePosition.getWidth()) >= finalX) {
                    lp.leftMargin = getIvBeforePosition.getLeft() + getTvBeforePosition.getWidth();
                    MyLog.i(TAG, "111111");
                } else if (finalX < 0) {
                    lp.leftMargin = 0;
                } else {
                    MyLog.i(TAG, "222222");
                    lp.leftMargin = (int) finalX;
                }
            } else {
                //如果是最后一段
                if ((getIvBeforePosition.getLeft() + getTvBeforePosition.getWidth()) >= finalX) {
                    lp.leftMargin = getIvBeforePosition.getLeft() + getTvBeforePosition.getWidth();
                    MyLog.i(TAG, "111111");
                } else if (finalX < 0) {
                    lp.leftMargin = 0;
                } else {
                    MyLog.i(TAG, "222222");
                    lp.leftMargin = (int) finalX;
                }
            }

        } else {
            //如果是第一段 还需要判断有没有第二段
            if (rRlAddVoiceSeek.getChildCount() > 1) {
                ImageView getIvAfterPosition = rRlAddVoiceSeek.getChildAt(position + 1).findViewById(R.id.ivAddVoiceSeek);
                if (finalX > getIvAfterPosition.getLeft() - tvAddVoiceSeek.getWidth()) {
                    lp.leftMargin = getIvAfterPosition.getLeft() - tvAddVoiceSeek.getWidth();
                } else if (finalX < 0) {
                    lp.leftMargin = 0;
                } else {
                    lp.leftMargin = (int) finalX;
                }
            } else {//如果只有第一段
                if (finalX < 0) {
                    lp.leftMargin = 0;
                } else {
                    lp.leftMargin = (int) finalX;
                }
            }

        }
        ivAddVoiceSeek.setLayoutParams(lp);

    }


    /**
     * @dec mSeekWidth 截取的音频宽度，修改
     * @author fanqie  mGetVoiceLenth 毫秒计算
     * @date 2018/8/29 17:07
     */
    public void updateVoiceSeekTime(int mVoicePosition, int mGetVoiceLenth) {
        //通过获取子view 来改变子item的宽度
        RelativeLayout getChildView = (RelativeLayout) rRlAddVoiceSeek.getChildAt(mVoicePosition);
        TextView tvAddVoiceSeek = getChildView.findViewById(R.id.tvAddVoiceSeek);
        ImageView ivAddVoiceSeek = getChildView.findViewById(R.id.ivAddVoiceSeek);

        ViewGroup.LayoutParams mParams = tvAddVoiceSeek.getLayoutParams();
        //音频在进度条中的长度=进度条总长度*当前音频的时间/视频总时间（mDurationMsAll）
        MyLog.i(TAG, "rRlAddVoiceSeek.getWidth()=" + rRlAddVoiceSeek.getWidth());
        MyLog.i(TAG, "mDurationMsAll=" + mDurationMsAll);
        MyLog.i(TAG, "mVoicePosition=" + mVoicePosition);
        MyLog.i(TAG, "mGetVoiceWidth=" + mGetVoiceLenth);

        int mSeekwidth = (int) (rRlAddVoiceSeek.getWidth() * mGetVoiceLenth / mDurationMsAll);
        mParams.width = mSeekwidth;
        tvAddVoiceSeek.setLayoutParams(mParams);

        //因为添加声音这一块不能够重复，所以必须知道上一段声音的起始点
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivAddVoiceSeek.getLayoutParams();
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        if (mVoicePosition > 0) {
            TextView gettvAddVoiceSeek = rRlAddVoiceSeek.getChildAt(mVoicePosition - 1).findViewById(R.id.tvAddVoiceSeek);
            ImageView getivAddVoiceSeek = rRlAddVoiceSeek.getChildAt(mVoicePosition - 1).findViewById(R.id.ivAddVoiceSeek);
            lp.leftMargin = getivAddVoiceSeek.getLeft() + gettvAddVoiceSeek.getWidth();
        } else {
            lp.leftMargin = 0;
        }
        ivAddVoiceSeek.setLayoutParams(lp);

    }

    /**
     * @dec mSeekWidth 截取的音频宽度，删除音频，不冲突
     * @author fanqie
     * @date 2018/8/29 17:07
     */
    public void deleteVoiceSeekTime(int mVoicePosition) {
        MyLog.i(TAG, "mVoicePosition=" + mVoicePosition);
        rRlAddVoiceSeek.removeViewAt(mVoicePosition);
    }


    /**
     * @dec mSeekWidth 截取的文字宽度，修改
     * @author fanqie  毫秒计算
     * @date 2018/8/29 17:07
     */
    public void updateTextSeekTime(int Position, float startTime, float endTime) {
        MyLog.i(TAG, "Position=" + Position);
        MyLog.i(TAG, "startTime=" + startTime);
        MyLog.i(TAG, "endTime=" + endTime);
        //通过获取子view 来改变子item的宽度
        LinearLayout getChildView = (LinearLayout) mRlAddTextCutSeek.getChildAt(Position);
        TextView tvAddTextSeek = getChildView.findViewById(R.id.tvAddTextSeek);
        View viewAddTextStart = getChildView.findViewById(R.id.viewAddTextStart);
        View viewAddTextEnd = getChildView.findViewById(R.id.viewAddTextEnd);

        int hight = UnitConversionTool.dip2px(context, getTextOrChartHeight);//
        MyLog.i(TAG, "hight=" + hight);
        tvAddTextSeek.setLayoutParams(new LinearLayout.LayoutParams(0, hight, (endTime - startTime)));
        viewAddTextStart.setLayoutParams(new LinearLayout.LayoutParams(0, hight, startTime));
        viewAddTextEnd.setLayoutParams(new LinearLayout.LayoutParams(0, hight, (1 - endTime)));

    }

    /**
     * @dec mSeekWidth 截取的文字宽度，修改
     * @author fanqie  毫秒计算
     * @date 2018/8/29 17:07
     */
    public void updateChartSeekTime(int Position, float startTime, float endTime) {
        MyLog.i(TAG, "Position=" + Position);
        MyLog.i(TAG, "startTime=" + startTime);
        MyLog.i(TAG, "endTime=" + endTime);

        //通过获取子view 来改变子item的宽度
        LinearLayout getChildView = (LinearLayout) mrlAddChartCutSeek.getChildAt(Position);
        TextView tvAddChartSeek = getChildView.findViewById(R.id.tvAddChartSeek);
        View viewAddChartStart = getChildView.findViewById(R.id.viewAddChartStart);
        View viewAddChartEnd = getChildView.findViewById(R.id.viewAddChartEnd);

        int hight = UnitConversionTool.dip2px(context, getTextOrChartHeight);//
        MyLog.i(TAG, "hight=" + hight);//通过权重来设置其实位置和结束位置
        tvAddChartSeek.setLayoutParams(new LinearLayout.LayoutParams(0, hight, (endTime - startTime)));
        viewAddChartStart.setLayoutParams(new LinearLayout.LayoutParams(0, hight, startTime));
        viewAddChartEnd.setLayoutParams(new LinearLayout.LayoutParams(0, hight, (1 - endTime)));

    }

    /**
     * @dec mSeekWidth 截取标识宽度，修改
     * @author fanqie  毫秒计算
     * @date 2018/8/29 17:07
     */
    public void updatePanelSeekTime(int Position, float startTime, float endTime) {
        MyLog.i(TAG, "Position=" + Position);
        MyLog.i(TAG, "startTime=" + startTime);
        MyLog.i(TAG, "endTime=" + endTime);

        //通过获取子view 来改变子item的宽度
        LinearLayout getChildView = (LinearLayout) rlAddPanelCutSeek.getChildAt(Position);
        TextView tvAddSeek = getChildView.findViewById(R.id.tvAddSeek);
        View viewAddStart = getChildView.findViewById(R.id.viewAddStart);
        View viewAddEnd = getChildView.findViewById(R.id.viewAddEnd);

        int hight = UnitConversionTool.dip2px(context, getTextOrChartHeight);//
        MyLog.i(TAG, "hight=" + hight);//通过权重来设置其实位置和结束位置
        tvAddSeek.setLayoutParams(new LinearLayout.LayoutParams(0, hight, (endTime - startTime)));
        viewAddStart.setLayoutParams(new LinearLayout.LayoutParams(0, hight, startTime));
        viewAddEnd.setLayoutParams(new LinearLayout.LayoutParams(0, hight, (1 - endTime)));

    }


    /**
     * @author fanqie
     * @dec 文字添加手势，缩放等
     * @date 2018/8/24 17:58
     */
    private class ViewTouchListener implements View.OnTouchListener {
        private float lastTouchRawX;
        private float lastTouchRawY;
        private boolean scale;
        private boolean isViewMoved;
        private View mView;
        private int position;

        public ViewTouchListener(int position, View view) {
            mView = view;
            this.position = position;
        }

        GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (mView instanceof PLTextView) {
//                    mAddtextView.remove(mView);
//                    mShortVideoEditor.removeTextView((PLTextView) mView);

                    if (mCurTextView != null) {
                        mCurTextView = null;
                    }
                } else if (mView instanceof PLImageView) {
//                    mAddImageView.remove(mView);
//                    mShortVideoEditor.removeImageView((PLImageView) mView);
                    if (mCurImageView != null) {
                        mCurImageView = null;
                    }
                }
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (isViewMoved) {
                    return true;
                }
                if (mView instanceof PLTextView) {
                    createTextDialog((PLTextView) mView);
                }
                return true;
            }
        };
        final GestureDetector gestureDetector = new GestureDetector(context, simpleOnGestureListener);

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (gestureDetector.onTouchEvent(event)) {
                return true;
            }
            int action = event.getAction();
            float touchRawX = event.getRawX();
            float touchRawY = event.getRawY();
            float touchX = event.getX();
            float touchY = event.getY();

            if (action == MotionEvent.ACTION_DOWN) {
                boolean xOK = touchX >= v.getWidth() * 3 / 4 && touchX <= v.getWidth();
                boolean yOK = touchY >= v.getHeight() * 2 / 4 && touchY <= v.getHeight();
                scale = xOK && yOK;

                if (v instanceof PLTextView) {
                    showTextViewBorder((PLTextView) v);
                } else if (v instanceof PLImageView) {
                    showImageViewBorder((PLImageView) v);
                }
            }

            if (action == MotionEvent.ACTION_MOVE) {
                float deltaRawX = touchRawX - lastTouchRawX;
                float deltaRawY = touchRawY - lastTouchRawY;
                if (scale) {
                    float centerX = v.getX() + (float) v.getWidth() / 2;
                    float centerY = v.getY() + (float) v.getHeight() / 2;
                    double angle = Math.atan2(touchRawY - centerY, touchRawX - centerX) * 180 / Math.PI;
                    v.setRotation((float) angle - 45);

                    // scale
                    float xx = (touchRawX >= centerX ? deltaRawX : -deltaRawX);
                    float yy = (touchRawY >= centerY ? deltaRawY : -deltaRawY);
                    float sf = (v.getScaleX() + xx / v.getWidth() + v.getScaleY() + yy / v.getHeight()) / 2;
                    v.setScaleX(sf);
                    v.setScaleY(sf);
                } else {
                    // translate
                    v.setTranslationX(v.getTranslationX() + deltaRawX);
                    v.setTranslationY(v.getTranslationY() + deltaRawY);
                }
                isViewMoved = true;
            }

            if (action == MotionEvent.ACTION_UP) {
                if (mView instanceof PLTextView) {
                    mDataTextList.get(position).setStrokedTextView((StrokedTextView) mView);

                } else if (mView instanceof PLImageView) {
                    mDataChartList.get(position).setImageView((PLImageView) mView);
                }
                isViewMoved = false;
            }

            lastTouchRawX = touchRawX;
            lastTouchRawY = touchRawY;
            return true;
        }
    }

    private void createTextDialog(final PLTextView textView) {
        final EditText edit = new EditText(context);
        edit.setText(textView.getText());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(edit);
        builder.setTitle("请输入文字");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textView.setText(edit.getText());
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void editSave() {

        mProcessingDialog.show();

        // 监听保存状态和结果
        mShortVideoEditor.save();

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyLog.i(TAG, "onStop");

        reIdleReStartPlay();
        handler.removeCallbacksAndMessages(null);
        getCurrentHandler.removeCallbacksAndMessages(null);//停止统计获取当前的时间

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            MyLog.i(TAG, "onKeyDown");
            finishs();
        }
        return false;
    }

    public void finishs() {
        MyLog.i(TAG, "finishs");
        mDataVideoList.clear();

        mDurationMsAll = 0;


        mDataTextList.clear();
        mDataChartList.clear();
        mDataMusicList.clear();
        mDataListVoice.clear();

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * 编辑视频
     */
    private void EditVideo() {

        // 1.14.0终于有了这个功能  指定特效的时间范围（文字特效，贴图和涂鸦），单位为 Ms
        // //这里需要修改不能直接赋值，还需要-去前面的被裁切的时间,暂时只处理（假如只有一个视频的情况）？？？？？？？？？？？
        for (int i = 0; i < mDataTextList.size(); i++) {
            MyLog.i(TAG, "mAddtextView.get(i).getStartTime()=" + mDataTextList.get(i).getStartTime());
            MyLog.i(TAG, "mAddtextView.get(i).getEndTime()=" + (mDataTextList.get(i).getEndTime() - mDataTextList.get(i).getStartTime()));
            mShortVideoEditor.setViewTimeline(mDataTextList.get(i).getStrokedTextView(), mDataTextList.get(i).getStartTime() + mDataVideoList.get(0).getStartTime(),
                    mDataTextList.get(i).getEndTime() - mDataTextList.get(i).getStartTime() + mDataVideoList.get(0).getStartTime());
        }

        for (int i = 0; i < mDataChartList.size(); i++) {
            MyLog.i(TAG, "mAddImageView.get(i).getStartTime()=" + mDataChartList.get(i).getStartTime());
            MyLog.i(TAG, "mAddImageView.get(i).getEndTime()=" + (mDataChartList.get(i).getEndTime() - mDataChartList.get(i).getStartTime() + mDataVideoList.get(0).getStartTime()));
            mShortVideoEditor.setViewTimeline(mDataChartList.get(i).getImageView(), mDataChartList.get(i).getStartTime() + mDataVideoList.get(0).getStartTime(),
                    mDataChartList.get(i).getEndTime() - mDataChartList.get(i).getStartTime() + mDataVideoList.get(0).getStartTime());
        }

        MyLog.i(TAG, "mSelectedFilter=" + mSelectedFilter);
        MyLog.i(TAG, "mSpeed=" + mSpeed);
        SelectedFilter(mSelectedFilter);
        mShortVideoEditor.setSpeed(mSpeed);

//        if (getlocalVoices.size() > 0) {
//            MyLog.i(TAG, "save=" + getlocalVoices.get(0).getMusicUrl());
//            mShortVideoEditor.setAudioMixFile(getlocalVoices.get(0).getMusicUrl());
//            mShortVideoEditor.setAudioMixFileRange(2000, 4000);
//            mShortVideoEditor.setAudioMixLooping(false);
//        }
    }

    public void getPermissions() {
        XXPermissions.with(this)
                //.constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                //.permission(Permission.SYSTEM_ALERT_WINDOW, Permission.REQUEST_INSTALL_PACKAGES) //支持请求6.0悬浮窗权限8.0请求安装权限
                .permission("android.permission.RECORD_AUDIO") //不指定权限则自动获取清单中的危险权限
                .request(new OnPermission() {

                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {

                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {

                    }
                });

    }
}
