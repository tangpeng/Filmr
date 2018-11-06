package com.tangxiaopeng.videoeditdemo.bean;

import com.qiniu.pili.droid.shortvideo.PLImageView;

import java.io.Serializable;

/**
 *  @dec
 *  @author fanqie
 *  @date  2018/8/24 14:32
 */
public class EditChartbean implements Serializable {
    private PLImageView imageView;
    private int ivImageId;
    private long startTime;
    private long endTime;

    public PLImageView getImageView() {
        return imageView;
    }

    public void setImageView(PLImageView imageView) {
        this.imageView = imageView;
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

    public int getIvImageId() {
        return ivImageId;
    }

    public void setIvImageId(int ivImageId) {
        this.ivImageId = ivImageId;
    }
}
