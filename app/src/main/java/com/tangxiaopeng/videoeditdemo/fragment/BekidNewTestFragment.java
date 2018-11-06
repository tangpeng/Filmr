package com.tangxiaopeng.videoeditdemo.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qiniu.pili.droid.shortvideo.PLMediaFile;
import com.qiniu.pili.droid.shortvideo.PLVideoFrame;
import com.tangxiaopeng.videoeditdemo.BekidMainActivity;
import com.tangxiaopeng.videoeditdemo.R;
import com.tangxiaopeng.videoeditdemo.bean.videobean;
import com.tangxiaopeng.videoeditdemo.utils.GetPathFromUri;
import com.tangxiaopeng.videoeditdemo.utils.ImageUtil;
import com.tangxiaopeng.videoeditdemo.utils.MyLog;
import com.tangxiaopeng.videoeditdemo.utils.QiniuTool;
import com.tangxiaopeng.videoeditdemo.utils.UnitConversionTool;
import com.tangxiaopeng.videoeditdemo.view.RoundImageView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.provider.MediaStore.Images.Thumbnails.MICRO_KIND;
import static com.tangxiaopeng.videoeditdemo.BekidMainActivity.mDurationMsAll;
import static com.tangxiaopeng.videoeditdemo.BekidMainActivity.mFragmentVideos;
import static com.tangxiaopeng.videoeditdemo.utils.Tools.getlocalVideo;


/**
 * bekid
 * Created by JY
 * Date：17/2/20
 * Time：下午5:01
 */
public class BekidNewTestFragment extends BaseFragment implements View.OnClickListener {

    public static String CURRENT_PAGE = "current_page";
    private static final String TAG = "BekidNewFragment";
    @BindView(R.id.lltopFilmr)
    LinearLayout lltopFilmr;
    Unbinder unbinder;

    private static final int SLICE_COUNT = 8;
    @BindView(R.id.addVideoFromLocal)
    ImageView mAddVideoFromLocal;

    private View addViewtopFilmr;

    private int mSlicesTotalLength;

    /*Fragment的传参方式(通过Bundle对象来传递)
     *采用这种传参方式可以保证用户在横竖屏切换时所
      * 传递的参数不会丢失
      */
    public static BekidNewTestFragment getInstance(String data) {
        BekidNewTestFragment rightFragment = new BekidNewTestFragment();
        Bundle bundle = new Bundle();
        //将需要传递的字符串以键值对的形式传入bundle
        bundle.putString("data", data);
        rightFragment.setArguments(bundle);
        return rightFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filmr_cut, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i(TAG, "init");
        findViews();
    }

