/*
 * Copyright 2017 Yan Zhenjie
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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tangxiaopeng.videoeditdemo.utils.MyLog;
import com.tangxiaopeng.videoeditdemo.utils.Tools;

/**
 * @author fanqie
 * @dec 父类封装一下
 * @date 2018/9/11 15:54
 */
public abstract class BaseAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private final String TAG = "BASEADAPTER";
    private LayoutInflater mInflater;

    public BaseAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    public LayoutInflater getInflater() {
        return mInflater;
    }

    public abstract void notifyDataSetChanged(Object dataList);


    public void updateHandlerLeftPosition(TextView tvFrgmentCutTime, long mDurationMs, View mHandlerLeftAlpha, View mHandlerRightAlpha, LinearLayout mFrameListView, View mHandlerLeft, View mHandlerRight, float movedPosition, RelativeLayout mRlVideoHandlerLeft, int mSlicesTotalLength) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mHandlerLeft.getLayoutParams();
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);//因为需要有阴影效果，所以靠右对齐,这个时候的起始点其实是图标的右边

        if ((movedPosition) > mHandlerRight.getX()) {//这个时候的起始点其实是图标的右边
            lp.rightMargin = (int) (mRlVideoHandlerLeft.getWidth() - mHandlerRight.getX());
        } else if (movedPosition < mHandlerLeft.getWidth()) {
            lp.rightMargin = mRlVideoHandlerLeft.getWidth() - (mHandlerLeft.getWidth());
        } else {
            lp.rightMargin = (int) (mRlVideoHandlerLeft.getWidth() - movedPosition);
        }
        mHandlerLeft.setLayoutParams(lp);

        //使用滑动的阴影
        float beginPercent = 1.0f * ((mHandlerLeft.getX() + mHandlerLeft.getWidth() / 2) - mFrameListView.getX()) / mSlicesTotalLength;
        MyLog.i(TAG, "beginPercent=" + beginPercent);

        //获取裁切的时间
        Long mSelectedBeginMs = (long) (beginPercent * mDurationMs);
        tvFrgmentCutTime.setText(Tools.getTimeZone(mSelectedBeginMs) + "");
    }

    public void updateHandlerRightPosition(TextView tvFrgmentCutTime, long mDurationMs, View mHandlerLeft, View mHandlerRight, LinearLayout mFrameListView, float movedPosition, int mSlicesTotalLength) {
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
        tvFrgmentCutTime.setText(Tools.getTimeZone(mSelectedEndMs) + "");
    }

    public void SetDefaultLeftMargin(MainAdapter.ViewHolder holder) {
        //设置一个默认值
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.mHandlerLeft.getLayoutParams();
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);//因为需要有阴影效果，所以靠右对齐,这个时候的起始点其实是图标的右边
        lp.rightMargin = holder.mRlVideoHandlerLeft.getWidth() - (holder.mHandlerLeft.getWidth());
        holder.mHandlerLeft.setLayoutParams(lp);
    }

//
//    public OnScrollListener onOnScrollListener;
//    public void setOnScrollListener(OnScrollListener listener) {
//        this.onOnScrollListener = listener;
//    }
//    /**
//     * 使用回调，观察者模式，比较好用
//     */
//    public interface OnScrollListener {
//        public void update(boolean canScroll);
//    }

}
