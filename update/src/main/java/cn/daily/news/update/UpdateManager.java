package cn.daily.news.update;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonObject;
import com.zjrb.core.api.base.APIGetTask;
import com.zjrb.core.api.callback.APICallBack;
import com.zjrb.core.common.biz.ResourceBiz;
import com.zjrb.core.common.manager.APICallManager;
import com.zjrb.core.db.SPHelper;
import com.zjrb.core.utils.SettingManager;

import java.io.File;

/**
 * Created by lixinke on 2017/8/30.
 */

public class UpdateManager {
    public static String TAG_TASK = "tag_task_update_manager";

    public interface UpdateListener {
        void onUpdate(UpdateResponse.DataBean dataBean);

        void onError(String errMsg, int errCode);
    }

    public static void checkUpdate(AppCompatActivity appCompatActivity, VersionBean latest_version) {
        checkData(latest_version, appCompatActivity, null);
    }

    public static void checkUpdate(final AppCompatActivity activity, final UpdateListener listener) {
        new APIGetTask<UpdateResponse.DataBean>(new CheckUpdateCallBack(listener, activity)) {
            @Override
            protected void onSetupParams(Object... params) {
            }

            @Override
            protected String getApi() {
                return "/api/app_version/detail";
            }
        }.setTag(TAG_TASK).exe();
    }


    private static void checkData(VersionBean latest, AppCompatActivity activity, UpdateListener listener) {
        String versionName="5.0";
        try {
            PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            if (packageInfo != null) {
                versionName = packageInfo.versionName;
            }
        } catch (Exception e) {
            Log.e("Update", "get version code error", e);
        }

        if (VersionCompareUtils.compareVersionName(versionName,latest.version)==-1) {
            latest.isNeedUpdate = true;
            UpdateDialogFragment updateDialogFragment;
            if (latest.force_upgraded) {
                updateDialogFragment = new ForceUpdateDialog();
            } else {
                if (isHasPreloadApk(latest.pkg_url)) {
                    latest.preloadPath = SettingManager.getInstance().getApkPath(latest.pkg_url);
                    updateDialogFragment = new PreloadUpdateDialog();
                } else {
                    updateDialogFragment = new UpdateDialogFragment();
                }
            }
            Bundle args = new Bundle();
            args.putSerializable(Constant.Key.UPDATE_INFO, latest);
            updateDialogFragment.setArguments(args);
            updateDialogFragment.show(activity.getSupportFragmentManager(), "updateDialog");
        }

        if (listener != null) {
            UpdateResponse.DataBean dataBean = new UpdateResponse.DataBean();
            dataBean.latest = latest;
            listener.onUpdate(dataBean);
        }
    }

    public static String getPreloadApkPath() {
        String result = "";
        ResourceBiz biz = SPHelper.get().getObject(SPHelper.Key.INITIALIZATION_RESOURCES);
        if (biz != null && biz.latest_version != null && !TextUtils.isEmpty(biz.latest_version.pkg_url)) {
            String url = biz.latest_version.pkg_url;
            if (isHasPreloadApk(url)) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("path", SettingManager.getInstance().getApkPath(url));
                jsonObject.addProperty("version", biz.latest_version.version_code);
                return jsonObject.toString();
            }
        }
        return result;
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


    public static void installApk(Context context, String path) {
        File file = new File(path);
        if (file!=null && file.exists()) {
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //兼容7.0私有文件权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", file);
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                install.setDataAndType(apkUri, context.getString(R.string.update_mime_apk));
            } else {
                install.setDataAndType(Uri.fromFile(file), context.getString(R.string.update_mime_apk));
            }
            context.startActivity(install);
        }
    }

    public static String getApkKey(String url, String version) {
        try {
            if (!TextUtils.isEmpty(url)) {
                Uri uri = Uri.parse(url);
                String scheme = uri.getScheme();
                return uri.buildUpon().appendQueryParameter(Constant.Key.VERSION_CODE, version).appendQueryParameter(Constant.Key.SCHEME, scheme).build().toString();
            }
        } catch (Exception e) {
        }
        return url;
    }
    public static void cancel() {
        APICallManager.get().cancel(TAG_TASK);
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
            data.latest.pkg_url = getApkKey(data.latest.pkg_url, String.valueOf(data.latest.version_code));
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
}