    private void findViews() {

        mAddVideoFromLocal.setOnClickListener(this);
        String data = getArguments().getString("data");
        init(data);
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


    public void init(String videoPath) {


        if (getlocalVideo.size() > 0) {
            mFragmentVideos.add(FragmentVideo.getInstance(videoPath));//d
//            ((BekidMainActivity) getActivity()).reIdleStartPlay();
        }
        PLMediaFile mMediaFile;
        mMediaFile = new PLMediaFile(videoPath);

        videobean mvideobean = new videobean();

        if (getlocalVideo.size() == 0) {
            mvideobean.setVideoSize(mMediaFile.getDurationMs());
            mvideobean.setStartTime(0);
            mvideobean.setEndTime(mMediaFile.getDurationMs());
        } else {

            mvideobean.setVideoSize(mMediaFile.getDurationMs());
            mvideobean.setStartTime(0);
            mvideobean.setEndTime(mMediaFile.getDurationMs());
        }
        mvideobean.setVideoUrl(videoPath);
        getlocalVideo.add(mvideobean);

        addViewtopFilmr = View.inflate(getActivity(), R.layout.including_video_cut, null);
        lltopFilmr.addView(addViewtopFilmr);//添加选择器到布局中来

        ((BekidMainActivity) getActivity()).AddLocalDivisionVideo(mMediaFile);//进度条的分割

        //从0开始
        initVideoFrameList(addViewtopFilmr, mMediaFile.getDurationMs(), mMediaFile, getlocalVideo.size() - 1);
        Log.i(TAG, "getDurationMs=" + mMediaFile.getDurationMs());
    }


    /**
     * 滚动器
     */
    private void initVideoFrameList(final View baseview, final long mDurationMs, final PLMediaFile mMediaFile, final int getVideNum) {
        final RelativeLayout rlFragmentCutProcess;
        final LinearLayout mFrameListView;
        final View mHandlerLeft;
        final View mHandlerRight;
        final boolean[] isShow = {true};
        final View handlerLeftAlpha;//滑动的时候，左边的裁切的需要加阴影
        final View handlerRightAlpha;//滑动的时候，右边透明

        //获取视频的第一帧封面
        Bitmap getOneBitmap = ImageUtil.getVideoThumbnail(getlocalVideo.get(getVideNum).getVideoUrl(),
                UnitConversionTool.dip2px(getActivity(), 48), UnitConversionTool.dip2px(getActivity(), 48), MICRO_KIND);

        rlFragmentCutProcess = (RelativeLayout) baseview.findViewById(R.id.rlFragmentCutProcess);
        mFrameListView = (LinearLayout) baseview.findViewById(R.id.video_frame_list);
        mHandlerLeft = baseview.findViewById(R.id.handler_left);
        mHandlerRight = baseview.findViewById(R.id.handler_right);

        if (getVideNum == 0) {//默认第一个选中
            mHandlerLeft.setBackgroundResource(R.drawable.a5);
            mHandlerRight.setBackgroundResource(R.drawable.a4);
        }

        handlerLeftAlpha = baseview.findViewById(R.id.handler_left_alpha);
        handlerRightAlpha = baseview.findViewById(R.id.handler_left_alpha_other);

        //左边的总布局
        final RelativeLayout rlFrgmentCutEdit = (RelativeLayout) baseview.findViewById(R.id.rlFrgmentCutEdit);

        //左边的默认图层
        final RelativeLayout rlFrgmentCutFuncNormal = (RelativeLayout) baseview.findViewById(R.id.rlFrgmentCutFuncNormal);
        final RoundImageView rivFrgmentCutEditNormal = (RoundImageView) baseview.findViewById(R.id.rivFrgmentCutEditNormal);
        rivFrgmentCutEditNormal.setImageBitmap(getOneBitmap);
        final TextView mtvFrgmentNumberNor = (TextView) baseview.findViewById(R.id.tvFrgmentNumberNor);
        //左边的选中图层
        final RelativeLayout rlFrgmentCutFuncSelect = (RelativeLayout) baseview.findViewById(R.id.rlFrgmentCutFuncSelect);
        final RoundImageView rivFrgmentCutEditSelect = (RoundImageView) baseview.findViewById(R.id.rivFrgmentCutEditSelect);
        rivFrgmentCutEditSelect.setImageResource(R.drawable.bg_index_color);
        final TextView tvFrgmentNumberSel = (TextView) baseview.findViewById(R.id.tvFrgmentNumberSel);
        final TextView tvFrgmentCutTime = (TextView) baseview.findViewById(R.id.tvFrgmentCutTime);


        final RelativeLayout rlFrgmentCutEditShow = (RelativeLayout) baseview.findViewById(R.id.rlFrgmentCutEditShow);
        final TextView tvFragmentCutDelete = (TextView) baseview.findViewById(R.id.tvFragmentCutDelete);
        final TextView tvFragmentCutSpeed = (TextView) baseview.findViewById(R.id.tvFragmentCutSpeed);
        final TextView tvFragmentCutVoice = (TextView) baseview.findViewById(R.id.tvFragmentCutVoice);
        final TextView tvFragmentRotate = (TextView) baseview.findViewById(R.id.tvFragmentRotate);
        final TextView tvFragmentCutMirroring = (TextView) baseview.findViewById(R.id.tvFragmentCutMirroring);
        final TextView tvFragmentCutInOut = (TextView) baseview.findViewById(R.id.tvFragmentCutInOut);
        final TextView tvFragmentCutCopy = (TextView) baseview.findViewById(R.id.tvFragmentCutCopy);

        mtvFrgmentNumberNor.setText((getVideNum + 1) + "");
        tvFrgmentNumberSel.setText((getVideNum + 1) + "");

        addViewtopFilmr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击按钮
                for (int i = 0; i < getlocalVideo.size(); i++) {
                    View imageViewleft = (View) (lltopFilmr.getChildAt(i).findViewById(R.id.handler_left));
                    View imageViewRight = (View) (lltopFilmr.getChildAt(i).findViewById(R.id.handler_right));
                    if (getVideNum == i) {
                        imageViewleft.setBackgroundResource(R.drawable.a5);
                        imageViewRight.setBackgroundResource(R.drawable.a4);
                    } else {
                        imageViewleft.setBackgroundResource(R.drawable.a3);
                        imageViewRight.setBackgroundResource(R.drawable.a2);
                    }
                    lltopFilmr.getChildAt(i).findViewById(R.id.rlFrgmentCutFuncNormal).setVisibility(View.VISIBLE);
                    lltopFilmr.getChildAt(i).findViewById(R.id.rlFrgmentCutFuncSelect).setVisibility(View.GONE);
                }
            }
        });

        tvFragmentCutCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //复制
                init(getlocalVideo.get(getVideNum).getVideoUrl());
            }
        });

        tvFragmentCutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lltopFilmr.removeViewAt(getVideNum);//还需要修改i

