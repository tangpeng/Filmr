package com.tangxiaopeng.videoeditdemo.mvpview;

/**
 * @author tangpeng
 *         mvc框架
 */
public interface MvpUserActivityView {

    public void MVPFail(String data);

    public void MVPSuccess(int type,Object data);


}
