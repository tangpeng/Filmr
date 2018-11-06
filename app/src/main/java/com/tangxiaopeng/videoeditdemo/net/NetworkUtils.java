/**
 * @author  张新耀 
 * @Email   zhangxinyao@gexing.com
 * @version 创建时间： 2013-3-4 下午2:18:31
*/
package com.tangxiaopeng.videoeditdemo.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.tangxiaopeng.videoeditdemo.TuApplication;


public class NetworkUtils{

	public static final int NETWORK_NONE = -1;
	public static final int NETWORK_WIFI = 1;
	public static final int NETWORK_MOBILE = 0;
	public static final int NETWORK_MOBILE_CMNET = 2;
	public static final int NETWORK_MOBILE_CMWAP = 3;
	public static final int NETWORK_3G = 4;

	public static int getAPNType(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if (networkInfo == null || !networkInfo.isAvailable()) {
			return NETWORK_NONE;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			// if (networkInfo.getExtraInfo().equals("cmnet") ||
			// networkInfo.getExtraInfo().equals("3gnet")) {
			// return NETWORK_MOBILE_CMNET;
			// } else {
			// return NETWORK_MOBILE_CMWAP;
			// }
			return NETWORK_MOBILE;
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			return NETWORK_WIFI;
		}
		return NETWORK_NONE;
	}

    public static boolean isWifi(Context context) {
        return getAPNType(context) == NETWORK_WIFI;
    }

    public static boolean isMobile(Context context) {
        return getAPNType(context) == NETWORK_MOBILE;
    }

    public static boolean hasNetWork(Context context) {
        return isMobile(context) || isWifi(context);
    }

	public static boolean isWifi() {
		return getAPNType(TuApplication.getInstance()) == NETWORK_WIFI;
	}

	public static boolean isMobile() {
		return getAPNType(TuApplication.getInstance()) == NETWORK_MOBILE;
	}

	public static boolean hasNetWork() {
		return isMobile() || isWifi();
	}
}
