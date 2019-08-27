package cn.daily.news.update.notify;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import cn.daily.news.update.util.APKDownloadManager;

import cn.daily.news.update.Constants;
import cn.daily.news.update.R;
import cn.daily.news.update.util.SPManager;

/**
 * Created by lixinke on 2017/12/28.
 */

public class NotifyDownloadManager {
    private static final int NOTIFY_PROGRESS_ID = 11111;
    private static final long UPDATE_DURATION_TIME = 500;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;
    private long mUpdateTime;
    private APKDownloadManager mDownloadManager;
    private String mLastVersion;
    private String mApkUrl;
    private Context mContext;

    public NotifyDownloadManager(Context context, APKDownloadManager downloadManager, String version, String apkUrl) {
        mDownloadManager = downloadManager;
        mLastVersion = version;
        mApkUrl = apkUrl;
        mContext=context;

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1",mContext.getString(R.string.app_name), NotificationManager.IMPORTANCE_LOW);
            channel.enableLights(false);
            channel.enableVibration(false);
            channel.setShowBadge(false);
            mNotificationManager.createNotificationChannel(channel);
        }
        mBuilder = new NotificationCompat.Builder(mContext, "1");
        mBuilder.setSmallIcon(android.R.drawable.stat_sys_download);
    }

    public void startDownloadApk() {
        mBuilder.setContentTitle(mContext.getString(R.string.app_name));
        mBuilder.setContentText("更新" + mContext.getString(R.string.app_name) + "到" + mLastVersion);
        mBuilder.setProgress(0, 0, true);
        mNotificationManager.notify(NOTIFY_PROGRESS_ID, mBuilder.build());

        mUpdateTime = System.currentTimeMillis();

        mDownloadManager.setListener(new MyOnDownloadListener(mContext)).download(mApkUrl);
    }

    private class MyOnDownloadListener implements APKDownloadManager.OnDownloadListener {
        private Context mContext;

        public MyOnDownloadListener(Context context) {
            mContext = context;
        }

        @Override
        public void onStart(long total) {
            SPManager.getInstance().setApkSize(mApkUrl,total);
        }

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
            Intent data = new Intent(mContext, UpdateReceiver.class);
            data.setAction(Constants.Action.DOWNLOAD_COMPLETE);
            data.putExtra(Constants.Key.APK_URL, mApkUrl);
            data.putExtra(Constants.Key.APK_PATH, path);

            PendingIntent intent = PendingIntent.getBroadcast(mContext, 100, data, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(intent);
            mBuilder.setContentText(mContext.getString(R.string.download_complete_tip)).setProgress(0, 0, false);
            mBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done);
            mBuilder.setAutoCancel(true);
            mNotificationManager.notify(NOTIFY_PROGRESS_ID, mBuilder.build());

            mContext.sendBroadcast(data);
            SPManager.getInstance().setApkPath(mApkUrl, path);

        }

        @Override
        public void onFail(String err) {

            Intent data = new Intent(mContext, UpdateReceiver.class);
            data.setAction(Constants.Action.DOWNLOAD_RETRY);
            data.putExtra(Constants.Key.APK_URL, mApkUrl);
            data.putExtra(Constants.Key.APK_VERSION, mLastVersion);

            PendingIntent intent = PendingIntent.getBroadcast(mContext, 100, data, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentText(mContext.getString(R.string.download_error_tip)).setProgress(0, 0, false);
            mBuilder.setContentIntent(intent);
            mBuilder.setSmallIcon(android.R.drawable.stat_notify_error);
            mBuilder.setAutoCancel(true);
            mNotificationManager.notify(NOTIFY_PROGRESS_ID, mBuilder.build());
        }
    }
}
