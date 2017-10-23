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
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zjrb.core.common.permission.AbsPermSingleCallBack;
import com.zjrb.core.common.permission.IPermissionOperate;
import com.zjrb.core.common.permission.Permission;
import com.zjrb.core.common.permission.PermissionManager;
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
public class UpdateDialogFragment extends DialogFragment implements DownloadUtil.OnDownloadListener,IPermissionOperate {
    @BindView(R2.id.update_dialog_title)
    TextView mTitleView;
    @BindView(R2.id.update_dialog_msg)
    TextView mMsgView;
    @BindView(R2.id.update_dialog_cancel)
    View mCancelView;
    @BindView(R2.id.update_dialog_ok)
    TextView mOkView;
    @BindView(R2.id.update_dialog_progressBar)
    ProgressBar mProgressBar;

    private Unbinder mUnBinder;
    private UpdateResponse.DataBean.LatestBean mLatestBean;
    private String mApkPath;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_update_dialog, container, false);
        mUnBinder = ButterKnife.bind(this, rootView);
        mMsgView.setMovementMethod(ScrollingMovementMethod.getInstance());

        if (getArguments() != null) {
            mLatestBean = getArguments().getParcelable(UpdateManager.Key.UPDATE_INFO);
            mMsgView.setText(getRemark());
            mOkView.setText(getOKText());
            mTitleView.setText(getTitle());
        }
        return rootView;
    }

    protected void hideCancel(){
        mCancelView.setVisibility(View.GONE);
    }

    protected String getTitle() {
        return "更新提示";
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
            args.putParcelable(UpdateManager.Key.UPDATE_INFO, mLatestBean);
            dialog.setArguments(args);
            dialog.show(getFragmentManager(), "updateDialog");
        }
    }

    protected void downloadApk() {

        PermissionManager.get().request(this, new AbsPermSingleCallBack() {
            @Override
            public void onGranted(boolean isAlreadyDef) {
                dismiss();
                DownloadManager downloadManager= (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request=new DownloadManager.Request(Uri.parse(mLatestBean.pkg_url));
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                request.setTitle("浙江新闻");
                request.setDescription("更新浙江新闻版本到"+mLatestBean.version);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"zhejiang.apk");
                request.setMimeType("application/vnd.android.package-archive");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                downloadManager.enqueue(request);
            }

            @Override
            public void onDenied(List<String> neverAskPerms) {
                Toast.makeText(getContext(),"请给我写文件的权限",Toast.LENGTH_SHORT).show();
            }
        }, Permission.STORAGE_WRITE);



    }

    protected void forceDowloadApk(){
        mProgressBar.setVisibility(View.VISIBLE);
        mMsgView.setVisibility(View.GONE);
        DownloadUtil.get().setListener(this).download(mLatestBean.pkg_url);
    }

    @OnClick(R2.id.update_dialog_cancel)
    public void cancelUpdate(View view) {
        if (UpdateManager.isHasPreloadApk(mLatestBean.version_code) && NetUtils.isWifi()) {
            DownloadUtil.get().setListener(new DownloadUtil.OnDownloadListener() {
                @Override
                public void onLoading(int progress) {

                }

                @Override
                public void onSuccess(String path) {
                    SettingManager.getInstance().setApkPath(path);
                }

                @Override
                public void onFail(String err) {

                }
            }).download(mLatestBean.pkg_url);
        }
        dismiss();
    }

    @Override
    public void onLoading(int progress) {

    }

    @Override
    public void onSuccess(String path) {

        UpdateManager.installApk(getContext(), path);
        SettingManager.getInstance().setApkPath(path);

        mApkPath = path;

        mMsgView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mOkView.setEnabled(true);
        mOkView.setText("安装");
    }

    protected void installPreloadApk(){
        UpdateManager.installApk(getContext(),mLatestBean.preloadPath);
    }

    @Override
    public void onFail(String err) {
        mProgressBar.setVisibility(View.GONE);
        mMsgView.setText("更新失败,请稍后再试");
        mMsgView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnBinder.unbind();
        DownloadUtil.get().setListener(null);
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
