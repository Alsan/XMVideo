package cn.xm.xmvideoplayer.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import cn.xm.xmvideoplayer.core.MyApplication;

/**
 * 网络工具类
 */
public class NetWorkUtil {

    private NetWorkUtil() {

    }

    public static boolean isNetworkConnected() {

        if (MyApplication.getContext() != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * wifi是否连接
     *
     * @return
     */
    public static boolean isWifiConnected() {

        if (MyApplication.getContext() != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 手机是否连接
     *
     * @return
     */
    public static boolean isMobileConnected() {

        if (MyApplication.getContext() != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) MyApplication.getContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static int getConnectedType() {

        if (MyApplication.getContext() != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }
}
