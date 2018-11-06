/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Umeng, Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.tangxiaopeng.videoeditdemo.presenter;

import android.app.Activity;
import android.content.Context;

/**
 * Presenter基类,含有各API操作的统一接口{@see CommunitySDK}以及数据库的操作统一接口{@see DatabaseAPI}
 */
public abstract class BasePresenter {
    protected Context context;

    public void attach(Context context) {
        this.context = context;
    }

    /**
     * 在Fragment、Activity的onDestroy中调用.用于注销广播接收器等</br>
     */
    public void detach() {
        context = null;
    }

    protected boolean isActivityAlive() {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            return !activity.isFinishing();
        }
        return false;
    }

}
