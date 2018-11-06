/*
 * Copyright 2016 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tangxiaopeng.videoeditdemo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
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
import com.tangxiaopeng.videoeditdemo.utils.MyLog;
import com.tangxiaopeng.videoeditdemo.utils.QiniuTool;
import com.tangxiaopeng.videoeditdemo.view.RoundImageView;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tangxiaopeng.videoeditdemo.BekidMainActivity.mDurationMsAll;


/**
 * @author fanqie
 * @dec 父类封装 裁切
 * @date 2018/9/11 15:54
 */
public class MainAdapter extends BaseAdapter<MainAdapter.ViewHolder> {
    private static final String TAG = "MainAdapter";

    private SwipeMenuRecyclerView mMenuRecyclerView;
    private List<videobean> mDataVideoList;
    private Context mContext;
    private static final int SLICE_COUNT = 8;
    private int mSlicesTotalLength;

    private int isSelectPosition = 0;//判断点击的是那一个,默认第一个选中
    private int isSelectEditPosition = -1;//判断选中进行编辑操作的是那个，默认没有选中编辑

    int[] rlGetVideoHandlerPosition = new int[2];//获取裁切控件的的位置

    public MainAdapter(Context context, SwipeMenuRecyclerView menuRecyclerView) {
        super(context);
        this.mContext = context;
        this.mMenuRecyclerView = menuRecyclerView;
    }

    @Override
    public void notifyDataSetChanged(Object dataList) {
        this.mDataVideoList = (List<videobean>) dataList;
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataVideoList == null ? 0 : mDataVideoList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.including_video_cut, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        MyLog.i(TAG, "position=" + position);
        MyLog.i(TAG, "mRlVideoHandlerLeft.getWidth()=" + holder.mRlVideoHandlerLeft.getWidth());

        initVideoFrameList(holder, mDataVideoList.get(position).getGetAllTime(), new PLMediaFile(mDataVideoList.get(position).getVideoUrl()), position,holder.mRlVideoHandlerLeft);

//        SetDefaultLeftMargin(holder);

        onClickener mOnClickener = new onClickener(holder, position);
        holder.rlFrgmentCutEdit.setOnClickListener(mOnClickener);
        holder.rlFragmentCutProcess.setOnClickListener(mOnClickener);
        holder.tvFragmentCutDelete.setOnClickListener(mOnClickener);
        holder.tvFragmentCutCopy.setOnClickListener(mOnClickener);

        //holder.mLlIncludeCutVideo.setOnTouchListener(new OnTouchListener(holder));

    }

    boolean isShowEdit = false;//点击编辑时候，可以切换

    /**
     * @author fanqie
     * @dec 点击事件
     * @date 2018/9/12 11:54
     */
    class onClickener implements View.OnClickListener {
        ViewHolder holder;
        int position;

