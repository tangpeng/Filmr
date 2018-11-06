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
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tangxiaopeng.videoeditdemo.BekidMainActivity;
import com.tangxiaopeng.videoeditdemo.R;
import com.tangxiaopeng.videoeditdemo.bean.EditTextbean;
import com.tangxiaopeng.videoeditdemo.utils.MyLog;
import com.tangxiaopeng.videoeditdemo.utils.QiniuTool;
import com.tangxiaopeng.videoeditdemo.view.RoundImageView;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tangxiaopeng.videoeditdemo.BekidMainActivity.mDurationMsAll;
import static com.tangxiaopeng.videoeditdemo.fragment.BekidTextFragment.mDataTextList;

/**
 *  @dec 添加文字
 *  @author fanqie
 *  @date  2018/9/12 15:12
 */
public class AddTextAdapter extends BaseAdapter<AddTextAdapter.ViewHolder> {
    private static final String TAG = "AddTextAdapter";
    private SwipeMenuRecyclerView mMenuRecyclerView;

    private Context mContext;
    private static final int SLICE_COUNT = 8;
    private int mSlicesTotalLength;

    private int isSelectPosition = 0;//判断点击的是那一个,默认第一个选中
    private int isSelectEditPosition = -1;//判断选中进行编辑操作的是那个，默认没有选中编辑


    int[] rlGetVideoHandlerPosition = new int[2];//获取裁切控件的的位置

    public AddTextAdapter(Context context, SwipeMenuRecyclerView menuRecyclerView, int type) {
        super(context);
        this.mContext = context;
        this.mMenuRecyclerView = menuRecyclerView;
    }

    @Override
    public void notifyDataSetChanged(Object dataList) {
//        this.mDataTextList = (List<EditTextbean>) dataList;
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataTextList == null ? 0 : mDataTextList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.including_video_cut, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyLog.i(TAG, "position=" + position);

        holder.mRlFrgmentCutEditOtherShow.setVisibility(View.VISIBLE);
        holder.rlFrgmentCutEditShow.setVisibility(View.GONE);

        initVideoFrameList(holder,mDurationMsAll, position,holder.mRlVideoHandlerLeft);//这是总时间哦

        onClickener mOnClickener = new onClickener(holder, position);
        holder.rlFrgmentCutEdit.setOnClickListener(mOnClickener);
        holder.rlFragmentCutProcess.setOnClickListener(mOnClickener);
        holder.tvFragmentCutOtherDelete.setOnClickListener(mOnClickener);
        holder.tvFragmentCutOtherCopy.setOnClickListener(mOnClickener);

        holder.tvFragmentCutOtherTry.setVisibility(View.INVISIBLE);

    }

    /**
     * 滚动器
     */
    private boolean isPlay = false;
    boolean isShowEdit=false;//点击编辑时候，可以切换
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
                case R.id.rlFrgmentCutEdit://编辑
                    //如果点击是当前的值
                    if(isSelectEditPosition== position){
                        if(isShowEdit){
                            isShowEdit=false;
                            isSelectEditPosition=-1;
                            holder.rlFrgmentCutFuncNormal.setVisibility(View.VISIBLE);
                            holder.rlFrgmentCutFuncSelect.setVisibility(View.GONE);
                            holder.rlFragmentCutProcess.setVisibility(View.VISIBLE);
                            holder.mRlFrgmentCutEditOtherShow.setVisibility(View.GONE);
                        }else{
                            isShowEdit=true;
                            isSelectEditPosition = position;
                            notifyDataSetChanged();
                        }
                    }else{
                        isShowEdit=true;
                        isSelectEditPosition = position;
                        notifyDataSetChanged();
                    }

                    break;
                case R.id.rlFragmentCutProcess://点击选中，颜色加深
                    isSelectPosition = position;
                    notifyDataSetChanged();
                    break;
                case R.id.tvFragmentCutOtherDelete://删除
                    DragComplete();
                    mMenuRecyclerView.removeViewAt(position);//删除布局，防止在新增的时候，出现缓存

