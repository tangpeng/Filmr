package com.tangxiaopeng.videoeditdemo.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.qiniu.pili.droid.shortvideo.PLMediaFile;
import com.qiniu.pili.droid.shortvideo.PLShortVideoComposer;
import com.qiniu.pili.droid.shortvideo.PLShortVideoTrimmer;
import com.qiniu.pili.droid.shortvideo.PLVideoEncodeSetting;
import com.qiniu.pili.droid.shortvideo.PLVideoSaveListener;
import com.tangxiaopeng.videoeditdemo.R;
import com.tangxiaopeng.videoeditdemo.bean.videobean;
import com.tangxiaopeng.videoeditdemo.utils.Config;
import com.tangxiaopeng.videoeditdemo.utils.GetPathFromUri;
import com.tangxiaopeng.videoeditdemo.utils.RecordSettings;
import com.tangxiaopeng.videoeditdemo.utils.ToastUtils;
import com.tangxiaopeng.videoeditdemo.view.CustomProgressDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;


/**
 * 已经放弃，修改布局了
 * Created by JY
 * Date：17/2/20
 * Time：下午5:01
 */
public class BekidFragment extends Fragment implements View.OnClickListener {

    public static String CURRENT_PAGE = "current_page";
    private static final String TAG = "VideoFilmrActivity";
    @BindView(R.id.preview)
    VideoView mPreview;
    @BindView(R.id.seek_bar_id)
    SeekBar mSeekBar;
    @BindView(R.id.llSeekBarAddCut)
    LinearLayout lladdSeekView;
    @BindView(R.id.startButton)
    ImageView startButton;
    @BindView(R.id.video_runtime)
    TextView mTimeText;
    @BindView(R.id.lltopFilmr)
    LinearLayout lltopFilmr;
    Unbinder unbinder;

    private static final int SLICE_COUNT = 8;
    @BindView(R.id.addVideoFromLocal)
    ImageView mAddVideoFromLocal;

    private PLShortVideoTrimmer mShortVideoTrimmer; //视频裁切 ，视频剪辑
    private PLShortVideoComposer mShortVideoComposer;

    private CustomProgressDialog mProcessingDialog;

    private View addViewtopFilmr;

    private long getVideoMsIng = 0;//单个视频播放点
    private long delayMillis = 1;//每0.001秒执行一次 进度条
    private long delayMillisCurrent = 1 * 1000;//每1秒执行一次，获取当前播放的时间

    private View addSeekView;

    private int mVideoFrameCount;
    private int mSlicesTotalLength;

    private ArrayList<videobean> getlocalVideo = new ArrayList<>();//记录每一段视频的信息
    private ArrayList<videobean> getlocalVideoDelete = new ArrayList<>();//记录每一段视频的信息，删除的时候用到
    private int getnowVideo = 0;//当前正在播放是第几段视频

    public int currentState = 3;
    public static final int CURRENT_STATE_PAUSE = 5;
    public static final int CURRENT_STATE_PLAYING = 3;

    private long mDurationMsAll = 0;//所有视频加起来的时间；
    private long getDurationPlayingMsAll = 0;//当前播放的第几段时间

    List<String> videos = new ArrayList<>();//视频裁切后的视频地址
    SparseArray<Boolean> getSelect = new SparseArray<>();
    private Boolean isEditWork = false;//是否编辑收藏


    /**
     * 获取播放的时间
     */
    @SuppressLint("HandlerLeak")
    private Handler getCurrentHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Log.i("Current", "Current=" + mPreview.getCurrentPosition());
                    //需要监听播放的时间点，播放下一段视频  ，???
                    int getCurrentTime = (int) (mPreview.getCurrentPosition() / delayMillisCurrent);
                    int getEndTime = (int) (getlocalVideo.get(getnowVideo).getEndTime() / delayMillisCurrent);
                    Log.i(TAG, "getCurrentTime=" + getCurrentTime);
                    Log.i(TAG, "getEndTime=" + getEndTime);
                    if (getnowVideo < getlocalVideo.size() - 1) {//如果不是最后一段
                        if (getCurrentTime == getEndTime) {
                            //如果播放到截取的时间，播放下一个视频，算播放完成
                            getnowVideo++;
                            Log.i(TAG, "播放完成后，继续播放下一段视频=" + getnowVideo);
                            getVideoMsIng = getlocalVideo.get(getnowVideo).getStartTime();
                            play();
                        }

                    } else {
                        if (getCurrentTime == getEndTime) {
                            Log.i(TAG, "播放到最后一段视频，回到第一段视频暂停");
                            reStartPlay();
                        }
                    }
                    //循环发送消息, 携带进度
                    msg = Message.obtain();
                    getCurrentHandler.removeMessages(1);
                    msg.what = 1;
                    getCurrentHandler.sendMessageDelayed(msg, delayMillisCurrent);

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
    private Handler seekhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //初始化，seekbar
                    mSeekBar.setMax((int) mDurationMsAll);
                    //设置显示的时长
                    mTimeText.setText(0 / 1000 + " / " + mDurationMsAll / 1000);
                    mSeekBar.setProgress(0);

