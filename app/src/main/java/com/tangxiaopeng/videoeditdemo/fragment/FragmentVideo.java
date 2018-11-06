package com.tangxiaopeng.videoeditdemo.fragment;

import android.app.Fragment;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qiniu.pili.droid.shortvideo.PLShortVideoEditor;
import com.qiniu.pili.droid.shortvideo.PLVideoEditSetting;
import com.tangxiaopeng.videoeditdemo.BekidMainActivity;
import com.tangxiaopeng.videoeditdemo.R;
import com.tangxiaopeng.videoeditdemo.utils.Config;
import com.tangxiaopeng.videoeditdemo.utils.MyLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 视频使用fragment
 */
public class FragmentVideo extends Fragment {

    private static final String TAG = "FragmentVideo";

    Unbinder unbinder;
    @BindView(R.id.glsvVideoCommon)
    GLSurfaceView mGlsvVideoCommon;
    private PLVideoEditSetting setting = new PLVideoEditSetting();
    private PLShortVideoEditor mShortVideoEditor;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_common, container, false);
        unbinder = ButterKnife.bind(this, view);
        MyLog.i(TAG,"onCreateView");
        return view;
    }

    /*Fragment的传参方式(通过Bundle对象来传递)
  *采用这种传参方式可以保证用户在横竖屏切换时所
   * 传递的参数不会丢失
   */
    public static FragmentVideo getInstance(String data) {
        FragmentVideo rightFragment = new FragmentVideo();
        Bundle bundle = new Bundle();
        //将需要传递的字符串以键值对的形式传入bundle
        bundle.putString("data", data);
        rightFragment.setArguments(bundle);
        return rightFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String data = getArguments().getString("data");

        initShortVideoEditor(data);
    }

    /**
     * @dec 视频编辑页面的数据初始化, 可能会多次用到
     * @author fanqie
     * @date 2018/8/28 16:28
     */
    private void initShortVideoEditor(String mMp4path) {
        MyLog.i(TAG, "editing file: " + mMp4path);
        setting.setSourceFilepath(mMp4path);
        // 视频源文件路径
        setting.setDestFilepath(Config.EDITED_FILE_PATH);
        // 编辑保存后，是否保留源文件
        setting.setKeepOriginFile(true);
        //编辑后保存的目标文件路径
        //SquareGLSurfaceView srlQiqiuVideo = new SquareGLSurfaceView(context);
        //mSrlQiqiuVideoInlude.removeAllViews();
        //mSrlQiqiuVideoInlude.addView(srlQiqiuVideo);

        mShortVideoEditor = new PLShortVideoEditor(mGlsvVideoCommon, setting);
        ((BekidMainActivity)getActivity()).getShortVideoEditor(mShortVideoEditor);
    }

    public PLShortVideoEditor ShortVideoEditor(){
        return  mShortVideoEditor;
    }


    @Override
    public void onPause() {
        super.onPause();
        MyLog.i(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        mShortVideoEditor.pausePlayback();
        MyLog.i(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.i(TAG, "onResume");
    }
}
