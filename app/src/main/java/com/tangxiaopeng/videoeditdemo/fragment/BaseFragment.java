package com.tangxiaopeng.videoeditdemo.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tangxiaopeng.videoeditdemo.TuApplication;

/**
 * ProjectName:PLDroidShortVideoDemo
 * Date:2018/8/20 16:43
 *
 * @author fanqiejiang
 */

public class BaseFragment extends Fragment {
    private Activity activity;

    @Override
    public Context getContext() {
        if (activity == null) {
            return TuApplication.getInstance();
        }
        return activity;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

        /**
     *  @dec  滑动区域，已经禁止了recyclerview 的滑动，因为和裁切左右滑动有冲突
     *  @author fanqie
     *  @date  2018/9/25 10:28
     */
    protected RecyclerView.LayoutManager createLayoutManager(final boolean canScroll) {
        return new LinearLayoutManager(getActivity()){
            @Override
            public boolean canScrollVertically() {
                return canScroll;
            }
        };
    }

//
//    public LinearLayoutManager mLayoutManager;
//    protected RecyclerView.LayoutManager createLayoutManager(final boolean canScroll) {
//        mLayoutManager = new LinearLayoutManager(getActivity());
//        mLayoutManager.setSmoothScrollbarEnabled(canScroll);
//        return mLayoutManager;
//    }

//    /**
//     * @dec
//     * @author fanqie
//     * @date 2018/9/27 10:21
//     */
//    public void getLayoutManager(boolean canScroll) {
//        if(canScroll){
//            mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
//        }else {
//            mLayoutManager.setOrientation(OrientationHelper.HORIZONTAL);
//        }
//    }
}