                    break;
                default:
                    break;

            }
        }
    };

    /**
     * 滚动条
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
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
                    Log.i("seekbar", "滑动seekbar的时候有一个瞬间为0,或者可能会大于总进度：currentPosition=" + currentPosition);
                    if (currentPosition > 0 && currentPosition <= mDurationMsAll) {
                        //设置seekBar中的数据
                        mSeekBar.setMax(allLength);
                        //设置显示的时长
                        mTimeText.setText(currentPosition / 1000 + " / " + allLength / 1000);
                        mSeekBar.setProgress((int) (currentPosition));
                    }

                    //循环发送消息, 携带进度
                    msg = Message.obtain();
                    if (getnowVideo == 0) {
                        if (getlocalVideo.size() > 0) {
                            msg.arg1 = (int) (mPreview.getCurrentPosition() - getlocalVideo.get(getnowVideo).getStartTime());
                        }
                    } else {
                        //需要加上之前的长度
                        msg.arg1 = (int) (Integer.parseInt(String.valueOf(getlocalVideo.get(getnowVideo - 1).getVideoSize())) + mPreview.getCurrentPosition() - getlocalVideo.get(getnowVideo).getStartTime());
                    }
                    msg.arg2 = (int) mDurationMsAll;
                    msg.what = 1;
                    if (mPreview.isPlaying()) {
                        handler.sendMessageDelayed(msg, delayMillis);
                    }
                    break;
                default:
                    break;

            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collect_common, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViews();
    }

    private void findViews() {

        mShortVideoComposer = new PLShortVideoComposer(getActivity());
        mProcessingDialog = new CustomProgressDialog(getActivity());
        mProcessingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mShortVideoTrimmer.cancelTrim();
                mShortVideoComposer.cancelComposeVideos();
            }
        });


        //为VideoView设置控制器
        //设置暂停和播放的按钮
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getlocalVideo.size() == 0) {
                    return;
                }
                //判断当前的视频是否正在播放
                if (mPreview.isPlaying()) {
                    Log.i(TAG, "由播放到暂停");
                    mPreview.pause();
                    startButton.setImageResource(R.drawable.exo_controls_play);

                    getCurrentHandler.removeMessages(1);//停止统计获取当前的时间
                } else {
                    handler.removeMessages(1);

                    getVideoMsIng = mPreview.getCurrentPosition();
                    Log.i(TAG, "由暂停到播放=" + getVideoMsIng);
                    play();

                    //发送一个消息, 使seekBar , TextView和 VideoView关联
                    Message message = Message.obtain();
                    message.what = 1;
                    //当前播放的时间-裁切的时间
                    message.arg1 = (int) (mPreview.getCurrentPosition() - getlocalVideo.get(getnowVideo).getStartTime());
                    message.arg2 = (int) mDurationMsAll;
                    handler.sendMessageDelayed(message, delayMillis);
                }
            }
        });


        mPreview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                handler.removeMessages(1);

                //视频开启播放后, 触发的监听器
                Message message = Message.obtain();
                message.what = 1;
                if (getnowVideo == 0) {
                    message.arg1 = 0;
                } else {
                    //需要从裁切后的时间点开始
                    message.arg1 = Integer.parseInt(String.valueOf(getlocalVideo.get(getnowVideo - 1).getVideoSize()) + getlocalVideo.get(getnowVideo).getStartTime());
                }
                Log.i(TAG, "setOnPreparedListener+VideoSize=" + message.arg1);
                message.arg2 = (int) mDurationMsAll;
                handler.sendMessageDelayed(message, delayMillis);
            }
        });

        //设置进度条拖拽的监听
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                //判断是否是由用户拖拽发生的变化
                if (fromUser) {
                    startButton.setImageResource(R.drawable.exo_controls_pause);

                    Log.i(TAG, "onProgressChanged");
                    //需要判断是拖动到了第几段视频了
                    for (int i = 0; i < getlocalVideo.size(); i++) {
                        if (progress < getlocalVideo.get(i).getVideoSize()) {

                            getnowVideo = i;//一旦小于，就代表第几段，退出
                            if (getnowVideo == 0) {
                                getVideoMsIng = progress + getlocalVideo.get(i).getStartTime();//播放起始时间，需要加上裁切的时间
                            } else {
                                //选择从第几段的，多少秒开始播放视频
                                getVideoMsIng = progress + getlocalVideo.get(i).getStartTime() - getlocalVideo.get(getnowVideo - 1).getVideoSize();//
                            }
                            play();

                            if (progress != 0) {
                                handler.removeMessages(1);
                                Message message = Message.obtain();
                                message.what = 1;
                                message.arg1 = progress;
                                message.arg2 = (int) mDurationMsAll;
                                handler.sendMessageDelayed(message, delayMillis);
                            }
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

        mAddVideoFromLocal.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addVideoFromLocal:
                addLoacalVideo();
                break;
            default:
                break;
        }
    }


    private void init(String videoPath) {

        getnowVideo = 0;
        getCurrentHandler.removeMessages(1);//停止统计获取当前的时间

        PLMediaFile mMediaFile;
        mMediaFile = new PLMediaFile(videoPath);

        videobean mvideobean = new videobean();

        if (getlocalVideo.size() == 0) {
            mvideobean.setVideoSize(mMediaFile.getDurationMs());
            mvideobean.setStartTime(0);
            mvideobean.setEndTime(mMediaFile.getDurationMs());
        } else {
            //第三个的开始视频时长，是第一个+第二个+第三个总和的总和，以此类推
            mvideobean.setVideoSize(getlocalVideo.get(getlocalVideo.size() - 1).getVideoSize() + mMediaFile.getDurationMs());
            mvideobean.setStartTime(0);
            mvideobean.setEndTime(mMediaFile.getDurationMs());
        }
        mvideobean.setVideoUrl(videoPath);
        getlocalVideo.add(mvideobean);

        addViewtopFilmr = View.inflate(getActivity(), R.layout.including_video_cut, null);
        lltopFilmr.addView(addViewtopFilmr);//添加选择器到布局中来

        //把进度条分割成多个片段
        addSeekView = View.inflate(getActivity(), R.layout.including_seek_bar_video_cut, null);
        addSeekView.setLayoutParams(new LinearLayout.LayoutParams(0, MATCH_PARENT, mMediaFile.getDurationMs()));
        lladdSeekView.addView(addSeekView);

        //从0开始
//        initVideoFrameList(addViewtopFilmr, mMediaFile.getDurationMs(), mMediaFile, getlocalVideo.size() - 1);
        Log.i(TAG, "getDurationMs=" + mMediaFile.getDurationMs());

        mDurationMsAll = mDurationMsAll + mMediaFile.getDurationMs();//计算总时间

        if (getlocalVideo.size() == 1) {//播放第一个视频
            mPreview.setVideoPath(videoPath);
        }

        //设置显示的时长
        mTimeText.setText(0 + " / " + mDurationMsAll / 1000);

    }
//
//
//    /**
//     * 滚动器
//     */
//    private void initVideoFrameList(final View baseview, final long mDurationMs, final PLMediaFile mMediaFile, final int getVideNum) {
//        final RelativeLayout rlFragmentCutProcess;
//        final LinearLayout mFrameListView;
//        final View mHandlerLeft;
//        final View mHandlerRight;
//        final boolean[] isShow = {true};
//
//        rlFragmentCutProcess = (RelativeLayout) baseview.findViewById(R.id.rlFragmentCutProcess);
//        mFrameListView = (LinearLayout) baseview.findViewById(R.id.video_frame_list);
//        mHandlerLeft = baseview.findViewById(R.id.handler_left);
//        mHandlerRight = baseview.findViewById(R.id.handler_right);
//
//        final RelativeLayout rlFrgmentCutEdit = (RelativeLayout) baseview.findViewById(R.id.rlFrgmentCutEdit);
//
//        final TextView mtvFrgmentNumberNor = (TextView) baseview.findViewById(R.id.tvFrgmentNumberNor);
//        final LinearLayout llFrgmentCutFuncNormal = (LinearLayout) baseview.findViewById(R.id.llFrgmentCutFuncNormal);
//
//        final LinearLayout llFrgmentCutFuncSelect = (LinearLayout) baseview.findViewById(R.id.llFrgmentCutFuncSelect);
//        final TextView tvFrgmentNumberSel = (TextView) baseview.findViewById(R.id.tvFrgmentNumberSel);
//        final TextView tvFrgmentCutTime = (TextView) baseview.findViewById(R.id.tvFrgmentCutTime);
//
//
//        final RelativeLayout rlFrgmentCutEditShow = (RelativeLayout) baseview.findViewById(R.id.rlFrgmentCutEditShow);
//        final TextView tvFragmentCutDelete = (TextView) baseview.findViewById(R.id.tvFragmentCutDelete);
//        final TextView tvFragmentCutSpeed = (TextView) baseview.findViewById(R.id.tvFragmentCutSpeed);
//        final TextView tvFragmentCutVoice = (TextView) baseview.findViewById(R.id.tvFragmentCutVoice);
//        final TextView tvFragmentRotate = (TextView) baseview.findViewById(R.id.tvFragmentRotate);
//        final TextView tvFragmentCutMirroring = (TextView) baseview.findViewById(R.id.tvFragmentCutMirroring);
//        final TextView tvFragmentCutInOut = (TextView) baseview.findViewById(R.id.tvFragmentCutInOut);
//        final TextView tvFragmentCutCopy = (TextView) baseview.findViewById(R.id.tvFragmentCutCopy);
//
//        mtvFrgmentNumberNor.setText((getVideNum + 1) + "");
//        tvFrgmentNumberSel.setText((getVideNum + 1) + "");
//
//        tvFragmentCutCopy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //复制
//                init(getlocalVideo.get(getVideNum).getVideoUrl());
//            }
//        });
//
//        tvFragmentCutDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                mDurationMsAll = 0;
//
//                handler.removeMessages(1);
//                getCurrentHandler.removeMessages(1);//停止统计获取当前的时间
//
//                MyLog.i(TAG, "getVideNum=" + getVideNum);
//                getlocalVideo.remove(getVideNum);
//                lltopFilmr.removeAllViews();
//                lladdSeekView.removeAllViews();
//
//                getlocalVideoDelete.clear();
//                getlocalVideoDelete.addAll(getlocalVideo);//重新paix排序
//                getlocalVideo.clear();
//
//                MyLog.i(TAG, "getlocalVideoDelete.size()=" + getlocalVideoDelete.size());
//                for (int i = 0; i < getlocalVideoDelete.size(); i++) {
//                    init(getlocalVideoDelete.get(i).getVideoUrl());
//                }
//                reStartPlay();
//            }
//        });
//
//        rlFrgmentCutEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //先恢复默认的
//                for (int i = 0; i < getlocalVideo.size(); i++) {
//                    lltopFilmr.getChildAt(i).findViewById(R.id.rlFrgmentCutEditShow).setVisibility(View.GONE);
//                    lltopFilmr.getChildAt(i).findViewById(R.id.rlFragmentCutProcess).setVisibility(View.VISIBLE);
//                    lltopFilmr.getChildAt(i).findViewById(R.id.llFrgmentCutFuncNormal).setVisibility(View.VISIBLE);
//                    lltopFilmr.getChildAt(i).findViewById(R.id.llFrgmentCutFuncSelect).setVisibility(View.GONE);
//                }
//                if (isShow[0]) {
//                    isShow[0] = false;
//                    llFrgmentCutFuncNormal.setVisibility(View.GONE);
//                    llFrgmentCutFuncSelect.setVisibility(View.VISIBLE);
//
//                    rlFragmentCutProcess.setVisibility(View.GONE);
//                    rlFrgmentCutEditShow.setVisibility(View.VISIBLE);
//                } else {
//                    isShow[0] = true;
//                    llFrgmentCutFuncNormal.setVisibility(View.VISIBLE);
//                    llFrgmentCutFuncSelect.setVisibility(View.GONE);
//
//                    rlFragmentCutProcess.setVisibility(View.VISIBLE);
//                    rlFrgmentCutEditShow.setVisibility(View.GONE);
//                }
//
//
//            }
//        });
//
//        mHandlerLeft.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int action = event.getAction();
//                float viewX = v.getX();
//                float movedX = event.getX();
//                float finalX = viewX + movedX;
//                updateHandlerLeftPosition(mHandlerLeft, mHandlerRight, finalX);
//
//                if (action == MotionEvent.ACTION_UP) {
//                    calculateRange(mHandlerLeft, mHandlerRight, mFrameListView, mDurationMs, getVideNum, baseview);
//                }
//                return true;
//            }
//        });
//
//        mHandlerRight.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int action = event.getAction();
//                float viewX = v.getX();
//                float movedX = event.getX();
//                float finalX = viewX + movedX;
//                updateHandlerRightPosition(mHandlerLeft, mHandlerRight, mFrameListView, finalX);
//
//                if (action == MotionEvent.ACTION_UP) {
//                    calculateRange(mHandlerLeft, mHandlerRight, mFrameListView, mDurationMs, getVideNum, baseview);
//                }
//
//                return true;
//            }
//        });
//
//        mFrameListView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @SuppressLint("StaticFieldLeak")
//            @Override
//            public void onGlobalLayout() {
//                mFrameListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//
//                final int sliceEdge = mFrameListView.getWidth() / SLICE_COUNT;
//                mSlicesTotalLength = sliceEdge * SLICE_COUNT;
////                Log.i(TAG, "slice edge: " + sliceEdge);
//                final float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
//
//                new AsyncTask<Void, PLVideoFrame, Void>() {
//                    @Override
//                    protected Void doInBackground(Void... v) {
//                        for (int i = 0; i < SLICE_COUNT; ++i) {
//                            PLVideoFrame frame = mMediaFile.getVideoFrameByTime((long) ((1.0f * i / SLICE_COUNT) * mDurationMs), true, sliceEdge, sliceEdge);
//                            publishProgress(frame);
//                        }
//                        return null;
//                    }
//
//                    @Override
//                    protected void onProgressUpdate(PLVideoFrame... values) {
//                        super.onProgressUpdate(values);
//                        PLVideoFrame frame = values[0];
//                        if (frame != null) {
//                            View root = LayoutInflater.from(getActivity()).inflate(R.layout.frame_item, null);
//
//                            int rotation = frame.getRotation();
//                            ImageView thumbnail = (ImageView) root.findViewById(R.id.thumbnail);
//                            thumbnail.setImageBitmap(frame.toBitmap());
//                            thumbnail.setRotation(rotation);
//                            FrameLayout.LayoutParams thumbnailLP = (FrameLayout.LayoutParams) thumbnail.getLayoutParams();
//                            if (rotation == 90 || rotation == 270) {
//                                thumbnailLP.leftMargin = thumbnailLP.rightMargin = (int) px;
//                            } else {
//                                thumbnailLP.topMargin = thumbnailLP.bottomMargin = (int) px;
//                            }
//                            thumbnail.setLayoutParams(thumbnailLP);
//
//                            LinearLayout.LayoutParams rootLP = new LinearLayout.LayoutParams(sliceEdge, sliceEdge);
//                            mFrameListView.addView(root, rootLP);
//                        }
//                    }
//                }.execute();
//            }
//        });
//
//
//    }

    private void updateHandlerLeftPosition(View mHandlerLeft, View mHandlerRight, float movedPosition) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mHandlerLeft.getLayoutParams();
        if ((movedPosition + mHandlerLeft.getWidth()) > mHandlerRight.getX()) {
            lp.leftMargin = (int) (mHandlerRight.getX() - mHandlerLeft.getWidth());
        } else if (movedPosition < 0) {
            lp.leftMargin = 0;
        } else {
            lp.leftMargin = (int) movedPosition;
        }
        mHandlerLeft.setLayoutParams(lp);
    }

    private void updateHandlerRightPosition(View mHandlerLeft, View mHandlerRight, LinearLayout mFrameListView, float movedPosition) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mHandlerRight.getLayoutParams();
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        if (movedPosition < (mHandlerLeft.getX() + mHandlerLeft.getWidth())) {
            lp.leftMargin = (int) (mHandlerLeft.getX() + mHandlerLeft.getWidth());
        } else if ((movedPosition + (mHandlerRight.getWidth() / 2)) > (mFrameListView.getX() + mSlicesTotalLength)) {
            lp.leftMargin = (int) ((mFrameListView.getX() + mSlicesTotalLength) - (mHandlerRight.getWidth() / 2));
        } else {
            lp.leftMargin = (int) movedPosition;
        }
        mHandlerRight.setLayoutParams(lp);
    }


    private float clamp(float origin) {
        if (origin < 0) {
            return 0;
        }
        if (origin > 1) {
            return 1;
        }
        return origin;
    }

    /**
     * 裁切范围
     *
     * @param mHandlerLeft
     * @param mHandlerRight
     * @param mFrameListView
     * @param mDurationMs
     */
    private void calculateRange(View mHandlerLeft, View mHandlerRight, LinearLayout mFrameListView, long mDurationMs, int getVideNum, View baseview) {

        float beginPercent = 1.0f * ((mHandlerLeft.getX() + mHandlerLeft.getWidth() / 2) - mFrameListView.getX()) / mSlicesTotalLength;
        float endPercent = 1.0f * ((mHandlerRight.getX() + mHandlerRight.getWidth() / 2) - mFrameListView.getX()) / mSlicesTotalLength;
        beginPercent = clamp(beginPercent);
        endPercent = clamp(endPercent);


        Long mSelectedBeginMs = (long) (beginPercent * mDurationMs);
        Long mSelectedEndMs = (long) (endPercent * mDurationMs);
        Log.i(TAG, "begin percent: " + beginPercent + " end percent: " + endPercent);
        Log.i(TAG, "getVideNum: " + getVideNum);


        //总的进度条时间需要减少
        if (getlocalVideo.size() == 0) {//如果只有一个视频，就不需要那么麻烦了
            mDurationMsAll = getlocalVideo.get(getVideNum).getEndTime() - getlocalVideo.get(getVideNum).getStartTime();
        } else {
            //只需要判断之前的时间和现在的时间的一个对比
            if (mSelectedBeginMs != getlocalVideo.get(getVideNum).getStartTime()) {
                Log.i(TAG, "mSelectedBeginMs=" + mSelectedBeginMs);
                Log.i(TAG, "getlocalVideo.get(getVideNum).getStartTime()=" + getlocalVideo.get(getVideNum).getStartTime());
                mDurationMsAll = mDurationMsAll + getlocalVideo.get(getVideNum).getStartTime() - mSelectedBeginMs;
            }
            //只需要判断之前的时间和现在的时间的一个对比

            if (mSelectedEndMs != getlocalVideo.get(getVideNum).getEndTime()) {
                Log.i(TAG, "mSelectedEndMs=" + mSelectedEndMs);
                Log.i(TAG, "getlocalVideo.get(getVideNum).getEndTime()=" + getlocalVideo.get(getVideNum).getEndTime());
                mDurationMsAll = mDurationMsAll - getlocalVideo.get(getVideNum).getEndTime() + mSelectedEndMs;
            }
        }

        Log.i(TAG, "getStartTime+getEndTime=" + getlocalVideo.get(getVideNum).getStartTime() + "-" + getlocalVideo.get(getVideNum).getEndTime());
        //先处理裁切后面的视频，比如裁切的是第二段，那后面视频的，就需要判断是前面的还是后面的裁切
        for (int i = getVideNum + 1; i < getlocalVideo.size(); i++) {//从当前这个段开始算
            videobean mvideobeanNew = new videobean();
            mvideobeanNew.setStartTime(getlocalVideo.get(i).getStartTime());
            mvideobeanNew.setEndTime(getlocalVideo.get(i).getEndTime());
            mvideobeanNew.setVideoUrl(getlocalVideo.get(i).getVideoUrl());
            //如果是左边的裁切
            if (mSelectedBeginMs != getlocalVideo.get(getVideNum).getStartTime()) {
                Long mVideoSize = getlocalVideo.get(i).getVideoSize() + getlocalVideo.get(getVideNum).getStartTime() - mSelectedBeginMs;
                Log.i(TAG, "如果是左边的裁切=mVideoSize=" + mVideoSize);
                mvideobeanNew.setVideoSize(mVideoSize);
            }
            //如果是右边的裁切
            if (mSelectedEndMs != getlocalVideo.get(getVideNum).getEndTime()) {
                Long mVideoSize = getlocalVideo.get(i).getVideoSize() - getlocalVideo.get(getVideNum).getEndTime() + mSelectedEndMs;
                Log.i(TAG, "如果是右边的裁切=mVideoSize=" + mVideoSize);
                mvideobeanNew.setVideoSize(mVideoSize);
            }
            getlocalVideo.set(i, mvideobeanNew);
        }

        //重新保存视频数据。裁切作品和计算时间
        videobean mvideobean = new videobean();
        mvideobean.setStartTime(mSelectedBeginMs);
        mvideobean.setEndTime(mSelectedEndMs);
        mvideobean.setVideoUrl(getlocalVideo.get(getVideNum).getVideoUrl());
        if (getVideNum == 0) {
            //end-start
            mvideobean.setVideoSize((mSelectedEndMs - mSelectedBeginMs));
        } else {
            mvideobean.setVideoSize(getlocalVideo.get(getVideNum - 1).getVideoSize() + (mSelectedEndMs - mSelectedBeginMs));
        }
        getlocalVideo.set(getVideNum, mvideobean);// 当前的视频参数需要修改，


        //进度条记录每一个片段的比例
        lladdSeekView.removeAllViews();//先移除
        for (int i = 0; i < getlocalVideo.size(); i++) {
            Log.i(TAG, "把进度条分割成多个片段=" + (getlocalVideo.get(i).getEndTime() - getlocalVideo.get(i).getStartTime()));
            addSeekView = View.inflate(getActivity(), R.layout.including_seek_bar_video_cut, null);
            addSeekView.setLayoutParams(new LinearLayout.LayoutParams(0, MATCH_PARENT, (getlocalVideo.get(i).getEndTime() - getlocalVideo.get(i).getStartTime()) / 1000));
            lladdSeekView.addView(addSeekView);
        }

        Log.i(TAG, "new range: " + mSelectedBeginMs + "-" + mSelectedEndMs);
//        TextView range = (TextView) baseview.findViewById(R.id.range);
//        range.setText(formatTime(mSelectedBeginMs) + " - " + formatTime(mSelectedEndMs));

        resumePlay();

    }


    private String formatTime(long timeMs) {
        return String.format(Locale.CHINA, "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(timeMs),
                TimeUnit.MILLISECONDS.toSeconds(timeMs) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeMs))
        );
    }


    /**
     * 播放
     */
    private void play() {
        if (mPreview != null) {
            startButton.setImageResource(R.drawable.exo_controls_pause);
            Log.i(TAG, "getnowVideo=" + getnowVideo);
            Log.i(TAG, "VideoUrl()=" + getlocalVideo.get(getnowVideo).getVideoUrl());
            Log.i(TAG, "getVideoMsIng=" + getVideoMsIng);
            mPreview.setVideoPath(getlocalVideo.get(getnowVideo).getVideoUrl());
            mPreview.seekTo((int) getVideoMsIng);
            mPreview.start();

            getCurrentHandler.removeMessages(1);
            Message message = Message.obtain();
            message.what = 1;
            getCurrentHandler.sendMessageDelayed(message, delayMillisCurrent);


        }
    }


    /**
     * 播放完了之后，回到第一段暂停
     */
    private void reStartPlay() {
        if (mPreview != null) {
            mPreview.resume();//代表的是重新播放
            mPreview.pause();
        }
        startButton.setImageResource(R.drawable.exo_controls_play);
        getnowVideo = 0;

        if (getlocalVideo.size() > 0) {
            getVideoMsIng = getlocalVideo.get(getnowVideo).getStartTime();//从截取时间点
        } else {
            mDurationMsAll = 0;//如果没有视频，那么总时间为0
        }
        getCurrentHandler.removeMessages(1);//停止统计获取当前的时间
        Log.i(TAG, "reStartPlay");

        Message message = Message.obtain();
        message.what = 1;
        seekhandler.sendMessageDelayed(message, delayMillis);

    }

    /**
     * 从0开始播放
     */
    private void resumePlay() {
        if (mPreview != null) {
            mPreview.resume();//代表的是重新播放
            mPreview.pause();
        }
        getnowVideo = 0;
        getVideoMsIng = getlocalVideo.get(getnowVideo).getStartTime();//从截取时间点

        Message message = Message.obtain();
        message.what = 1;
        seekhandler.sendMessageDelayed(message, delayMillis);
        Log.i(TAG, "resumePlay");

        getCurrentHandler.removeMessages(1);//暂停记录播放的时间
        play();
    }

    private Handler mHandler = new Handler();

    private void startTrackPlayProgress() {
        stopTrackPlayProgress();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mPreview.getCurrentPosition() >= getVideoMsIng) {
                    mPreview.seekTo((int) getVideoMsIng);
                }
                mHandler.postDelayed(this, 100);
            }
        }, 100);
    }

    private void stopTrackPlayProgress() {
        mHandler.removeCallbacksAndMessages(null);
    }


    @Override
    public void onPause() {
        super.onPause();
        getCurrentHandler.removeMessages(1);//停止统计获取当前的时间
        startButton.setImageResource(R.drawable.exo_controls_play);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mShortVideoTrimmer != null) {
            mShortVideoTrimmer.destroy();
        }
    }

    private void addLoacalVideo() {
        Intent intentvideo = new Intent();
        if (Build.VERSION.SDK_INT < 19) {
            intentvideo.setAction(Intent.ACTION_GET_CONTENT);
            intentvideo.setType("video/*");
        } else {
            intentvideo.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intentvideo.addCategory(Intent.CATEGORY_OPENABLE);
            intentvideo.setType("video/*");
        }
        startActivityForResult(Intent.createChooser(intentvideo, "选择要导入的视频"), 0);
    }

    /**
     * @dec 七牛的裁切的方法
     * @author fanqie
     * @date 2018/8/14 17:04
     */
    private void trim(int i, String url, long beginMs, long endMs) {
        final int[] j = {i};
        String TRIM_FILE_PATH = Config.VIDEO_STORAGE_DIR + (int) ((Math.random() * 9 + 1) * 10000) + "trimmed.mp4";
        mShortVideoTrimmer = new PLShortVideoTrimmer(getActivity(), url, TRIM_FILE_PATH);
        mProcessingDialog.show();
        mShortVideoTrimmer.trim(beginMs, endMs, PLShortVideoTrimmer.TRIM_MODE.FAST, new PLVideoSaveListener() {
            @Override
            public void onSaveVideoSuccess(String path) {
                mProcessingDialog.dismiss();
                Log.i(TAG, "path=" + path);
                videos.add(path);//视频地址

                Message message = Message.obtain();
                message.what = 1;
                message.arg1 = j[0];
                getTrimHandler.sendMessageDelayed(message, 1000);

            }

            @Override
            public void onSaveVideoFailed(final int errorCode) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProcessingDialog.dismiss();
                        ToastUtils.toastErrorCode(getActivity(), errorCode);
                    }
                });
            }

            @Override
            public void onSaveVideoCanceled() {
                mProcessingDialog.dismiss();
            }

            @Override
            public void onProgressUpdate(final float percentage) {
                getActivity().runOnUiThread(new Runnable() {
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
                    if (msg.arg1 == getlocalVideo.size() - 1) {//如果已经裁切了最后一个
//                        jumpToActivity(VideoDemoActivity.class);
                        onClickCompose();
                    } else {
                        int i = msg.arg1 + 1;
                        trim(i, getlocalVideo.get(i).getVideoUrl(), getlocalVideo.get(i).getStartTime(), getlocalVideo.get(i).getEndTime());
                    }

                    break;
                default:
                    break;

            }
        }
    };

    /**
     * @dec 裁切拼接
     * @author fanqie
     * @date 2018/8/14 16:51
     */
    public void tvClickTrim(View view) {
        reStartPlay();

        trim(0, getlocalVideo.get(0).getVideoUrl(), getlocalVideo.get(0).getStartTime(), getlocalVideo.get(0).getEndTime());
    }

    /**
     * @dec 视频拼接
     * @author fanqie
     * @date 2018/8/14 18:10
     */
    public void onClickCompose() {
        if (videos.size() < 2) {
            ToastUtils.s(getActivity(), "请先添加至少 2 个视频");
            return;
        }
        PLVideoEncodeSetting setting = new PLVideoEncodeSetting(getActivity());
        setting.setEncodingSizeLevel(getEncodingSizeLevel(14));
        setting.setEncodingBitrate(getEncodingBitrateLevel(6));
        if (mShortVideoComposer.composeVideos(videos, Config.COMPOSE_FILE_PATH, setting, mVideoSaveListener)) {
            mProcessingDialog.show();
        } else {
            ToastUtils.s(getActivity(), "开始拼接失败！");
        }
    }

    private PLVideoSaveListener mVideoSaveListener = new PLVideoSaveListener() {
        @Override
        public void onSaveVideoSuccess(String filepath) {
            mProcessingDialog.dismiss();
            Log.i(TAG, "filepath=" + filepath);
//            VideoEditActivity.start(getActivity(), filepath);
//            Intent intent = new Intent(getActivity(), VideoDemoActivity.class);
//            intent.putExtra("video_path", filepath);
//            startActivity(intent);
        }

        @Override
        public void onSaveVideoFailed(final int errorCode) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProcessingDialog.dismiss();
                    ToastUtils.toastErrorCode(getActivity(), errorCode);
                }
            });
        }

        @Override
        public void onSaveVideoCanceled() {
            mProcessingDialog.dismiss();
        }

        @Override
        public void onProgressUpdate(final float percentage) {
            getActivity().runOnUiThread(new Runnable() {
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String selectedFilepath = GetPathFromUri.getPath(getActivity(), data.getData());
            Log.i(TAG, "Select file: " + selectedFilepath);
            if (selectedFilepath != null && !"".equals(selectedFilepath)) {
                try {
                    init(selectedFilepath);
                } catch (Exception e) {
                    Log.i(TAG, "e=" + e.getMessage());
                }
            }
        } else {
        }
    }
}
