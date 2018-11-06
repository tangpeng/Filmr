package com.tangxiaopeng.videoeditdemo.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * ProjectName:VideoEditDemo20181011
 * Date:2018/10/25 15:52
 *
 * @author fanqiejiang
 */

public class QiniuTokenBean implements Parcelable {

    /**
     * retCode : 0
     * retMsg : null
     * data : {"id":"u715kfg6s5d","fileKey":"u715kfg6s5c","token":"mDBaY_vQq-ZngOpsGE69Jo_jJDbmFMnEVZ_Q6huK:-nPZGKKZ7Fm50M9vNfl6yVi3yWA=:eyJwZXJzaXN0ZW50UGlwZWxpbmUiOiJ2aWRlbyIsImNhbGxiYWNrQm9keVR5cGUiOiJhcHBsaWNhdGlvbi9qc29uIiwic2NvcGUiOiJ0ZXN0dmlkZXA6dTcxNWtmZzZzNWMiLCJwZXJzaXN0ZW50Tm90aWZ5VXJsIjoiaHR0cDovL3lpc2FpdGVzdC5leXNhaS5jb20vZm1hbmFnZS9xaW5pdS9wZXJzaXNjYWxsYmFjayIsImNhbGxiYWNrVXJsIjoiaHR0cDovL3lpc2FpdGVzdC5leXNhaS5jb20vZm1hbmFnZS9xaW5pdS91cGNhbGxiYWNrIiwicGVyc2lzdGVudE9wcyI6InZmcmFtZS9qcGcvb2Zmc2V0LzF8d2F0ZXJtYXJrLzEvaW1hZ2UvYUhSMGNEb3ZMMk52YlhCbGRHbDBhVzl1TjI0dVpYbHpZV2t1WTI5dEx6azBOalJoTm1Zek4yRTVZVFJoWldWaE56WXhObU0yTjJFNFpqQTRNalUxTG5CdVp3XHUwMDNkXHUwMDNkL2dyYXZpdHkvTm9ydGhXZXN0fHNhdmVhcy9kR1Z6ZEhacFpHVndPblUzTVRWclptYzJjelZsO2F2dGh1bWIvbXA0L3dtSW1hZ2UvYUhSMGNEb3ZMMk52YlhCbGRHbDBhVzl1TjI0dVpYbHpZV2t1WTI5dEx6azBOalJoTm1Zek4yRTVZVFJoWldWaE56WXhObU0yTjJFNFpqQTRNalUxTG5CdVp3XHUwMDNkXHUwMDNkL3dtR3Jhdml0eS9Ob3J0aFdlc3R8c2F2ZWFzL2RHVnpkSFpwWkdWd09uVTNNVFZyWm1jMmN6VmsiLCJkZWFkbGluZSI6MTU0MDQ0Mzg1NSwiY2FsbGJhY2tCb2R5Ijoie1wiYXBwVGFnc1wiOlwiZXlzYWlfYXBwXCIsXCJkdXJhdGlvblwiOlwiJChhdmluZm8udmlkZW8uZHVyYXRpb24pXCIsXCJmSWRcIjpcInU3MTVrZmc2czVkXCIsXCJmUHJpbWl0aXZlTmFtZVwiOlwiJChmbmFtZSlcIixcImZUeXBlXCI6XCJWSURFT1wiLFwiZmlkUHJpbWl0aXZlXCI6XCJ1NzE1a2ZnNnM1Y1wiLFwiaGVpZ2h0XCI6XCIkKGF2aW5mby52aWRlby5oZWlnaHQpXCIsXCJtaW1lVHlwZVwiOlwiJChtaW1lVHlwZSlcIixcInFFdGFnXCI6XCIkKGV0YWcpXCIsXCJxUGVyc2lzdElkXCI6XCIkKHBlcnNpc3RlbnRJZClcIixcInNpemVcIjpcIiQoZnNpemUpXCIsXCJ1cERldmljZVwiOlwiYXBwL2FuZHJvaWRcIixcInVzZUZvclwiOlwiVklEX0NPTVBcIixcInVzZXJJZFwiOlwiYWJjXCIsXCJ2RHNwUmF0aW9cIjpcIiQoYXZpbmZvLnZpZGVvLmRpc3BsYXlfYXNwZWN0X3JhdGlvKVwiLFwid2lkdGhcIjpcIiQoYXZpbmZvLnZpZGVvLndpZHRoKVwifSJ9","hasPersisJob":1}
     * success : true
     */

    private int retCode;
    private Object retMsg;
    private DataBean data;
    private boolean success;

    protected QiniuTokenBean(Parcel in) {
        retCode = in.readInt();
        success = in.readByte() != 0;
    }

    public static final Creator<QiniuTokenBean> CREATOR = new Creator<QiniuTokenBean>() {
        @Override
        public QiniuTokenBean createFromParcel(Parcel in) {
            return new QiniuTokenBean(in);
        }

        @Override
        public QiniuTokenBean[] newArray(int size) {
            return new QiniuTokenBean[size];
        }
    };

