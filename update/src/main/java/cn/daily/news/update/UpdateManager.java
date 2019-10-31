package cn.daily.news.update;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import cn.daily.news.update.listenter.OnOperateListener;
import cn.daily.news.update.model.UpdateResponse;
import cn.daily.news.update.model.VersionBean;
import cn.daily.news.update.ui.ForceUpdateDialog;
import cn.daily.news.update.ui.PreloadUpdateDialog;
import cn.daily.news.update.ui.UpdateDialogFragment;
import cn.daily.news.update.util.SPManager;
import cn.daily.news.update.util.VersionCompareUtils;

/**
 * Created by lixinke on 2017/8/30.
 */

public class UpdateManager {
    public static String TAG_TASK = "tag_task_update_manager";
    private static int sLayoutId = 0;
    private static OnOperateListener sOnOperateListener;

    public static OnOperateListener getOnOperateListener() {
        return sOnOperateListener;
    }

    public static void setOnOperateListener(OnOperateListener onOperateListener) {
        sOnOperateListener = onOperateListener;
    }

    public interface UpdateListener {
        void onUpdate(UpdateResponse.DataBean dataBean);

        void onError(String errMsg, int errCode);
    }

    public static void init(Context context) {
        SPManager.getInstance().init(context);
    }

    public static void checkUpdate(AppCompatActivity appCompatActivity, VersionBean latest_version) {
        checkData(appCompatActivity, latest_version, null);
    }

    public static void checkUpdate(AppCompatActivity appCompatActivity, VersionBean versionBean, String tip) {
        checkData(appCompatActivity, versionBean, tip);
    }

    public static boolean isNeedUpdate(AppCompatActivity activity, VersionBean latest) {
        String versionName = "5.0";
        try {
            PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            if (packageInfo != null) {
                versionName = packageInfo.versionName;
            }
        } catch (Exception e) {
            Log.e("Update", "get version code error", e);
        }

        if (VersionCompareUtils.compareVersionName(versionName, latest.version) == -1) {
            return true;
        }
        return false;
    }

    private static void checkData(AppCompatActivity activity, VersionBean latest, String tip) {
        if (isNeedUpdate(activity, latest)) {
            latest.isNeedUpdate = true;
            UpdateDialogFragment updateDialogFragment;
            if (latest.force_upgraded) {
                updateDialogFragment = new ForceUpdateDialog();
            } else {
                if (isHasPreloadApk(latest.pkg_url)) {
                    latest.preloadPath = SPManager.getInstance().getApkPath(latest.pkg_url);
                    updateDialogFragment = new PreloadUpdateDialog();
                } else {
                    updateDialogFragment = new UpdateDialogFragment();
                }
            }
            Bundle args = new Bundle();
            args.putSerializable(Constants.Key.UPDATE_INFO, latest);
            updateDialogFragment.setArguments(args);
            updateDialogFragment.show(activity.getSupportFragmentManager(), "updateDialog");
        } else if (!TextUtils.isEmpty(tip)) {
            Toast.makeText(activity, tip, Toast.LENGTH_SHORT).show();
        }
    }


    public static boolean isHasPreloadApk(String pkg_url) {
//        try {
//            String path = SPManager.getInstance().getApkPath(pkg_url);
//            if (!TextUtils.isEmpty(path)) {
//                File file = new File(path);
//                long total = SPManager.getInstance().getApkSize(pkg_url);
//                if (file.exists() && file.length() > 0 && file.length() == total) {
//                    return true;
//                }
//                return false;
//            }
//            return false;
//        } catch (Exception e) {
//            return false;
//        }
        return false;
    }

    public static String getPreloadApkPah(String pkg_url) {
        return SPManager.getInstance().getApkPath(pkg_url);
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
                install.setDataAndType(apkUri, context.getString(R.string.update_mime_apk));
            } else {
                install.setDataAndType(Uri.fromFile(file), context.getString(R.string.update_mime_apk));
            }
            context.startActivity(install);
        }
    }

    public static void setLayout(@LayoutRes int id) {
        sLayoutId = id;
    }

    public static int getLayoutId() {
        return sLayoutId;
    }
}
