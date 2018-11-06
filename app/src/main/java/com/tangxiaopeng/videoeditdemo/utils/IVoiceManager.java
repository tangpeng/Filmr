package com.tangxiaopeng.videoeditdemo.utils;

/**
 * Created by JY
 * Date：2017/3/25
 * Time：下午4:13
 */
public interface IVoiceManager {

    boolean start(String path, int seek);

    String stop();
}
