package cn.daily.news.update;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.zjrb.core.utils.DownloadUtil;
import com.zjrb.core.utils.SettingManager;
import com.zjrb.core.utils.UIUtils;

/**
 * Created by lixinke on 2017/12/28.
 */

public class NotifyDownloadManager {
    private static final int NOTIFY_PROGRESS_ID = 11111;
    private static final long UPDATE_DURATION_TIME = 500;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;
    private long mUpdateTime;
    private DownloadUtil mDownloadUtil;
    private String mLastVersion;
    private String mApkUrl;

    public NotifyDownloadManager(DownloadUtil downloadUtil, String version, String apkUrl) {
        mDownloadUtil = downloadUtil;
        mLastVersion = version;
        mApkUrl = apkUrl;

        mNotificationManager = (NotificationManager) UIUtils.getApp().getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1", "浙江新闻", NotificationManager.IMPORTANCE_LOW);
            channel.enableLights(false);
            channel.enableVibration(false);
            channel.setShowBadge(false);
            mNotificationManager.createNotificationChannel(channel);
        }
        mBuilder = new NotificationCompat.Builder(UIUtils.getApp(), "1");
        mBuilder.setSmallIcon(android.R.drawable.stat_sys_download);
    }

    public void startDownloadApk() {
        mBuilder.setContentTitle(UIUtils.getString(R.string.app_name));
        mBuilder.setContentText("更新" + UIUtils.getString(R.string.app_name) + "到" + mLastVersion);
        mBuilder.setProgress(0, 0, true);
        mNotificationManager.notify(NOTIFY_PROGRESS_ID, mBuilder.build());

        mUpdateTime = System.currentTimeMillis();

        mDownloadUtil.setListener(new MyOnDownloadListener()).download(mApkUrl);
    }

    private class MyOnDownloadListener implements DownloadUtil.OnDownloadListener {
        @Override
        public void onLoading(int progress) {
            if (System.currentTimeMillis() - mUpdateTime < UPDATE_DURATION_TIME) {
                return;
            }
            mUpdateTime = System.currentTimeMillis();
            mBuilder.setAutoCancel(false);
            mBuilder.setProgress(100, progress, false);
            mNotificationManager.notify(NOTIFY_PROGRESS_ID, mBuilder.build());
        }

        @Override
        public void onSuccess(String path) {
            Intent data = new Intent(UIUtils.getApp(), UpdateReceiver.class);
            data.setAction(Constant.Action.DOWNLOAD_COMPLETE);
            data.putExtra(Constant.Key.APK_URL, mApkUrl);
            data.putExtra(Constant.Key.APK_PATH, path);

            PendingIntent intent = PendingIntent.getBroadcast(UIUtils.getApp(), 100, data, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(intent);
            mBuilder.setContentText(UIUtils.getString(R.string.download_complete_tip)).setProgress(0, 0, false);
            mBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done);
            mBuilder.setAutoCancel(true);
            mNotificationManager.notify(NOTIFY_PROGRESS_ID, mBuilder.build());

            UIUtils.getApp().sendBroadcast(data);
            SettingManager.getInstance().setApkPath(mApkUrl, path);

        }

        @Override
        public void onFail(String err) {

            Intent data = new Intent(UIUtils.getApp(), UpdateReceiver.class);
            data.setAction(Constant.Action.DOWNLOAD_RETRY);
            data.putExtra(Constant.Key.APK_URL, mApkUrl);
            data.putExtra(Constant.Key.APK_VERSION, mLastVersion);

            PendingIntent intent = PendingIntent.getBroadcast(UIUtils.getApp(), 100, data, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentText(UIUtils.getString(R.string.download_error_tip)).setProgress(0, 0, false);
            mBuilder.setContentIntent(intent);
            mBuilder.setSmallIcon(android.R.drawable.stat_notify_error);
            mBuilder.setAutoCancel(true);
            mNotificationManager.notify(NOTIFY_PROGRESS_ID, mBuilder.build());
        }
    }
}
