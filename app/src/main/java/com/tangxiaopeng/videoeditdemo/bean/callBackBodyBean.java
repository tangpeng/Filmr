package com.tangxiaopeng.videoeditdemo.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * ProjectName:VideoEditDemo20181011
 * Date:2018/10/26 16:19
 *
 * @author fanqiejiang
 */

public class callBackBodyBean implements Parcelable{

    /**
     * appTags : eysai_app
     * duration : 25.780999
     * fId : ub94fdibthd
     * fPrimitiveName : seek_exact.mp4
     * fType : VIDEO
     * fidPrimitive : ub94fdibthc
     * height : 640
     * mimeType : video/mp4
     * qEtag : FmRvwvzCsxqWgvZuBYkM8BnHaTx-
     * qPersistId : z0.5bd2c6ac38b9f349c840bdac
     * size : 2450424
     * upDevice : app_android
     * useFor : VID_COMP
     * userId : abc
     * vDspRatio : 9:16
     * width : 360
     */

    private String appTags;
    private String duration;
    private String fId;
    private String fPrimitiveName;
    private String fType;
    private String fidPrimitive;
    private String height;
    private String mimeType;
    private String qEtag;
    private String qPersistId;
    private String size;
    private String upDevice;
    private String useFor;
    private String userId;
    private String vDspRatio;
    private String width;

    protected callBackBodyBean(Parcel in) {
        appTags = in.readString();
        duration = in.readString();
        fId = in.readString();
        fPrimitiveName = in.readString();
        fType = in.readString();
        fidPrimitive = in.readString();
        height = in.readString();
        mimeType = in.readString();
        qEtag = in.readString();
        qPersistId = in.readString();
        size = in.readString();
        upDevice = in.readString();
        useFor = in.readString();
        userId = in.readString();
        vDspRatio = in.readString();
        width = in.readString();
    }

    public static final Creator<callBackBodyBean> CREATOR = new Creator<callBackBodyBean>() {
        @Override
        public callBackBodyBean createFromParcel(Parcel in) {
            return new callBackBodyBean(in);
        }

        @Override
        public callBackBodyBean[] newArray(int size) {
            return new callBackBodyBean[size];
        }
    };

    public String getAppTags() {
        return appTags;
    }

    public void setAppTags(String appTags) {
        this.appTags = appTags;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getFId() {
        return fId;
    }

    public void setFId(String fId) {
        this.fId = fId;
    }

    public String getFPrimitiveName() {
        return fPrimitiveName;
    }

    public void setFPrimitiveName(String fPrimitiveName) {
        this.fPrimitiveName = fPrimitiveName;
    }

    public String getFType() {
        return fType;
    }

    public void setFType(String fType) {
        this.fType = fType;
    }

    public String getFidPrimitive() {
        return fidPrimitive;
    }

    public void setFidPrimitive(String fidPrimitive) {
        this.fidPrimitive = fidPrimitive;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getQEtag() {
        return qEtag;
    }

    public void setQEtag(String qEtag) {
        this.qEtag = qEtag;
    }

    public String getQPersistId() {
        return qPersistId;
    }

    public void setQPersistId(String qPersistId) {
        this.qPersistId = qPersistId;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getUpDevice() {
        return upDevice;
    }

    public void setUpDevice(String upDevice) {
        this.upDevice = upDevice;
    }

    public String getUseFor() {
        return useFor;
    }

    public void setUseFor(String useFor) {
        this.useFor = useFor;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVDspRatio() {
        return vDspRatio;
    }

    public void setVDspRatio(String vDspRatio) {
        this.vDspRatio = vDspRatio;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    @Override
    public String toString() {
        return "callBackBodyBean{" +
                "appTags='" + appTags + '\'' +
                ", duration='" + duration + '\'' +
                ", fId='" + fId + '\'' +
                ", fPrimitiveName='" + fPrimitiveName + '\'' +
                ", fType='" + fType + '\'' +
                ", fidPrimitive='" + fidPrimitive + '\'' +
                ", height='" + height + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", qEtag='" + qEtag + '\'' +
                ", qPersistId='" + qPersistId + '\'' +
                ", size='" + size + '\'' +
                ", upDevice='" + upDevice + '\'' +
                ", useFor='" + useFor + '\'' +
                ", userId='" + userId + '\'' +
                ", vDspRatio='" + vDspRatio + '\'' +
                ", width='" + width + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(appTags);
        dest.writeString(duration);
        dest.writeString(fId);
        dest.writeString(fPrimitiveName);
        dest.writeString(fType);
        dest.writeString(fidPrimitive);
        dest.writeString(height);
        dest.writeString(mimeType);
        dest.writeString(qEtag);
        dest.writeString(qPersistId);
        dest.writeString(size);
        dest.writeString(upDevice);
        dest.writeString(useFor);
        dest.writeString(userId);
        dest.writeString(vDspRatio);
        dest.writeString(width);
    }
}
