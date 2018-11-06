package com.tangxiaopeng.videoeditdemo.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.qiniu.pili.droid.shortvideo.PLMediaFile;
import com.qiniu.pili.droid.shortvideo.PLVideoFrame;
import com.tangxiaopeng.videoeditdemo.R;
import com.tangxiaopeng.videoeditdemo.utils.MyLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.tangxiaopeng.videoeditdemo.utils.QiniuTool.clamp;


/**
 */
public class BekidSwitchFragment extends Fragment implements View.OnClickListener {

    public static String CURRENT_PAGE = "current_page";
    private static final String TAG = "BekidPanelFragment";
    Unbinder unbinder;
    @BindView(R.id.video_frame_lists)
    LinearLayout mVideoFrameList;
    @BindView(R.id.handler_lefts)
    View mHandlerLeft;
    @BindView(R.id.handler_rights)
    View mHandlerRight;

    private long delayMillis = 10;//
    private int maxProcess = 200;
    double speeds = 0;

    private PLMediaFile mMediaFile;


    private long mSelectedBeginMs;
    private long mSelectedEndMs;
    private long mDurationMs;

    private int mVideoFrameCount;
    private int mSlicesTotalLength;
    private static final int SLICE_COUNT = 8;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_switch_common, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MyLog.i(TAG, "onActivityCreated");
//        mMediaFile = new PLMediaFile(mDataVideoList.get(0).getVideoUrl());
//        initVideoFrameList();
    }

    @Override
    public void onPause() {
        super.onPause();
        MyLog.i(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        MyLog.i(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.i(TAG, "onResume");
    }

    @Override
    public void onClick(View v) {

    }


    private void initVideoFrameList() {

        MyLog.i(TAG, "initVideoFrameList");
        mHandlerLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MyLog.i(TAG, "onTouch");
                int action = event.getAction();
                float viewX = v.getX();
                float movedX = event.getX();
                float finalX = viewX + movedX;
                MyLog.i(TAG, "movedX=" + movedX);
                updateHandlerLeftPosition(finalX);

                if (action == MotionEvent.ACTION_UP) {
                    calculateRange();
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
                MyLog.i(TAG, "movedX=" + movedX);
                updateHandlerRightPosition(finalX);

                if (action == MotionEvent.ACTION_UP) {
                    calculateRange();
                }

                return true;
            }
        });

        mVideoFrameList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mVideoFrameList.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                final int sliceEdge = mVideoFrameList.getWidth() / SLICE_COUNT;
                mSlicesTotalLength = sliceEdge * SLICE_COUNT;
                Log.i(TAG, "slice edge: " + sliceEdge);
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
                            mVideoFrameList.addView(root, rootLP);
                        }
                    }
                }.execute();
            }
        });
    }

    private void updateHandlerLeftPosition(float movedPosition) {
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

    private void updateHandlerRightPosition(float movedPosition) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mHandlerRight.getLayoutParams();
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        if (movedPosition < (mHandlerLeft.getX() + mHandlerLeft.getWidth())) {
            lp.leftMargin = (int) (mHandlerLeft.getX() + mHandlerLeft.getWidth());
        } else if ((movedPosition + (mHandlerRight.getWidth() / 2)) > (mVideoFrameList.getX() + mSlicesTotalLength)) {
            lp.leftMargin = (int) ((mVideoFrameList.getX() + mSlicesTotalLength) - (mHandlerRight.getWidth() / 2));
        } else {
            lp.leftMargin = (int) movedPosition;
        }
        mHandlerRight.setLayoutParams(lp);
    }


    private void calculateRange() {
        float beginPercent = 1.0f * ((mHandlerLeft.getX() + mHandlerLeft.getWidth() / 2) - mVideoFrameList.getX()) / mSlicesTotalLength;
        float endPercent = 1.0f * ((mHandlerRight.getX() + mHandlerRight.getWidth() / 2) - mVideoFrameList.getX()) / mSlicesTotalLength;
        beginPercent = clamp(beginPercent);
        endPercent = clamp(endPercent);

        Log.i(TAG, "begin percent: " + beginPercent + " end percent: " + endPercent);

        mSelectedBeginMs = (long) (beginPercent * mDurationMs);
        mSelectedEndMs = (long) (endPercent * mDurationMs);

        Log.i(TAG, "new range: " + mSelectedBeginMs + "-" + mSelectedEndMs);
    }
}
