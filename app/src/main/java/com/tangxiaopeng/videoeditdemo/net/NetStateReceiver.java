package com.tangxiaopeng.videoeditdemo.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络状态监听
 * @author 蒋庆意
 * @date 2015-8-25
 * @time 下午5:55:50
 */
public class NetStateReceiver extends BroadcastReceiver
{
    /**
     * 当前使用网络类型(-1表示无可用网络)
     */
    public static int currentNetType = -1;
    
    @Override
    public void onReceive(Context context, Intent intent)
    {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();
        //有可用的网络
        if (null!=activeInfo&&activeInfo.isAvailable())
        {
            //设置当前使用网络类型
            setCurrentNetType(manager.getActiveNetworkInfo().getType());
        }else{
            setCurrentNetType(-1);
        }
    }

    /**
     * 获取当前网络类型
     * @return
     */
    public static int getCurrentNetType()
    {
        return currentNetType;
    }

    /**
     * 设置当前网络类型
     * @param currentNetType
     */
    public static void setCurrentNetType(int currentNetType)
    {
        NetStateReceiver.currentNetType = currentNetType;
    }
    
}
