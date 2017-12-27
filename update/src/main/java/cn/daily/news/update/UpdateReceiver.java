package cn.daily.news.update;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.zjrb.core.utils.SettingManager;

/**
 * Created by lixinke on 2017/10/23.
 */

public class UpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (UpdateManager.Action.DOWNLOAD_COMPLETE.equals(intent.getAction())) {
            String url = intent.getStringExtra(UpdateManager.Key.APK_URL);
            String path = intent.getStringExtra(UpdateManager.Key.APK_PATH);
            SettingManager.getInstance().setApkPath(url, path);
            UpdateManager.installApk(context, path);
        } else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(intent.getAction())) {
            Toast.makeText(context, "正在下载...", Toast.LENGTH_SHORT).show();
        }
    }
}
