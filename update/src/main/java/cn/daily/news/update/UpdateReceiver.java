package cn.daily.news.update;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.zjrb.core.utils.SettingManager;

/**
 * Created by lixinke on 2017/10/23.
 */

public class UpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
            DownloadManager.Query query = new DownloadManager.Query();
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Cursor cursor = manager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                String title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
                if (TextUtils.equals(title, "浙江新闻")) {

                    String url = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI));
                    Uri uri = Uri.parse(url);
                    String scheme = uri.getQueryParameter(UpdateManager.Key.SCHEME);
                    url = uri.buildUpon().scheme(scheme).build().toString();

                    String path = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    path = Uri.parse(path).getPath();
                    SettingManager.getInstance().setApkPath(url, path);
                    UpdateManager.installApk(context, path);
                }
            }

        } else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(intent.getAction())) {
            Toast.makeText(context, "正在下载...", Toast.LENGTH_SHORT).show();
        }
    }
}
