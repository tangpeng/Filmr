package com.tangxiaopeng.videoeditdemo.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * ProjectName:PLDroidShortVideoDemo
 * Date:2018/9/12 10:27
 *
 * @author fanqiejiang
 */

public class CustomLinearLayoutManager extends LinearLayoutManager {
    private boolean isScrollEnabled = true;

    public CustomLinearLayoutManager(Context context) {
        super(context);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically();
    }
}