        public onClickener(ViewHolder holder, int position) {
            this.holder = holder;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rlFrgmentCutEdit:
                    //如果点击是当前的值
                    if (isSelectEditPosition == position) {
                        if (isShowEdit) {
                            isShowEdit = false;
                            isSelectEditPosition = -1;
                            holder.rlFrgmentCutFuncNormal.setVisibility(View.VISIBLE);
                            holder.rlFrgmentCutFuncSelect.setVisibility(View.GONE);
                            holder.rlFragmentCutProcess.setVisibility(View.VISIBLE);
                            holder.rlFrgmentCutEditShow.setVisibility(View.GONE);
                        } else {
                            isShowEdit = true;
                            isSelectEditPosition = position;
                            notifyDataSetChanged();
                        }
                    } else {
                        isShowEdit = true;
                        isSelectEditPosition = position;
                        notifyDataSetChanged();
                    }
                    break;
                case R.id.rlFragmentCutProcess:
                    isSelectPosition = position;
                    notifyDataSetChanged();
                    ((BekidMainActivity) mContext).updateSeekBar(isSelectPosition);
                    break;
                case R.id.tvFragmentCutDelete:
                    DragComplete();
                    mMenuRecyclerView.removeViewAt(position);//删除布局，防止在新增的时候，出现缓存
                    mDataVideoList.remove(position);
                    ((BekidMainActivity) mContext).resumeDurationMsAll();
                    ((BekidMainActivity) mContext).reIdleReStartPlay();
                    if (mDataVideoList.size() == 0) {
                        ((BekidMainActivity) mContext).finishs();
                    }

                    //因为以下这些控件是没有赋值的，但是UI是不一样的，所以为了防止有缓存，先处理
//                    holder.mFrameListView.removeAllViews();

//                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.mHandlerLeft.getLayoutParams();
//                    lp.leftMargin = 0;
//                    holder.mHandlerLeft.setLayoutParams(lp);//清除缓存
//
//
//                    RelativeLayout.LayoutParams lpright = (RelativeLayout.LayoutParams) holder.mHandlerRight.getLayoutParams();
//                    lpright.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
////                    lpright.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
////                    lpright.rightMargin= 0;
//                    holder.mHandlerRight.setLayoutParams(lpright);//清除缓存

                    notifyDataSetChanged();
                    ((BekidMainActivity) mContext).updateSeekBar(0);

                    break;
                case R.id.tvFragmentCutCopy:
                    DragComplete();
                    onUpdateDataListener.updateData(position);
                    notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * @author fanqie
     * @dec 如果想点击既可以拖动
     * @date 2018/9/12 11:53
     */
    class OnTouchListener implements View.OnTouchListener {
        ViewHolder holder;

        public OnTouchListener(ViewHolder holder) {
            this.holder = holder;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN: {
//                    mMenuRecyclerView.startDrag(holder);
                    break;
                }
                default:
                    break;
            }
            return false;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rivFrgmentCutEditNormal)
        RoundImageView rivFrgmentCutEditNormal;
        @BindView(R.id.tvFrgmentNumberNor)
        TextView mtvFrgmentNumberNor;
        @BindView(R.id.rlFrgmentCutFuncNormal)//左边的默认图层
                RelativeLayout rlFrgmentCutFuncNormal;
        @BindView(R.id.rivFrgmentCutEditSelect)
        RoundImageView rivFrgmentCutEditSelect;
        @BindView(R.id.tvFrgmentNumberSel)
        TextView tvFrgmentNumberSel;
        @BindView(R.id.tvFrgmentCutTime)
        TextView tvFrgmentCutTime;
        @BindView(R.id.rlFrgmentCutFuncSelect)
        RelativeLayout rlFrgmentCutFuncSelect; //左边的选中图层
        @BindView(R.id.rlFrgmentCutEdit)
        RelativeLayout rlFrgmentCutEdit; //左边的总布局
        @BindView(R.id.video_frame_list)
        LinearLayout mFrameListView;
        @BindView(R.id.handler_left_alpha)
        View handlerLeftAlpha;//滑动的时候，左边的裁切的需要加阴影
        @BindView(R.id.handler_left_alpha_other)
        View handlerRightAlpha;//滑动的时候，右边透明
        @BindView(R.id.handler_left)
        View mHandlerLeft;
        @BindView(R.id.handler_right)
        View mHandlerRight;
        @BindView(R.id.rlFragmentCutProcess)
        RelativeLayout rlFragmentCutProcess;//右边可以裁切视频的布局
        @BindView(R.id.tvFragmentCutDelete)
        TextView tvFragmentCutDelete;
        @BindView(R.id.tvFragmentCutSpeed)
        TextView mTvFragmentCutSpeed;
        @BindView(R.id.tvFragmentCutVoice)
        TextView mTvFragmentCutVoice;
        @BindView(R.id.tvFragmentRotate)
        TextView mTvFragmentRotate;
        @BindView(R.id.tvFragmentCutMirroring)
        TextView mTvFragmentCutMirroring;
        @BindView(R.id.tvFragmentCutInOut)
        TextView mTvFragmentCutInOut;
        @BindView(R.id.tvFragmentCutCopy)
        TextView tvFragmentCutCopy;
        @BindView(R.id.rlFrgmentCutEditShow)
        RelativeLayout rlFrgmentCutEditShow;
        @BindView(R.id.tvFragmentCutOtherDelete)
        TextView mTvFragmentCutOtherDelete;
        @BindView(R.id.tvFragmentCutOtherCopy)
        TextView mTvFragmentCutOtherCopy;
        @BindView(R.id.tvFragmentCutOtherTry)
        TextView mTvFragmentCutOtherTry;
        @BindView(R.id.rlFrgmentCutEditOtherShow)
        RelativeLayout mRlFrgmentCutEditOtherShow;
        @BindView(R.id.llIncludeCutVideo)
        LinearLayout mLlIncludeCutVideo;
        @BindView(R.id.rlVideoHandlerLeft)
        RelativeLayout mRlVideoHandlerLeft;
        @BindView(R.id.rlGetVideoHandler)
        RelativeLayout rlGetVideoHandler;
        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

    /**
     * 滚动器
     */
    private void initVideoFrameList(final ViewHolder holder, final long mDurationMs, final PLMediaFile mMediaFile, final int position, final RelativeLayout mRlVideoHandlerLeft) {

        //获取视频的第一帧封面
//        Bitmap getOneBitmap = ImageUtil.getVideoThumbnail(mDataVideoList.get(position).getVideoUrl(),
//                UnitConversionTool.dip2px(mContext, 48), UnitConversionTool.dip2px(mContext, 48), MICRO_KIND);
//        holder.rivFrgmentCutEditNormal.setImageBitmap(getOneBitmap);

        //左边的选中图层
        holder.rivFrgmentCutEditSelect.setImageResource(R.drawable.bg_index_color);

        holder.mtvFrgmentNumberNor.setText((position + 1) + "");
        holder.tvFrgmentNumberSel.setText((position + 1) + "");

        if (isSelectPosition == position) {
            holder.mHandlerLeft.setBackgroundResource(R.drawable.a5);
            holder.mHandlerRight.setBackgroundResource(R.drawable.a4);
        } else {
            holder.mHandlerLeft.setBackgroundResource(R.drawable.a3);
            holder.mHandlerRight.setBackgroundResource(R.drawable.a2);
        }

        if (isSelectEditPosition == position) {
            holder.rlFrgmentCutFuncNormal.setVisibility(View.GONE);
            holder.rlFrgmentCutFuncSelect.setVisibility(View.VISIBLE);

            holder.rlFragmentCutProcess.setVisibility(View.GONE);
            holder.rlFrgmentCutEditShow.setVisibility(View.VISIBLE);
        } else {
            holder.rlFrgmentCutFuncNormal.setVisibility(View.VISIBLE);
            holder.rlFrgmentCutFuncSelect.setVisibility(View.GONE);

            holder.rlFragmentCutProcess.setVisibility(View.VISIBLE);
            holder.rlFrgmentCutEditShow.setVisibility(View.GONE);
        }

        //拖动左边
        holder.mHandlerLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                float viewX = v.getX();//相对于父类的x的坐标
                float movedX = event.getX();//getX()即表示的点击的位置相对于本身的坐标 ,getX()会突然变大，导致偏移??????????????
//                float finalX = viewX + movedX;
                holder.rlGetVideoHandler.getLocationInWindow(rlGetVideoHandlerPosition);
                float finalX = event.getRawX()-rlGetVideoHandlerPosition[0];
                //滑动控件的位置-视频区域的位置，就是滑动控件位于视频区域的偏移量

                updateHandlerLeftPosition(holder.tvFrgmentCutTime, mDurationMs, holder.handlerLeftAlpha, holder.handlerRightAlpha, holder.mFrameListView, holder.mHandlerLeft, holder.mHandlerRight, finalX,mRlVideoHandlerLeft,mSlicesTotalLength);

                if(action==MotionEvent.ACTION_DOWN){
                    MyLog.i(TAG,"ACTION_DOWN");
                    holder.rlFrgmentCutFuncNormal.setVisibility(View.GONE);
                    holder.rlFrgmentCutFuncSelect.setVisibility(View.VISIBLE);
                }
                if (action == MotionEvent.ACTION_UP) {
                    MyLog.i(TAG,"ACTION_UP");
                    holder.rlFrgmentCutFuncNormal.setVisibility(View.VISIBLE);
                    calculateRange(holder.handlerLeftAlpha, holder.handlerRightAlpha, holder.mHandlerLeft, holder.mHandlerRight, holder.mFrameListView, mDurationMs, position);
                }
                return true;
            }
        });

