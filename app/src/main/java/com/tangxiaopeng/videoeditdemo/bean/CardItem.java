package com.tangxiaopeng.videoeditdemo.bean;

import android.graphics.Bitmap;

/**
 * ProjectName:PLDroidShortVideoDemo
 * Date:2018/8/20 18:44
 *
 * @author fanqiejiang
 */

public class CardItem {

    private String mTextResource;
    private Bitmap imgId;

    public CardItem(Bitmap imgIdw, String text) {
        imgId = imgIdw;
        mTextResource = text;
    }

    public String getText() {
        return mTextResource;
    }

    public Bitmap getImg() {
        return imgId;
    }
}
