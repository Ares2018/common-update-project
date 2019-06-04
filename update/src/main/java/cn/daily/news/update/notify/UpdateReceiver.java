package cn.daily.news.update.notify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import cn.daily.news.update.util.DownloadManager;

import java.io.File;

import cn.daily.news.update.Constants;
import cn.daily.news.update.R;
import cn.daily.news.update.UpdateManager;
import cn.daily.news.update.util.SPHelper;

/**
 * Created by lixinke on 2017/10/23.
 */

public class UpdateReceiver extends BroadcastReceiver {
    private DownloadManager mDownloadManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Constants.Action.DOWNLOAD_COMPLETE.equals(intent.getAction())) {
            String url = intent.getStringExtra(Constants.Key.APK_URL);
            String path = intent.getStringExtra(Constants.Key.APK_PATH);
            SPHelper.getInstance().setApkPath(url, path);
            UpdateManager.installApk(context, path);
        } else if (Constants.Action.DOWNLOAD_RETRY.equals(intent.getAction())) {
            String url = intent.getStringExtra(Constants.Key.APK_URL);
            String version = intent.getStringExtra(Constants.Key.APK_VERSION);

            File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            mDownloadManager = DownloadManager.get()
                    .setDir(folder.getPath())
                    .setFileName(context.getString(R.string.app_name) + ".apk");
            new NotifyDownloadManager(context, mDownloadManager, version, url).startDownloadApk();
        }
    }
}