        //拖动右边
        holder.mHandlerRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                float viewX = v.getX();
                float movedX = event.getX();
//                float finalX = viewX + movedX;
                holder.rlGetVideoHandler.getLocationInWindow(rlGetVideoHandlerPosition);

                MyLog.i(TAG,"viewX="+viewX);
                MyLog.i(TAG,"movedX="+movedX);
                MyLog.i(TAG,"event.getRawX()="+event.getRawX());
                MyLog.i(TAG,"rlGetVideoHandlerPosition[0]="+rlGetVideoHandlerPosition[0]);

                float finalX = event.getRawX()-rlGetVideoHandlerPosition[0];
                //滑动控件的位置-视频区域的位置，就是滑动控件位于视频区域的偏移量

                updateHandlerRightPosition(holder.tvFrgmentCutTime, mDurationMs, holder.mHandlerLeft, holder.mHandlerRight, holder.mFrameListView, finalX,mSlicesTotalLength);
                if(action==MotionEvent.ACTION_DOWN){
                    holder.rlFrgmentCutFuncNormal.setVisibility(View.GONE);
                    holder.rlFrgmentCutFuncSelect.setVisibility(View.VISIBLE);
                }

                if (action == MotionEvent.ACTION_UP) {
                    holder.rlFrgmentCutFuncNormal.setVisibility(View.VISIBLE);
                    holder.rlFrgmentCutFuncSelect.setVisibility(View.GONE);
                    calculateRange(holder.handlerLeftAlpha, holder.handlerRightAlpha, holder.mHandlerLeft, holder.mHandlerRight, holder.mFrameListView, mDurationMs, position);
                }
                return true;
            }
        });

        holder.mFrameListView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onGlobalLayout() {
                holder.mFrameListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                final int sliceEdge = holder.mFrameListView.getWidth() / SLICE_COUNT;
                mSlicesTotalLength = sliceEdge * SLICE_COUNT;
//                Log.i(TAG, "slice edge: " + sliceEdge);
                final float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, mContext.getResources().getDisplayMetrics());

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
                            View root = LayoutInflater.from(mContext).inflate(R.layout.frame_item, null);

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
                            holder.mFrameListView.addView(root, rootLP);
                        }
                    }
                }.execute();
            }
        });


    }


    /**
     * 获取到裁切范围
     *
     * @param mHandlerLeft
     * @param mHandlerRight
     * @param mFrameListView
     * @param mDurationMs
     */
    private void calculateRange(View mHandlerLeftAlpha, View mHandlerRightAlpha, View mHandlerLeft, View mHandlerRight, LinearLayout mFrameListView, long mDurationMs, int position) {

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
        mvideobean.setVideoUrl(mDataVideoList.get(position).getVideoUrl());
        mvideobean.setVideoSize((mSelectedEndMs - mSelectedBeginMs));
        mvideobean.setGetAllTime(mDurationMs);
        mDataVideoList.set(position, mvideobean);
        // 当前的视频参数需要修改


        mDurationMsAll = 0;
        //总的进度条时间需要修改
        for (int i = 0; i < mDataVideoList.size(); i++) {
            mDurationMsAll = mDurationMsAll + mDataVideoList.get(i).getVideoSize();
        }
        MyLog.i(TAG, "mDurationMsAll=" + mDurationMsAll);//

        ((BekidMainActivity) mContext).updateSeekBar(position);
        ((BekidMainActivity) mContext).reIdleReStartPlay();
    }

    /**
     * 拖拽完成后应该清空，默认为第一个
     */
    public void DragComplete() {
        isSelectPosition = 0;
        isSelectEditPosition = -1;
        isShowEdit = false;

    }


    public OnUpdateDataListener onUpdateDataListener;

    public void setOnUpdateDataListener(OnUpdateDataListener listener) {
        this.onUpdateDataListener = listener;
    }

    /**
     * 使用回调，观察者模式，比较好用
     */
    public interface OnUpdateDataListener {
        public void updateData(int position);
    }

}
