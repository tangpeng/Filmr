package com.tangxiaopeng.videoeditdemo.bean;


import com.tangxiaopeng.videoeditdemo.view.StrokedTextView;

import java.io.Serializable;

/**
 *  @dec
 *  @author fanqie
 *  @date  2018/8/24 14:32
 */
public class EditTextbean implements Serializable {

    private StrokedTextView mStrokedTextView;
    private long startTime;
    private long endTime;

    public StrokedTextView getStrokedTextView() {
        return mStrokedTextView;
    }

    public void setStrokedTextView(StrokedTextView strokedTextView) {
        mStrokedTextView = strokedTextView;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "EditTextbean{" +
                "mStrokedTextView=" + mStrokedTextView +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
