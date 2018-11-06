package com.tangxiaopeng.videoeditdemo.utils;

/**
 * ProjectName:PLDroidShortVideoDemo
 * Date:2018/8/28 19:30
 *
 * @author fanqiejiang
 */

public class QiniuTool {

    public static float clamp(float origin) {
        if (origin < 0) {
            return 0;
        }
        if (origin > 1) {
            return 1;
        }
        return origin;
    }
}
