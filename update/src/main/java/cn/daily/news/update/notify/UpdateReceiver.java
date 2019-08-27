package cn.daily.news.update.notify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.daily.news.update.util.APKDownloadManager;

import cn.daily.news.update.Constants;
import cn.daily.news.update.UpdateManager;
import cn.daily.news.update.util.SPManager;

/**
 * Created by lixinke on 2017/10/23.
 */

public class UpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Constants.Action.DOWNLOAD_COMPLETE.equals(intent.getAction())) {
            String url = intent.getStringExtra(Constants.Key.APK_URL);
            String path = intent.getStringExtra(Constants.Key.APK_PATH);
            SPManager.getInstance().setApkPath(url, path);
            UpdateManager.installApk(context, path);
        } else if (Constants.Action.DOWNLOAD_RETRY.equals(intent.getAction())) {
            String url = intent.getStringExtra(Constants.Key.APK_URL);
            String version = intent.getStringExtra(Constants.Key.APK_VERSION);
            new NotifyDownloadManager(context, new APKDownloadManager(context), version, url).startDownloadApk();
        }
    }
}
