package com.tangxiaopeng.videoeditdemo.bean;

import com.qiniu.pili.droid.shortvideo.PLPaintView;

import java.io.Serializable;

/**
 *  @dec  标志
 *  @author fanqie
 *  @date  2018/9/20 15:00
 */
public class EditPanelbean implements Serializable {

    private PLPaintView mPLPaintView;
    private long startTime;
    private long endTime;

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

    public PLPaintView getPLPaintView() {
        return mPLPaintView;
    }

    public void setPLPaintView(PLPaintView PLPaintView) {
        mPLPaintView = PLPaintView;
    }

    @Override
    public String toString() {
        return "EditPanelbean{" +
                "mPLPaintView=" + mPLPaintView +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