    public int getRetCode() {
        return retCode;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public Object getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(Object retMsg) {
        this.retMsg = retMsg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(retCode);
        dest.writeByte((byte) (success ? 1 : 0));
    }

    public static class DataBean {
        /**
         * id : u715kfg6s5d
         * fileKey : u715kfg6s5c
         * token : mDBaY_vQq-ZngOpsGE69Jo_jJDbmFMnEVZ_Q6huK:-nPZGKKZ7Fm50M9vNfl6yVi3yWA=:eyJwZXJzaXN0ZW50UGlwZWxpbmUiOiJ2aWRlbyIsImNhbGxiYWNrQm9keVR5cGUiOiJhcHBsaWNhdGlvbi9qc29uIiwic2NvcGUiOiJ0ZXN0dmlkZXA6dTcxNWtmZzZzNWMiLCJwZXJzaXN0ZW50Tm90aWZ5VXJsIjoiaHR0cDovL3lpc2FpdGVzdC5leXNhaS5jb20vZm1hbmFnZS9xaW5pdS9wZXJzaXNjYWxsYmFjayIsImNhbGxiYWNrVXJsIjoiaHR0cDovL3lpc2FpdGVzdC5leXNhaS5jb20vZm1hbmFnZS9xaW5pdS91cGNhbGxiYWNrIiwicGVyc2lzdGVudE9wcyI6InZmcmFtZS9qcGcvb2Zmc2V0LzF8d2F0ZXJtYXJrLzEvaW1hZ2UvYUhSMGNEb3ZMMk52YlhCbGRHbDBhVzl1TjI0dVpYbHpZV2t1WTI5dEx6azBOalJoTm1Zek4yRTVZVFJoWldWaE56WXhObU0yTjJFNFpqQTRNalUxTG5CdVp3XHUwMDNkXHUwMDNkL2dyYXZpdHkvTm9ydGhXZXN0fHNhdmVhcy9kR1Z6ZEhacFpHVndPblUzTVRWclptYzJjelZsO2F2dGh1bWIvbXA0L3dtSW1hZ2UvYUhSMGNEb3ZMMk52YlhCbGRHbDBhVzl1TjI0dVpYbHpZV2t1WTI5dEx6azBOalJoTm1Zek4yRTVZVFJoWldWaE56WXhObU0yTjJFNFpqQTRNalUxTG5CdVp3XHUwMDNkXHUwMDNkL3dtR3Jhdml0eS9Ob3J0aFdlc3R8c2F2ZWFzL2RHVnpkSFpwWkdWd09uVTNNVFZyWm1jMmN6VmsiLCJkZWFkbGluZSI6MTU0MDQ0Mzg1NSwiY2FsbGJhY2tCb2R5Ijoie1wiYXBwVGFnc1wiOlwiZXlzYWlfYXBwXCIsXCJkdXJhdGlvblwiOlwiJChhdmluZm8udmlkZW8uZHVyYXRpb24pXCIsXCJmSWRcIjpcInU3MTVrZmc2czVkXCIsXCJmUHJpbWl0aXZlTmFtZVwiOlwiJChmbmFtZSlcIixcImZUeXBlXCI6XCJWSURFT1wiLFwiZmlkUHJpbWl0aXZlXCI6XCJ1NzE1a2ZnNnM1Y1wiLFwiaGVpZ2h0XCI6XCIkKGF2aW5mby52aWRlby5oZWlnaHQpXCIsXCJtaW1lVHlwZVwiOlwiJChtaW1lVHlwZSlcIixcInFFdGFnXCI6XCIkKGV0YWcpXCIsXCJxUGVyc2lzdElkXCI6XCIkKHBlcnNpc3RlbnRJZClcIixcInNpemVcIjpcIiQoZnNpemUpXCIsXCJ1cERldmljZVwiOlwiYXBwL2FuZHJvaWRcIixcInVzZUZvclwiOlwiVklEX0NPTVBcIixcInVzZXJJZFwiOlwiYWJjXCIsXCJ2RHNwUmF0aW9cIjpcIiQoYXZpbmZvLnZpZGVvLmRpc3BsYXlfYXNwZWN0X3JhdGlvKVwiLFwid2lkdGhcIjpcIiQoYXZpbmZvLnZpZGVvLndpZHRoKVwifSJ9
         * hasPersisJob : 1
         */

        private String id;
        private String fileKey;
        private String token;
        private int hasPersisJob;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFileKey() {
            return fileKey;
        }

        public void setFileKey(String fileKey) {
            this.fileKey = fileKey;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public int getHasPersisJob() {
            return hasPersisJob;
        }

        public void setHasPersisJob(int hasPersisJob) {
            this.hasPersisJob = hasPersisJob;
        }
    }
}
