package com.tangxiaopeng.videoeditdemo.fragment;

import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qiniu.pili.droid.shortvideo.PLMediaFile;
import com.tangxiaopeng.videoeditdemo.BekidMainActivity;
import com.tangxiaopeng.videoeditdemo.R;
import com.tangxiaopeng.videoeditdemo.adapter.AddMusicAdapter;
import com.tangxiaopeng.videoeditdemo.bean.Musicbean;
import com.tangxiaopeng.videoeditdemo.utils.MyLog;
import com.tangxiaopeng.videoeditdemo.utils.Tools;
import com.tangxiaopeng.videoeditdemo.utils.UPlayer;
import com.tangxiaopeng.videoeditdemo.utils.URecorder;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 添加声音
 */
public class BekidVoiceFragment extends BaseFragment implements View.OnClickListener, View.OnTouchListener {

    private static final String TAG = "BekidVoiceFragment";
    @BindView(R.id.addVoice)
    ImageView mAddVoice;
    Unbinder unbinder;
    @BindView(R.id.ivBgAddVoice)
    ImageView mIvBgAddVoice;
    AnimationDrawable animationDrawable;
    @BindView(R.id.tvtopVoiceHint)
    TextView mTvtopVoiceHint;
    @BindView(R.id.rlAddVoice)
    RelativeLayout mRlAddVoice;
    @BindView(R.id.chtTimer)
    Chronometer mChtTimer;
    private URecorder mURecorder;

    private int mSlicesTotalLength;
    private static final int SLICE_COUNT = 8;

    private UPlayer mUPlayer;

    @BindView(R.id.recycler_view)
    SwipeMenuRecyclerView mRecyclerView;
    public static List<Musicbean> mDataListVoice = new ArrayList<>();
    protected AddMusicAdapter mAddAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voice_common, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mURecorder = new URecorder();
        mUPlayer = new UPlayer();

        initviewList();
        OnInitListener();
    }


    /**
     * 添加进去的到选中的列表中
     */
    private void initviewList() {
        //type音乐（1）和录音（2）
        mAddAdapter = new AddMusicAdapter(getActivity(), mRecyclerView, 2);

        mRecyclerView.setLayoutManager(createLayoutManager(false));
        mRecyclerView.setAdapter(mAddAdapter);
        mAddAdapter.notifyDataSetChanged(mDataListVoice);

        mRecyclerView.setLongPressDragEnabled(false); // 长按拖拽，默认关闭。
        mRecyclerView.setItemViewSwipeEnabled(false); // 滑动删除，默认关闭。

        mAddAdapter.setOnUpdateDataListener(new AddMusicAdapter.OnUpdateDataListener() {
            @Override
            public void updateData(int position) {
                addItem(mDataListVoice.get(position).getMusicUrl());
            }
        });
    }

    public void addItem(String mPath) {

        if (mDataListVoice.size() > 0) {
            mTvtopVoiceHint.setVisibility(View.GONE);
            ((BekidMainActivity) getActivity()).reIdleReStartPlay();
        }
        PLMediaFile mMediaFile;
        mMediaFile = new PLMediaFile(mPath);
        MyLog.i(TAG, "mMediaFile.getDurationMs()=" + mMediaFile.getDurationMs());
        if (mMediaFile.getDurationMs() < 2 * 1000) {
            Tools.showToast(getActivity(), "声音录制不能少于3秒");
            return;
        }
        long mHetDurationMs = mMediaFile.getDurationMs();//毫秒计算

        Musicbean mMusicbean = new Musicbean();//添加参数
        mMusicbean.setMusicSize(mHetDurationMs);
        mMusicbean.setStartTime(0);
        mMusicbean.setEndTime(mHetDurationMs);
        mMusicbean.setMusicUrl(mPath);
        mMusicbean.setGetAllTime(mHetDurationMs);

        if(mDataListVoice.size()>0){
            //起始位置，为上一段的起始时间+上一段的播放时间
            mMusicbean.setStartInsertTime(mDataListVoice.get(mDataListVoice.size()-1).getStartInsertTime()+mDataListVoice.get(mDataListVoice.size()-1).getMusicSize());
        }

        mDataListVoice.add(mMusicbean);

        mAddAdapter.notifyDataSetChanged(mDataListVoice);
        ((BekidMainActivity) getActivity()).addIndexVoice(mDataListVoice.size() - 1, (int) mHetDurationMs);

    }

    private void OnInitListener() {
        mAddVoice.setOnTouchListener(this);

        mIvBgAddVoice.setImageResource(R.drawable.lottery_animlist);
        animationDrawable = (AnimationDrawable) mIvBgAddVoice.getDrawable();
    }


    @Override
    public void onPause() {
        super.onPause();
        mURecorder.stop();
        MyLog.i(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addVoice:
                Tools.showToast(getActivity(), "请长按...");
                break;
            default:
                break;
        }
    }
    boolean send = true;//是否保存
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.addVoice) {
            float startY = 0;
            float endY = 0;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    ((BekidMainActivity) getActivity()).reIdleReStartPlay();

                    send = true;
                    startY = event.getY();
                    MyLog.i(TAG, "audioButtonDown() MotionEvent.ACTION_DOWN");
                    animationDrawable.start();
                    mAddVoice.setImageResource(R.drawable.z3);
                    mIvBgAddVoice.setVisibility(View.VISIBLE);
                    mURecorder.start("",0);

                    mChtTimer.setBase(SystemClock.elapsedRealtime());
                    mChtTimer.start();

                    break;
                case MotionEvent.ACTION_UP:
                    endY = event.getY();
                    animationDrawable.stop();
                    mIvBgAddVoice.setVisibility(View.GONE);
                    mAddVoice.setImageResource(R.drawable.z2);
                    mChtTimer.stop();
                    mChtTimer.setBase(SystemClock.elapsedRealtime());

                    String voicePath = mURecorder.stop();
                    if(send){//是否保存
                        addVoice(voicePath);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    float moveY = event.getY();
                    int instance = (int) Math.abs((moveY - startY));
                    MyLog.i("tangpeng", "--action move--instance:" + instance);
                    if (instance > 200) {
                        mURecorder.stop();
                        send = false;
                        animationDrawable.stop();
                        mChtTimer.stop();
                        mChtTimer.setBase(SystemClock.elapsedRealtime());

                        mURecorder.stop();

                        mAddVoice.setImageResource(R.drawable.z2);
                        mIvBgAddVoice.setVisibility(View.GONE);
                        Tools.showToast(getActivity(),"你已取消录音");
                    } else {
                        mAddVoice.setImageResource(R.drawable.z3);
                        mIvBgAddVoice.setVisibility(View.VISIBLE);
                    }
                    break;
                default:
                    break;
            }

            return true;
        }
        return false;
    }


    private void addVoice(String mVoice) {
        addItem(mVoice);
    }


}
