package com.tangxiaopeng.videoeditdemo.bean;

import java.io.Serializable;

/**
 * ProjectName:PLDroidShortVideoDemo
 * Date:2018/8/6 16:28
 *
 * @author fanqiejiang
 */

public class videobean implements Serializable {
    private String videoUrl;
    private long videoSize;//视频总时间
    private long startTime;//起始播放时间
    private long endTime;//结束播放时间
    private long getAllTime;//原视频的总时间，没有裁切之前的

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Long getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(Long videoSize) {
        this.videoSize = videoSize;
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

    public void setVideoSize(long videoSize) {
        this.videoSize = videoSize;
    }

    public long getGetAllTime() {
        return getAllTime;
    }

    public void setGetAllTime(long getAllTime) {
        this.getAllTime = getAllTime;
    }

    @Override
    public String toString() {
        return "videobean{" +
                "videoUrl='" + videoUrl + '\'' +
                ", videoSize=" + videoSize +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", getAllTime=" + getAllTime +
                '}';
    }
}
