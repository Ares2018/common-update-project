package cn.daily.news.update;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;

import com.zjrb.coreprojectlibrary.api.base.APIGetTask;
import com.zjrb.coreprojectlibrary.api.callback.APICallBack;
import com.zjrb.coreprojectlibrary.nav.Nav;

import java.io.File;

/**
 * Created by lixinke on 2017/8/30.
 */

public class UpdateManager {
    private static final UpdateManager ourInstance = new UpdateManager();

    public static UpdateManager getInstance() {
        return ourInstance;
    }

    private UpdateManager() {
    }

    public void checkUpdate(final Context context, final String checkUrl) {
        sendUpdateRequest(checkUrl, new OnUpdateListener() {
            @Override
            public void onUpdate(int versionCode, int lastVersionCode, boolean isForce, String apkUrl, String tipMsg) {
                if (versionCode < lastVersionCode) {
                    createForceUpdateDialog(context, lastVersionCode, isForce, apkUrl, tipMsg);
                }
            }
        });
    }

    public void createForceUpdateDialog(final Context context, int lastVersionCode, boolean isForce, final String apkUrl, String tipMsg) {
        Bundle bundle = new Bundle();
        bundle.putString(Update.Key.title, "更新提示");
        bundle.putBoolean(Update.Key.isForce, isForce);
        bundle.putString(Update.Key.message, tipMsg);
        bundle.putInt(Update.Key.lastVersionCode, lastVersionCode);
        bundle.putString(Update.Key.url, apkUrl);
        Nav.with(context).setExtras(bundle).to("http://www.8531.cn/update/dialog");
    }


    private String MIME_APK = "application/vnd.android.package-archive";

    public void installApk(Context context, String path) {
        File file = new File(path);
        if (file.exists()) {
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //兼容7.0私有文件权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", file);
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                install.setDataAndType(apkUri, MIME_APK);
            } else {
                install.setDataAndType(Uri.fromFile(file), MIME_APK);
            }
            context.startActivity(install);
        }
    }

    private void sendUpdateRequest(final String checkUrl, final OnUpdateListener onUpdateListener) {
        new APIGetTask<UpdateResponse.DataBean>(new APICallBack<UpdateResponse.DataBean>() {
            @Override
            public void onSuccess(UpdateResponse.DataBean data) {
                if (onUpdateListener != null) {
                    if (data.current.version_code < data.latest.version_code) {
                        onUpdateListener.onUpdate(data.current.version_code, data.latest.version_code, data.latest.force_upgraded, data.latest.pkg_url, data.latest.remark);
                    } else {
                        onUpdateListener.onUpdate(data.current.version_code, data.latest.version_code, data.latest.force_upgraded, data.latest.pkg_url, data.latest.remark);
                    }
                }
            }
        }) {
            @Override
            protected void onSetupParams(Object... params) {

            }

            @Override
            protected String getApi() {
                return checkUrl;
            }
        };
    }

    public interface OnUpdateListener {
        void onUpdate(int versionCode, int lastVersionCode, boolean isForce, String apkUrl, String tipMsg);
    }
}