//                ((BekidMainActivity) getActivity()).deleteVideo(getVideNum);

//                MyLog.i(TAG, "添加：getlocalVideoDelete.size()=" + getlocalVideoDelete.size());
//                for (int i = 0; i < getlocalVideoDelete.size(); i++) {
//                    init(getlocalVideoDelete.get(i).getVideoUrl());
//                }
                ((BekidMainActivity) getActivity()).reIdleReStartPlay();
            }
        });

        rlFrgmentCutEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //点击按钮选中
                for (int i = 0; i < getlocalVideo.size(); i++) {
                    View imageViewleft = (View) (lltopFilmr.getChildAt(i).findViewById(R.id.handler_left));
                    View imageViewRight = (View) (lltopFilmr.getChildAt(i).findViewById(R.id.handler_right));
                    if (getVideNum == i) {
                        imageViewleft.setBackgroundResource(R.drawable.a5);
                        imageViewRight.setBackgroundResource(R.drawable.a4);
                    } else {
                        imageViewleft.setBackgroundResource(R.drawable.a3);
                        imageViewRight.setBackgroundResource(R.drawable.a2);
                    }
                }

                //先恢复默认的
                for (int i = 0; i < getlocalVideo.size(); i++) {
                    lltopFilmr.getChildAt(i).findViewById(R.id.rlFrgmentCutFuncNormal).setVisibility(View.VISIBLE);
                    lltopFilmr.getChildAt(i).findViewById(R.id.rlFrgmentCutFuncSelect).setVisibility(View.GONE);

                    lltopFilmr.getChildAt(i).findViewById(R.id.rlFragmentCutProcess).setVisibility(View.VISIBLE);
                    lltopFilmr.getChildAt(i).findViewById(R.id.rlFrgmentCutEditShow).setVisibility(View.GONE);
                }
                if (isShow[0]) {
                    isShow[0] = false;
                    rlFrgmentCutFuncNormal.setVisibility(View.GONE);
                    rlFrgmentCutFuncSelect.setVisibility(View.VISIBLE);

                    rlFragmentCutProcess.setVisibility(View.GONE);
                    rlFrgmentCutEditShow.setVisibility(View.VISIBLE);
                } else {
                    isShow[0] = true;
                    rlFrgmentCutFuncNormal.setVisibility(View.VISIBLE);
                    rlFrgmentCutFuncSelect.setVisibility(View.GONE);

                    rlFragmentCutProcess.setVisibility(View.VISIBLE);
                    rlFrgmentCutEditShow.setVisibility(View.GONE);
                }


            }
        });

        mHandlerLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                float viewX = v.getX();
                float movedX = event.getX();
                float finalX = viewX + movedX;
                updateHandlerLeftPosition(tvFrgmentCutTime, mDurationMs, handlerLeftAlpha, handlerRightAlpha, mFrameListView, mHandlerLeft, mHandlerRight, finalX);

                rlFrgmentCutFuncNormal.setVisibility(View.GONE);
                rlFrgmentCutFuncSelect.setVisibility(View.VISIBLE);

                if (action == MotionEvent.ACTION_UP) {
                    rlFrgmentCutFuncNormal.setVisibility(View.VISIBLE);
                    rlFrgmentCutFuncSelect.setVisibility(View.GONE);
                    calculateRange(handlerLeftAlpha, handlerRightAlpha, mHandlerLeft, mHandlerRight, mFrameListView, mDurationMs, getVideNum, baseview);
                }
                return true;
            }
        });

        mHandlerRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                float viewX = v.getX();
                float movedX = event.getX();
                float finalX = viewX + movedX;

                updateHandlerRightPosition(tvFrgmentCutTime, mDurationMs, mHandlerLeft, mHandlerRight, mFrameListView, finalX);

                rlFrgmentCutFuncNormal.setVisibility(View.GONE);
                rlFrgmentCutFuncSelect.setVisibility(View.VISIBLE);

                if (action == MotionEvent.ACTION_UP) {
                    rlFrgmentCutFuncNormal.setVisibility(View.VISIBLE);
                    rlFrgmentCutFuncSelect.setVisibility(View.GONE);
                    calculateRange(handlerLeftAlpha, handlerRightAlpha, mHandlerLeft, mHandlerRight, mFrameListView, mDurationMs, getVideNum, baseview);
                }

                return true;
            }
        });

        mFrameListView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onGlobalLayout() {
                mFrameListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                final int sliceEdge = mFrameListView.getWidth() / SLICE_COUNT;
                mSlicesTotalLength = sliceEdge * SLICE_COUNT;
//                Log.i(TAG, "slice edge: " + sliceEdge);
                final float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());

                new AsyncTask<Void, PLVideoFrame, Void>() {
                    @Override
                    protected Void doInBackground(Void... v) {
                        for (int i = 0; i < SLICE_COUNT; ++i) {
                            PLVideoFrame frame = mMediaFile.getVideoFrameByTime((long) ((1.0f * i / SLICE_COUNT) * mDurationMs), true, sliceEdge, sliceEdge);
                            publishProgress(frame);
                        }
                        return null;
                    }

                    @Override
                    protected void onProgressUpdate(PLVideoFrame... values) {
                        super.onProgressUpdate(values);
                        PLVideoFrame frame = values[0];
                        if (frame != null) {
                            View root = LayoutInflater.from(getActivity()).inflate(R.layout.frame_item, null);

                            int rotation = frame.getRotation();
                            ImageView thumbnail = (ImageView) root.findViewById(R.id.thumbnail);
                            thumbnail.setImageBitmap(frame.toBitmap());
                            thumbnail.setRotation(rotation);
                            FrameLayout.LayoutParams thumbnailLP = (FrameLayout.LayoutParams) thumbnail.getLayoutParams();
                            if (rotation == 90 || rotation == 270) {
                                thumbnailLP.leftMargin = thumbnailLP.rightMargin = (int) px;
                            } else {
                                thumbnailLP.topMargin = thumbnailLP.bottomMargin = (int) px;
                            }
                            thumbnail.setLayoutParams(thumbnailLP);

                            LinearLayout.LayoutParams rootLP = new LinearLayout.LayoutParams(sliceEdge, sliceEdge);
                            mFrameListView.addView(root, rootLP);
                        }
                    }
                }.execute();
            }
        });


    }

    private void updateHandlerLeftPosition(TextView tvFrgmentCutTime, long mDurationMs, View mHandlerLeftAlpha, View mHandlerRightAlpha, LinearLayout mFrameListView, View mHandlerLeft, View mHandlerRight, float movedPosition) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mHandlerLeft.getLayoutParams();
        if ((movedPosition + mHandlerLeft.getWidth()) > mHandlerRight.getX()) {
            lp.leftMargin = (int) (mHandlerRight.getX() - mHandlerLeft.getWidth());
        } else if (movedPosition < 0) {
            lp.leftMargin = 0;
        } else {
            lp.leftMargin = (int) movedPosition;
        }
        mHandlerLeft.setLayoutParams(lp);

        //使用滑动的阴影
        float beginPercent = 1.0f * ((mHandlerLeft.getX() + mHandlerLeft.getWidth() / 2) - mFrameListView.getX()) / mSlicesTotalLength;
        mHandlerLeftAlpha.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, beginPercent));
        mHandlerRightAlpha.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1 - beginPercent));

        //获取裁切的时间
        Long mSelectedBeginMs = (long) (beginPercent * mDurationMs);
        tvFrgmentCutTime.setText(mSelectedBeginMs / 1000 + "");
    }

    private void updateHandlerRightPosition(TextView tvFrgmentCutTime, long mDurationMs, View mHandlerLeft, View mHandlerRight, LinearLayout mFrameListView, float movedPosition) {
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

        //获取裁切的时间
        float endPercent = 1.0f * ((mHandlerRight.getX() + mHandlerRight.getWidth() / 2) - mFrameListView.getX()) / mSlicesTotalLength;
        Long mSelectedEndMs = (long) (endPercent * mDurationMs);
        tvFrgmentCutTime.setText(mSelectedEndMs / 1000 + "");
    }


    /**
     * 裁切范围
     *
     * @param mHandlerLeft
     * @param mHandlerRight
     * @param mFrameListView
     * @param mDurationMs
     */
    private void calculateRange(View mHandlerLeftAlpha, View mHandlerRightAlpha, View mHandlerLeft, View mHandlerRight, LinearLayout mFrameListView, long mDurationMs, int getVideNum, View baseview) {

        float beginPercent = 1.0f * ((mHandlerLeft.getX() + mHandlerLeft.getWidth() / 2) - mFrameListView.getX()) / mSlicesTotalLength;
        float endPercent = 1.0f * ((mHandlerRight.getX() + mHandlerRight.getWidth() / 2) - mFrameListView.getX()) / mSlicesTotalLength;
        beginPercent = QiniuTool.clamp(beginPercent);
        endPercent = QiniuTool.clamp(endPercent);


        Long mSelectedBeginMs = (long) (beginPercent * mDurationMs);
        Long mSelectedEndMs = (long) (endPercent * mDurationMs);
        Log.i(TAG, "begin percent: " + beginPercent + " end percent: " + endPercent);
        Log.i(TAG, "mDurationMs: " + mDurationMs);
        Log.i(TAG, "new range: " + mSelectedBeginMs + "-" + mSelectedEndMs);


        //重新保存视频数据。裁切作品和计算时间
        videobean mvideobean = new videobean();
        mvideobean.setStartTime(mSelectedBeginMs);
        mvideobean.setEndTime(mSelectedEndMs);
        mvideobean.setVideoUrl(getlocalVideo.get(getVideNum).getVideoUrl());
        mvideobean.setVideoSize((mSelectedEndMs - mSelectedBeginMs));
        getlocalVideo.set(getVideNum, mvideobean);
        // 当前的视频参数需要修改


        mDurationMsAll = 0;
        //总的进度条时间需要修改
        for (int i = 0; i < getlocalVideo.size(); i++) {
            mDurationMsAll = mDurationMsAll + getlocalVideo.get(i).getVideoSize();
        }
        MyLog.i(TAG, "mDurationMsAll=" + mDurationMsAll);//


//        ((BekidMainActivity) getActivity()).updateSeekBar();

        ((BekidMainActivity) getActivity()).reIdleResumePlay();

    }


    private String formatTime(long timeMs) {
        return String.format(Locale.CHINA, "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(timeMs),
                TimeUnit.MILLISECONDS.toSeconds(timeMs) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeMs))
        );
    }

    @Override
    public void onStop() {
        super.onStop();

//        ((BekidMainActivity) getActivity()).reIdleStartPlay();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
