package cn.daily.news.update.notify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import cn.daily.news.update.util.DownloadUtil;
import com.zjrb.core.utils.SettingManager;
import com.zjrb.core.utils.UIUtils;

import java.io.File;

import cn.daily.news.update.Constants;
import cn.daily.news.update.R;
import cn.daily.news.update.UpdateManager;

/**
 * Created by lixinke on 2017/10/23.
 */

public class UpdateReceiver extends BroadcastReceiver {
    private DownloadUtil mDownloadUtil;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Constants.Action.DOWNLOAD_COMPLETE.equals(intent.getAction())) {
            String url = intent.getStringExtra(Constants.Key.APK_URL);
            String path = intent.getStringExtra(Constants.Key.APK_PATH);
            SettingManager.getInstance().setApkPath(url, path);
            UpdateManager.installApk(context, path);
        } else if (Constants.Action.DOWNLOAD_RETRY.equals(intent.getAction())) {
            String url = intent.getStringExtra(Constants.Key.APK_URL);
            String version = intent.getStringExtra(Constants.Key.APK_VERSION);

            File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            mDownloadUtil = DownloadUtil.get()
                    .setDir(folder.getPath())
                    .setFileName(UIUtils.getString(R.string.app_name) + ".apk");
            new NotifyDownloadManager(mDownloadUtil, version, url).startDownloadApk();
        }
    }
}
