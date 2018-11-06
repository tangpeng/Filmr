package com.tangxiaopeng.videoeditdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.tangxiaopeng.videoeditdemo.R;
import com.tangxiaopeng.videoeditdemo.utils.ToastUtils;

public class VoiceButton extends android.support.v7.widget.AppCompatButton {

    private Context mContext;

    private long startTime;

    public VoiceButton(Context context) {
        super(context);
        this.mContext = context;
    }

    public VoiceButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventaction = event.getAction();
        switch (eventaction) {
            case MotionEvent.ACTION_DOWN:
                if (mTouchEvent != null) {
                    mTouchEvent.down();
                    startTime = System.currentTimeMillis();
                }
                setText(mContext.getResources().getString(
                        R.string.txt_workDetails_comment_voice_pressed));
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
                long stopTime = System.currentTimeMillis();
                if ((stopTime - startTime) <= 1000) {
                    ToastUtils.l(getContext(),"语音录入时间过短");
                }
                if (mTouchEvent != null) {
                    mTouchEvent.up();
                }
                setText(mContext.getResources().getString(
                        R.string.txt_workDetails_comment_voice));
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private TouchEvent mTouchEvent;

    public void setTouchEvent(TouchEvent mTouchEvent) {
        this.mTouchEvent = mTouchEvent;
    }

    public interface TouchEvent {
        void down();

        void up();
    }
}
