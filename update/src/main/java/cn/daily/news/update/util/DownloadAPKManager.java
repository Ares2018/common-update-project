package cn.daily.news.update.util;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.daily.news.update.Constants;
import cn.daily.news.update.R;
import cn.daily.news.update.network.OkHttpUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 文件下载工具类
 * Created by wangzhen on 2017/6/26.
 */
public class DownloadAPKManager {
    private final OkHttpClient okHttpClient;
    private final Handler mainThreadHandler;
    private OnDownloadListener mListener;
    private String fileName;
    private String dir;
    private Context mContext;


    public DownloadAPKManager(Context context) {
        mContext = context;
        okHttpClient = OkHttpUtils.getClient();
        mainThreadHandler = new Handler();
        File folder = new File(mContext.getExternalCacheDir(), Constants.Key.APK_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        dir = folder.getPath();
        fileName = mContext.getString(R.string.app_name) + ".apk";
    }

    public DownloadAPKManager setListener(OnDownloadListener listener) {
        this.mListener = listener;
        return this;
    }

    public void download(String url) {
        if (TextUtils.isEmpty(url)) {
            onFail(mContext.getString(R.string.error_invalid_apk_url));
            return;
        }
        if (TextUtils.isEmpty(fileName))
            fileName = getNameFromUrl(url);
        if (TextUtils.isEmpty(fileName)) {
            onFail(mContext.getString(R.string.error_invalid_apk_url));
            return;
        }
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onFail(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                FileOutputStream fos = null;
                byte[] buff = new byte[2048];
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    onStart(total);
                    File file = new File(dir, fileName);
                    if (!file.exists())
                        file.createNewFile();
                    fos = new FileOutputStream(file);
                    int len;
                    int sum = 0;
                    while ((len = is.read(buff)) != -1) {
                        fos.write(buff, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        onLoading(progress);
                    }
                    fos.flush();
                    onSuccess(dir + File.separator + fileName);
                } catch (Exception e) {
                    onFail(e.getMessage());
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void onStart(final long total) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onStart(total);
                }
            }
        });
    }

    private void onSuccess(final String path) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onSuccess(path);
                }
            }
        });
    }

    private void onLoading(final int progress) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onLoading(progress);
                }
            }
        });
    }

    private void onFail(final String err) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onFail(err);
                }
            }
        });
    }

    /**
     * 从下载连接中解析出文件名
     *
     * @param url
     * @return 文件名
     */
    private String getNameFromUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        return Uri.parse(url).getLastPathSegment();
    }

    public interface OnDownloadListener {
        /**
         * 下载进度
         *
         * @param progress 进度
         */
        void onLoading(int progress);

        /**
         * 下载成功
         *
         * @param path 文件路径
         */
        void onSuccess(String path);

        /**
         * 下载失败
         *
         * @param err 错误信息
         */
        void onFail(String err);

        /**
         * 开始下载
         *
         * @param total apk包总的大小
         */
        void onStart(long total);
    }
}
