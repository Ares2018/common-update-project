package cn.daily.news.update.notify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.zjrb.core.utils.DownloadUtil;
import com.zjrb.core.utils.SettingManager;
import com.zjrb.core.utils.UIUtils;

import cn.daily.news.update.R;
import cn.daily.news.update.UpdateManager;

/**
 * Created by lixinke on 2017/10/23.
 */

public class UpdateReceiver extends BroadcastReceiver {
    private DownloadUtil mDownloadUtil;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (UpdateManager.Action.DOWNLOAD_COMPLETE.equals(intent.getAction())) {
            String path = intent.getStringExtra(UpdateManager.Key.APK_PATH);
            String versionCode = intent.getStringExtra(UpdateManager.Key.APK_VERSION_CODE);
            String cachePath = null;
            try {
                cachePath = Uri.parse(path).buildUpon().appendQueryParameter(UpdateManager.Key.APK_VERSION_CODE, String.valueOf(versionCode)).toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            SettingManager.getInstance().setApkCachePath(cachePath);
            UpdateManager.installApk(context, path);
        } else if (UpdateManager.Action.DOWNLOAD_RETRY.equals(intent.getAction())) {
            String url = intent.getStringExtra(UpdateManager.Key.APK_URL);
            String versionName = intent.getStringExtra(UpdateManager.Key.APK_VERSION_NAME);
            int versionCode = intent.getIntExtra(UpdateManager.Key.APK_VERSION_CODE, 0);
            mDownloadUtil = DownloadUtil.get()
                    .setDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath())
                    .setFileName(UIUtils.getString(R.string.app_name) + ".apk");
            new NotifyDownloadManager(context,mDownloadUtil, versionName, url, versionCode).startDownloadApk();
        }
    }
}
