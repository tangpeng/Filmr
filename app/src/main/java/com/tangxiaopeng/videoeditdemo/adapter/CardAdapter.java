package com.tangxiaopeng.videoeditdemo.adapter;
import android.support.v7.widget.CardView;

/**
 * ProjectName:PLDroidShortVideoDemo
 * Date:2018/8/20 18:43
 *
 * @author fanqiejiang
 */

public interface CardAdapter {

    int MAX_ELEVATION_FACTOR = 8;

    float getBaseElevation();

    CardView getCardViewAt(int position);

    int getCount();
}