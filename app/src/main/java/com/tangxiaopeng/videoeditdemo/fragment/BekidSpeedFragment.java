package com.tangxiaopeng.videoeditdemo.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tangxiaopeng.videoeditdemo.BekidMainActivity;
import com.tangxiaopeng.videoeditdemo.R;
import com.tangxiaopeng.videoeditdemo.utils.MyLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 调节视频的速度
 */
public class BekidSpeedFragment extends Fragment implements View.OnClickListener{

    public static String CURRENT_PAGE = "current_page";
    private static final String TAG = "BekidSpeedFragment";
    Unbinder unbinder;

    @BindView(R.id.sbFgSpeed)
    SeekBar mSbFgSpeed;

    @BindView(R.id.TvSpeedTitle)
    TextView mTvSpeedTitle;

    @BindView(R.id.TvSpeedSure)
    ImageView mTvSpeedSure;

    private long delayMillis = 10;//
    private int maxProcess = 200;
    double speeds = 0;
    /**
     * 滚动条
     */
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    double process = msg.arg1;
                    MyLog.i(TAG, "process=" + process);
                    if (process != 0 && process < maxProcess) {
                        if (process == 100) {
                            mTvSpeedTitle.setText("速度:正常");
                            ((BekidMainActivity) getActivity()).SelectedSpeed(1.0);
                        } else {

                            if (process > 100) {
                                speeds = (process - 100) / 10;
                            } else {
                                speeds = (process) / 100;
                            }
                            MyLog.i(TAG, "50/ 100=" + 50 / 100);
                            MyLog.i(TAG, "speeds=" + speeds);
                            mTvSpeedTitle.setText("速度:" + speeds);
                        }
                    }
                    break;
                default:
                    break;

            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_speed_common, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViews();
        OnInitListener();
    }

    private void findViews() {
        mSbFgSpeed.setMax(maxProcess);
        mSbFgSpeed.setProgress(100);
        mSbFgSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                Message message = Message.obtain();
                message.what = 1;
                message.arg1 = progress;
                handler.sendMessageDelayed(message, delayMillis);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mTvSpeedSure.setOnClickListener(this);
    }

    private void OnInitListener() {

    }


    @Override
    public void onPause() {
        super.onPause();
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

    @Override
    public void onClick(View v) {
        ((BekidMainActivity) getActivity()).SelectedSpeed(speeds);
    }
}
