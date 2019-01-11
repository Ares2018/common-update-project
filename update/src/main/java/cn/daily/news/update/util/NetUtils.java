package cn.daily.news.update.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络工具类
 *
 * @author a_liYa
 * @date 2017/5/2 19:14.
 */
public class NetUtils {

    /**
     * 网络是否可用
     *
     * @return true:可用
     */
    public static boolean isAvailable(Context context) {
        NetworkInfo networkInfo = getNetworkInfo(context);
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * 是否为移动网络
     *
     * @return true:移动网络
     */
    public static boolean isMobile(Context context) {
//        return isWifi();
        NetworkInfo info = getNetworkInfo(context);
        if (info == null || !info.isAvailable()) {
            return false;
        }
        return ConnectivityManager.TYPE_MOBILE == info.getType();
    }

    /**
     * 是否为WiFi网络
     *
     * @return true：WiFi
     */
    public static boolean isWifi(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info == null || !info.isAvailable()) {
            return false;
        }
        return ConnectivityManager.TYPE_WIFI == info.getType();
    }

    private static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }


}
