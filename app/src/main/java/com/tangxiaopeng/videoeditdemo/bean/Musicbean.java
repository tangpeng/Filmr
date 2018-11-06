package com.tangxiaopeng.videoeditdemo.bean;

import java.io.Serializable;

/**
 *  @dec
 *  @author fanqie
 *  @date  2018/8/24 14:32
 */
public class Musicbean implements Serializable {

    private String musicUrl;
    private Long musicSize;
    private long startTime;
    private long endTime;
    private long startInsertTime;//从什么时候开始播放或者显示
    private long getAllTime;//原文件的总时间，没有裁切之前的
    public String getMusicUrl() {
        return musicUrl;
    }

    public void setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
    }

    public Long getMusicSize() {
        return musicSize;
    }

    public void setMusicSize(Long musicSize) {
        this.musicSize = musicSize;
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
        return "Musicbean{" +
                "musicUrl='" + musicUrl + '\'' +
                ", musicSize=" + musicSize +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", startInsertTime=" + startInsertTime +
                ", getAllTime=" + getAllTime +
                '}';
    }

    public long getGetAllTime() {
        return getAllTime;
    }

    public void setGetAllTime(long getAllTime) {
        this.getAllTime = getAllTime;
    }

    public long getStartInsertTime() {
        return startInsertTime;
    }

    public void setStartInsertTime(long startInsertTime) {
        this.startInsertTime = startInsertTime;
    }
}
