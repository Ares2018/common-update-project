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
import com.zjrb.core.common.manager.APICallManager;
import com.zjrb.core.utils.SettingManager;

import java.io.File;

import cn.daily.news.update.analytic.IAnalytic;
import cn.daily.news.update.ui.ForceUpdateDialog;
import cn.daily.news.update.ui.PreloadUpdateDialog;
import cn.daily.news.update.ui.UpdateDialogFragment;

/**
 * Created by lixinke on 2017/8/30.
 */

public class UpdateManager {
    public static String TAG_TASK = "tag_task_update_manager";
    private static String MIME_APK = "application/vnd.android.package-archive";
    private static IAnalytic sIAnalytic;
    private static int sVersionCode = 0;


    public static void checkUpdate(final AppCompatActivity activity, final UpdateListener listener) {
        checkUpdate(activity, listener, "/api/app_version/detail");
    }

    public static void checkUpdate(AppCompatActivity appCompatActivity, VersionBean latest_version) {
        checkData(latest_version, appCompatActivity, null);
    }

    public static void checkUpdate(AppCompatActivity activity, UpdateListener listener, final String url) {
        new APIGetTask<UpdateResponse.DataBean>(new CheckUpdateCallBack(listener, activity)) {
            @Override
            protected void onSetupParams(Object... params) {
            }

            @Override
            protected String getApi() {
                return url;
            }
        }.setTag(TAG_TASK).exe();
    }


    public static void cancel() {
        APICallManager.get().cancel(TAG_TASK);
    }

    private static void checkData(VersionBean latest, AppCompatActivity activity, UpdateListener listener) {
        if (getVersionCode(activity) < latest.version_code) {
            latest.isNeedUpdate = true;
            UpdateDialogFragment updateDialogFragment;
            if (latest.force_upgraded) {
                updateDialogFragment = new ForceUpdateDialog();
            } else {
                if (isHasPreloadApk(getVersionCode(activity))) {
                    latest.preloadPath = SettingManager.getInstance().getApkPath(latest.pkg_url);
                    updateDialogFragment = new PreloadUpdateDialog();
                } else {
                    updateDialogFragment = new UpdateDialogFragment();
                }
            }
            Bundle args = new Bundle();
            args.putSerializable(Key.UPDATE_INFO, latest);
            updateDialogFragment.setArguments(args);
            updateDialogFragment.show(activity.getSupportFragmentManager(), "updateDialog");
        }

        if (listener != null) {
            UpdateResponse.DataBean dataBean = new UpdateResponse.DataBean();
            dataBean.latest = latest;
            listener.onUpdate(dataBean);
        }
    }

    public static boolean isHasPreloadApk(int versionCode) {
        try {
            String path = SettingManager.getInstance().getApkCachePath();
            if (!TextUtils.isEmpty(path)) {
                Uri uri = Uri.parse(path);
                String code = uri.getQueryParameter(Key.APK_VERSION_CODE);
                if (Integer.parseInt(code) < versionCode) {
                    return false;
                }
                path = uri.getPath();
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


    public static void installApk(Context context, String path) {
        File file = new File(path);
        if (file != null && file.exists()) {
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

    public static int getVersionCode(Context context) {
        if (context == null) {
            return 0;
        }
        if (sVersionCode > 0) {
            return sVersionCode;
        }
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                sVersionCode = packageInfo.versionCode;
            }
        } catch (Exception e) {
            Log.e("Update", "get version code error", e);
        }
        return sVersionCode;
    }

    private static class CheckUpdateCallBack extends APICallBack<UpdateResponse.DataBean> {
        private final UpdateListener mListener;
        private final AppCompatActivity mActivity;

        public CheckUpdateCallBack(UpdateListener listener, AppCompatActivity activity) {
            mListener = listener;
            mActivity = activity;
        }

        @Override
        public void onSuccess(UpdateResponse.DataBean data) {
            if (data == null || data.latest == null) {
                if (mListener != null) {
                    mListener.onError("检测更新失败!", -1);
                    if (BuildConfig.DEBUG) {
                        Log.e("update", "服务端返回错误!");
                    }
                }
                return;
            }
//            data.latest.pkg_url = getApkKey(data.latest.pkg_url, String.valueOf(data.latest.version_code));
            checkData(data.latest, mActivity, mListener);
        }

        @Override
        public void onError(String errMsg, int errCode) {
            super.onError(errMsg, errCode);
            if (mListener != null) {
                mListener.onError(errMsg, errCode);
            }
        }
    }

    public interface Key {
        String UPDATE_INFO = "update_info";
        String APK_VERSION_CODE = "versionCode";
        String SCHEME = "scheme";
        String APK_URL = "download_apk_url";
        String APK_PATH = "download_apk_local_path";
        String APK_VERSION_NAME = "download_apk_version";
    }

    public interface Action {
        String DOWNLOAD_COMPLETE = "download_complete";
        String DOWNLOAD_RETRY = "download_retry";
    }

    public interface UpdateListener {
        void onUpdate(UpdateResponse.DataBean dataBean);

        void onError(String errMsg, int errCode);
    }


    public static void setIAnalytic(IAnalytic IAnalytic) {
        sIAnalytic = IAnalytic;
    }

    public static IAnalytic getIAnalytic() {
        return sIAnalytic;
    }
}
