package cn.daily.news.update;


import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zjrb.core.common.biz.ResourceBiz;
import com.zjrb.core.common.permission.AbsPermSingleCallBack;
import com.zjrb.core.common.permission.IPermissionOperate;
import com.zjrb.core.common.permission.Permission;
import com.zjrb.core.common.permission.PermissionManager;
import com.zjrb.core.ui.widget.dialog.LoadingIndicatorDialog;
import com.zjrb.core.utils.DownloadUtil;
import com.zjrb.core.utils.NetUtils;
import com.zjrb.core.utils.SettingManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateDialogFragment extends DialogFragment implements DownloadUtil.OnDownloadListener, IPermissionOperate {
    @BindView(R2.id.update_dialog_title)
    TextView mTitleView;
    @BindView(R2.id.update_dialog_msg)
    TextView mMsgView;
    @BindView(R2.id.update_dialog_cancel)
    View mCancelView;
    @BindView(R2.id.update_dialog_ok)
    TextView mOkView;
    LoadingIndicatorDialog mProgressBar;

    private Unbinder mUnBinder;
    protected ResourceBiz.LatestVersionBean mLatestBean;

    private DownloadUtil mDownloadUtil;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_update_dialog, container, false);
        mUnBinder = ButterKnife.bind(this, rootView);
        mMsgView.setMovementMethod(ScrollingMovementMethod.getInstance());
        setCancelable(false);

        PermissionManager.get().request(this, new AbsPermSingleCallBack() {
            @Override
            public void onGranted(boolean isAlreadyDef) {
                mDownloadUtil = DownloadUtil.get()
                        .setDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath())
                        .setFileName(UpdateManager.Key.APK_NAME);
            }

            @Override
            public void onDenied(List<String> neverAskPerms) {

            }
        }, Permission.STORAGE_WRITE, Permission.STORAGE_READE);

        if (getArguments() != null) {
            mLatestBean = (ResourceBiz.LatestVersionBean) getArguments().getSerializable(UpdateManager.Key.UPDATE_INFO);
            mMsgView.setMovementMethod(ScrollingMovementMethod.getInstance());
            if(mLatestBean!=null && !TextUtils.isEmpty(mLatestBean.remark)){
                mMsgView.setText(Html.fromHtml(mLatestBean.remark));
            }else{
                mMsgView.setText("有新版本请更新!");
            }
            mOkView.setText(getOKText());
            mTitleView.setText(getTitle());
        }

        setCancelable(false);
        return rootView;
    }

    protected void hideCancel() {
        mCancelView.setVisibility(View.GONE);
    }

    protected String getTitle() {
        return "检测到新版本V"+mLatestBean.version+"(版本号),立即更新?";
    }

    protected void setTitleVisible(int visible) {
        mTitleView.setVisibility(visible);
    }

    protected String getRemark() {
        return mLatestBean.remark;
    }

    protected String getOKText() {
        return "更新";
    }


    @OnClick(R2.id.update_dialog_ok)
    public void updateApk(View view) {
        if (NetUtils.isWifi()) {
            downloadApk();
        } else {
            dismiss();
            NonWiFiUpdateDialog dialog = new NonWiFiUpdateDialog();
            Bundle args = new Bundle();
            args.putSerializable(UpdateManager.Key.UPDATE_INFO, mLatestBean);
            dialog.setArguments(args);
            dialog.show(getFragmentManager(), "updateDialog");
        }
    }

    protected void downloadApk() {

        PermissionManager.get().request(this, new AbsPermSingleCallBack() {
            @Override
            public void onGranted(boolean isAlreadyDef) {
                dismiss();
                DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mLatestBean.pkg_url));
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                request.setTitle("浙江新闻");
                request.setDescription("更新浙江新闻版本到" + mLatestBean.version);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, UpdateManager.Key.APK_NAME);
                request.setMimeType("application/vnd.android.package-archive");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                downloadManager.enqueue(request);
                SettingManager.getInstance().setLastApkVersionCode(mLatestBean.version_code);
                SettingManager.getInstance().setApkPath(mLatestBean.pkg_url, null);
            }

            @Override
            public void onDenied(List<String> neverAskPerms) {
                Toast.makeText(getContext(), "请给我写文件的权限", Toast.LENGTH_SHORT).show();
            }
        }, Permission.STORAGE_WRITE,Permission.STORAGE_READE);


    }

    protected void forceDownloadApk() {
        if (UpdateManager.isHasPreloadApk(mLatestBean.pkg_url)) {
            mOkView.setText("安装");
            UpdateManager.installApk(getContext(), SettingManager.getInstance().getApkPath(mLatestBean.pkg_url));
        } else {
            mProgressBar = new LoadingIndicatorDialog(getActivity());
            mProgressBar.show();
            DownloadUtil.get().setListener(this).download(mLatestBean.pkg_url);
        }
    }

    @OnClick(R2.id.update_dialog_cancel)
    public void cancelUpdate(View view) {
        dismiss();
        if (!UpdateManager.isHasPreloadApk(mLatestBean.pkg_url) && NetUtils.isWifi() && mDownloadUtil != null) {
            mDownloadUtil.setListener(new DownloadUtil.OnDownloadListener() {
                @Override
                public void onLoading(int progress) {
                }

                @Override
                public void onSuccess(String path) {
                    SettingManager.getInstance().setApkPath(mLatestBean.pkg_url, path);
                }

                @Override
                public void onFail(String err) {
                }
            }).download(mLatestBean.pkg_url);
        }
    }

    @Override
    public void onLoading(int progress) {

    }

    @Override
    public void onSuccess(String path) {
        UpdateManager.installApk(getContext(), path);
        SettingManager.getInstance().setApkPath(mLatestBean.pkg_url, path);
        mProgressBar.dismiss();
        mOkView.setEnabled(true);
        mOkView.setText("安装");
    }

    protected void installPreloadApk() {
        UpdateManager.installApk(getContext(), SettingManager.getInstance().getApkPath(mLatestBean.pkg_url));
    }

    @Override
    public void onFail(String err) {
        mProgressBar.dismiss();
        mMsgView.setText("更新失败,请稍后再试");
        mMsgView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnBinder.unbind();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.get().onRequestPermissionsResult(requestCode, permissions,
                grantResults, this);
    }

    @Override
    public void exeRequestPermissions(@NonNull String[] permissions, int requestCode) {
        requestPermissions(permissions, requestCode);
    }

    @Override
    public boolean exeShouldShowRequestPermissionRationale(@NonNull String permission) {
        return shouldShowRequestPermissionRationale(permission);
    }
}
