package com.tangxiaopeng.videoeditdemo.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.qiniu.pili.droid.shortvideo.PLMediaFile;
import com.tangxiaopeng.videoeditdemo.BekidMainActivity;
import com.tangxiaopeng.videoeditdemo.R;
import com.tangxiaopeng.videoeditdemo.bean.videobean;
import com.tangxiaopeng.videoeditdemo.utils.GetPathFromUri;
import com.tangxiaopeng.videoeditdemo.utils.MyLog;
import com.tangxiaopeng.videoeditdemo.utils.QiniuTool;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.tangxiaopeng.videoeditdemo.BekidMainActivity.mDurationMsAll;
import static com.tangxiaopeng.videoeditdemo.utils.Tools.getlocalVideo;


/**
 * bekid
 * Created by JY
 * Date：17/2/20
 * Time：下午5:01
 */
public class BekidNewFragment extends BaseFragment implements View.OnClickListener {

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
    public static BekidNewFragment getInstance(String data) {
        BekidNewFragment rightFragment = new BekidNewFragment();
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
        MyLog.i("tangpeng","init");
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
            ((BekidMainActivity) getActivity()).reIdleReStartPlay();
        }

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

        ((BekidMainActivity) getActivity()).AddLocalDivisionVideo(mMediaFile);

        //从0开始
//        initVideoFrameList(addViewtopFilmr, mMediaFile.getDurationMs(), mMediaFile, getlocalVideo.size() - 1);
        Log.i(TAG, "getDurationMs=" + mMediaFile.getDurationMs());

    }

//
//    /**
//     * 滚动器
//     */
//    private void initVideoFrameList(final View baseview, final long mDurationMs, final PLMediaFile mMediaFile, final int getVideNum) {
//        final RelativeLayout rlFragmentCutProcess;
//        final LinearLayout mFrameListView;
//        final View mHandlerLeft;
//        final View mHandlerRight;
//        final View handlerLeftAlpha;//滑动的时候，左边的裁切的需要加阴影
//        final View handlerRightAlpha;//滑动的时候，右边透明
//        final boolean[] isShow = {true};
//
//        rlFragmentCutProcess = (RelativeLayout) baseview.findViewById(R.id.rlFragmentCutProcess);
//        mFrameListView = (LinearLayout) baseview.findViewById(R.id.video_frame_list);
//        mHandlerLeft = baseview.findViewById(R.id.handler_left);
//        mHandlerRight = baseview.findViewById(R.id.handler_right);
//        handlerLeftAlpha = baseview.findViewById(R.id.handler_left_alpha);
//        handlerRightAlpha = baseview.findViewById(R.id.handler_left_alpha_other);
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
//                lltopFilmr.removeAllViews();
//                ((BekidMainActivity) getActivity()).deleteVideo(getVideNum);
//
//                MyLog.i(TAG, "添加：getlocalVideoDelete.size()=" + getlocalVideoDelete.size());
//                for (int i = 0; i < getlocalVideoDelete.size(); i++) {
//                    init(getlocalVideoDelete.get(i).getVideoUrl());
//                }
//
//                ((BekidMainActivity) getActivity()).reIdleStartPlay();
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
//                    calculateRange(handlerLeftAlpha,handlerRightAlpha,mHandlerLeft, mHandlerRight, mFrameListView, mDurationMs, getVideNum, baseview);
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
//                    calculateRange(handlerLeftAlpha,handlerRightAlpha,mHandlerLeft, mHandlerRight, mFrameListView, mDurationMs, getVideNum, baseview);
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
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
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




    /**
     * 裁切范围
     *
     * @param mHandlerLeft
     * @param mHandlerRight
     * @param mFrameListView
     * @param mDurationMs
     */
    private void calculateRange(View mHandlerLeftAlpha,View mHandlerRightAlpha,View mHandlerLeft, View mHandlerRight, LinearLayout mFrameListView, long mDurationMs, int getVideNum, View baseview) {

        float beginPercent = 1.0f * ((mHandlerLeft.getX() + mHandlerLeft.getWidth() / 2) - mFrameListView.getX()) / mSlicesTotalLength;
        float endPercent = 1.0f * ((mHandlerRight.getX() + mHandlerRight.getWidth() / 2) - mFrameListView.getX()) / mSlicesTotalLength;
        beginPercent = QiniuTool.clamp(beginPercent);
        endPercent = QiniuTool.clamp(endPercent);


        Long mSelectedBeginMs = (long) (beginPercent * mDurationMs);
        Long mSelectedEndMs = (long) (endPercent * mDurationMs);
        Log.i(TAG, "begin percent: " + beginPercent + " end percent: " + endPercent);
        Log.i(TAG, "getVideNum: " + getVideNum);

        mHandlerLeftAlpha.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,beginPercent));
        mHandlerRightAlpha.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,1-beginPercent));

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

//        ((BekidMainActivity) getActivity()).updateSeekBar();

        Log.i(TAG, "new range: " + mSelectedBeginMs + "-" + mSelectedEndMs);
//        TextView range = (TextView) baseview.findViewById(R.id.range);
//        range.setText(formatTime(mSelectedBeginMs) + " - " + formatTime(mSelectedEndMs));

        ((BekidMainActivity) getActivity()).resumePlay();

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
        ((BekidMainActivity) getActivity()).reIdleReStartPlay();
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
