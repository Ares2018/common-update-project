package cn.daily.news.update.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class OkHttpUtils {

    private static volatile OkHttpClient mClient;

    public static OkHttpClient getClient() {

        if (mClient == null) {
            synchronized (OkHttpUtils.class) {
                if (mClient == null) {
                    mClient = initClient();
                }
            }
        }

        return mClient;
    }


    private static OkHttpClient initClient() {
        SSLSocketManager sslSM = new SSLSocketManager();
        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .sslSocketFactory(sslSM.getSSLSocketFactory(), sslSM.getX509TrustManager())
                .hostnameVerifier(sslSM.getHostnameVerifier())
                .connectTimeout(5, TimeUnit.SECONDS)  // 设置网络超时 - 连接
                .readTimeout(20, TimeUnit.SECONDS) // 设置网络超时 - 读
                .writeTimeout(20, TimeUnit.SECONDS) // 设置网络超时 - 写
                .build();

        return client;
    }


}

