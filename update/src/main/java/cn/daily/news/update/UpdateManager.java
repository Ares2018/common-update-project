package cn.daily.news.update;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.zjrb.core.api.base.APIGetTask;
import com.zjrb.core.api.callback.APICallBack;
import com.zjrb.core.utils.SettingManager;

import java.io.File;

/**
 * Created by lixinke on 2017/8/30.
 */

public class UpdateManager {

    interface Key {
        String UPDATE_INFO = "update_info";
        String APK_NAME = "zhejiang.apk";
        String VERSION_CODE = "version_code";
    }

    public interface UpdateListener {
        void onUpdate(UpdateResponse.DataBean dataBean);

        void onError(String errMsg, int errCode);
    }

    public static void checkUpdate(final AppCompatActivity activity, final UpdateListener listener) {
        new APIGetTask<UpdateResponse.DataBean>(new APICallBack<UpdateResponse.DataBean>() {
            @Override
            public void onSuccess(UpdateResponse.DataBean data) {
                if (data == null || data.latest == null) {
                    if (listener != null) {
                        listener.onError("检测更新失败!", -1);
                        if (BuildConfig.DEBUG) {
                            Log.e("update", "服务端返回错误!");
                        }
                    }
                    return;
                }
                int versionCode = 0;
                try {
                    data.latest.pkg_url = Uri.parse(data.latest.pkg_url).buildUpon().appendQueryParameter(Key.VERSION_CODE, String.valueOf(data.latest.version_code)).build().toString();
                    PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
                    if (packageInfo != null) {
                        versionCode = packageInfo.versionCode;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e("Update", "get version code error", e);
                }

                if (versionCode < data.latest.version_code) {
                    data.latest.isNeedUpdate = true;
                    UpdateDialogFragment updateDialogFragment;
                    if (data.latest.force_upgraded) {
                        updateDialogFragment = new ForceUpdateDialog();
                    } else {
                        if (isHasPreloadApk(data.latest.pkg_url)) {
                            data.latest.preloadPath = SettingManager.getInstance().getApkPath(data.latest.pkg_url);
                            updateDialogFragment = new PreloadUpdateDialog();
                        } else {
                            updateDialogFragment = new UpdateDialogFragment();
                        }
                    }
                    Bundle args = new Bundle();
                    args.putParcelable(Key.UPDATE_INFO, data.latest);
                    updateDialogFragment.setArguments(args);
                    updateDialogFragment.show(activity.getSupportFragmentManager(), "updateDialog");
                }

                if (listener != null) {
                    listener.onUpdate(data);
                }
            }

            @Override
            public void onError(String errMsg, int errCode) {
                super.onError(errMsg, errCode);
                if (listener != null) {
                    listener.onError(errMsg, errCode);
                }
            }
        }) {
            @Override
            protected void onSetupParams(Object... params) {
            }

            @Override
            protected String getApi() {
                return "/api/app_version/detail";
            }
        }.exe();
    }

    public static boolean isHasPreloadApk(String pkg_url) {
        try {
            String path = SettingManager.getInstance().getApkPath(pkg_url);
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                if (file.exists()) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static String MIME_APK = "application/vnd.android.package-archive";

    public static void installApk(Context context, String path) {
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
}