                    ((BekidMainActivity) mContext).DeleteCurrentText(position, mDataTextList.get(position).getStrokedTextView());
                    mDataTextList.remove(position);
                    notifyDataSetChanged();

                    break;
                case R.id.tvFragmentCutOtherCopy://复制
                    DragComplete();
                    onUpdateDataListener.updateData(position);
                    notifyDataSetChanged();
                    break;

                default:
                    break;
            }
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
        TextView tvFragmentCutOtherDelete;
        @BindView(R.id.tvFragmentCutOtherCopy)
        TextView tvFragmentCutOtherCopy;
        @BindView(R.id.tvFragmentCutOtherTry)
        TextView tvFragmentCutOtherTry;
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
    private void initVideoFrameList(final ViewHolder holder, final long mDurationMs, final int position,final RelativeLayout mRlVideoHandlerLeft) {

        //获取视频的第一帧封面
//        Bitmap getOneBitmap = ImageUtil.getVideoThumbnail(mDataTextList.get(position).getVideoUrl(),
//                UnitConversionTool.dip2px(mContext, 48), UnitConversionTool.dip2px(mContext, 48), MICRO_KIND);
//        holder.rivFrgmentCutEditNormal.setImageBitmap(getOneBitmap);

        holder.mtvFrgmentNumberNor.setText(mDataTextList.get(position).getStrokedTextView().getText());
        holder.mtvFrgmentNumberNor.setTextColor(Color.BLACK);//显示黑色字体

        //左边的选中图层
        holder.rivFrgmentCutEditSelect.setImageResource(R.drawable.bg_index_color);

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
            holder.mRlFrgmentCutEditOtherShow.setVisibility(View.VISIBLE);
        } else {
            holder.rlFrgmentCutFuncNormal.setVisibility(View.VISIBLE);
            holder.rlFrgmentCutFuncSelect.setVisibility(View.GONE);

            holder.rlFragmentCutProcess.setVisibility(View.VISIBLE);
            holder.mRlFrgmentCutEditOtherShow.setVisibility(View.GONE);
        }


        //拖动左边
        holder.mHandlerLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                float viewX = v.getX();
                float movedX = event.getX();
                //float finalX = viewX + movedX;
                holder.rlGetVideoHandler.getLocationInWindow(rlGetVideoHandlerPosition);
                float finalX = event.getRawX()-rlGetVideoHandlerPosition[0];
                //滑动控件的位置-视频区域的位置，就是滑动控件位于视频区域的偏移量

                updateHandlerLeftPosition(holder.tvFrgmentCutTime, mDurationMs, holder.handlerLeftAlpha, holder.handlerRightAlpha, holder.mFrameListView, holder.mHandlerLeft, holder.mHandlerRight, finalX,mRlVideoHandlerLeft,mSlicesTotalLength);

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

        //拖动右边
        holder.mHandlerRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                float viewX = v.getX();
                float movedX = event.getX();
                //float finalX = viewX + movedX;
                holder.rlGetVideoHandler.getLocationInWindow(rlGetVideoHandlerPosition);
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
            }
        });

    }


    /**
     * 裁切范围
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


        EditTextbean mEditTextbean = new EditTextbean();
        mEditTextbean.setStrokedTextView(mDataTextList.get(position).getStrokedTextView());

        mDataTextList.get(position).setStartTime(mSelectedBeginMs);
        mDataTextList.get(position).setEndTime(mSelectedEndMs);

        ((BekidMainActivity)mContext).updateTextSeekTime(position,beginPercent,endPercent);//修改在进度条中的进度
        ((BekidMainActivity)mContext).reIdleReStartPlay();
    }

    /**
     * 拖拽完成后应该清空
     */
    public void DragComplete(){
        isSelectPosition=0;
        isSelectEditPosition=-1;
        isShowEdit=false;
    }


    public OnUpdateDataListener onUpdateDataListener;

    public void setOnUpdateDataListener(OnUpdateDataListener listener){
        this.onUpdateDataListener=listener;
    }
    /**
     * 使用回调，观察者模式，比较好用
     */
    public interface OnUpdateDataListener{
        public void updateData(int position);
    }

}
