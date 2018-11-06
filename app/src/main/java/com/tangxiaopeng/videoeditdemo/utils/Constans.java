package com.tangxiaopeng.videoeditdemo.utils;

import android.os.Environment;

public class Constans {

    public static final int REQUEST_PERMISSION = 90;//权限申请

    public static final int FRAGINDEX = 79;
    public static final int FRAGMESSAGE = 80;
    public static final int FRAGME = 81;
    public static final int FRAGDIRECTOR = 82;


    public static final int VERI_TIME = 60000;//验证码倒计时

    public static final String MENGMENGCHICKFILEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ystx/";

    public static final int RESULT_SYSYTEM_OK = -1;// 获取系统的回调，相机，拍照，裁切都是成功为-1
    public static final int RESULT_OK = 1;// forResultActivity返回OK标志
    public static final int REQUEST_POINT = 2;//报名点
    public static final int REQUEST_SCHOOL = 3;//选择机构
    public static final int REQUEST_RESUME = 4;//填写简介回调、

    public static final int BLANK_REQUEST_LOCAL_VIDEO = 5;//选择本地视频回调
    public static final int BLANK_REQUEST_SHOOT_VIDEO = 6;//选择拍摄视频回调
    public static final int BLANK_REQUEST_GRAFT_VIDEO = 7;//选择草稿箱回调

    public static final int ADD_STUDENT_REQUEST = 8;//添加学生
    public static final int UPDATE_INFO_DETAILS = 9;//修改机构详细
    public static final int UPDATE_INFO_RESUME = 10;//修改个人简介

    public static final int DELETE_CIRCLE_WORK = 11;//删除作品圈作品，刷新数据

    public static final int PRODUCT_LIST = 12;//视频，图片，列表

    public static final int DRAFT_LIST = 13;//草稿箱列表，跳到详情，删除回调

    public static  final  String QINIU_UOTOKEN="/app/fmanage/qiniu/uptoken";
    public static  final  String QINIU_REPORT="/app/fmanage/qiniu/report";

}

