package cn.daily.news.update.ui;


import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.daily.news.update.Constants;
import cn.daily.news.update.R;
import cn.daily.news.update.R2;
import cn.daily.news.update.UpdateManager;
import cn.daily.news.update.model.VersionBean;
import cn.daily.news.update.notify.NotifyDownloadManager;
import cn.daily.news.update.util.AnalyticUtil;
import cn.daily.news.update.util.DownloadUtil;
import cn.daily.news.update.util.NetUtils;
import cn.daily.news.update.util.SPHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateDialogFragment extends DialogFragment implements DownloadUtil.OnDownloadListener {
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
    protected VersionBean mLatestBean;

    private DownloadUtil mDownloadUtil;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_update_dialog, container, false);
        mUnBinder = ButterKnife.bind(this, rootView);
        mMsgView.setMovementMethod(ScrollingMovementMethod.getInstance());
        setCancelable(false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getArguments() != null) {
            mLatestBean = (VersionBean) getArguments().getSerializable(Constants.Key.UPDATE_INFO);
            mMsgView.setMovementMethod(ScrollingMovementMethod.getInstance());
            if (mLatestBean != null && !TextUtils.isEmpty(getRemark())) {
                mMsgView.setText(Html.fromHtml(getRemark()));
            } else {
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
        return "检测到新版本V" + mLatestBean.version + "(版本号),立即更新?";
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
        if (NetUtils.isWifi(getActivity())) {
            downloadApk();
        } else {
            dismissAllowingStateLoss();
            NonWiFiUpdateDialog dialog = new NonWiFiUpdateDialog();
            Bundle args = new Bundle();
            args.putSerializable(Constants.Key.UPDATE_INFO, mLatestBean);
            dialog.setArguments(args);
            dialog.show(getFragmentManager(), "updateDialog");
        }
        AnalyticUtil.ok(getContext());
    }


    protected void downloadApk() {
        dismissAllowingStateLoss();
        if (mDownloadUtil == null) {
            initDownload();
        }
        new NotifyDownloadManager(getActivity(), mDownloadUtil, mLatestBean.version, mLatestBean.pkg_url).startDownloadApk();
    }

    private void initDownload() {

        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        mDownloadUtil = DownloadUtil.get()
                .setDir(folder.getPath())
                .setFileName(getString(R.string.app_name) + ".apk");
    }

    protected void forceDownloadApk() {
        if (UpdateManager.isHasPreloadApk(mLatestBean.pkg_url)) {
            mOkView.setText("安装");
            UpdateManager.installApk(getContext(), SPHelper.getInstance().getApkPath(mLatestBean.pkg_url));
        } else {
            mProgressBar = new LoadingIndicatorDialog(getActivity());
            mProgressBar.show();
            DownloadUtil.get().setListener(this).download(mLatestBean.pkg_url);
        }
    }

    @OnClick(R2.id.update_dialog_cancel)
    public void cancelUpdate(View view) {
        dismissAllowingStateLoss();
        if (!UpdateManager.isHasPreloadApk(mLatestBean.pkg_url) && NetUtils.isWifi(getActivity())) {
            if (mDownloadUtil == null) {
                initDownload();
            }
            mDownloadUtil.setListener(new DownloadUtil.OnDownloadListener() {
                @Override
                public void onLoading(int progress) {
                }

                @Override
                public void onSuccess(String path) {
                    SPHelper.getInstance().setApkPath(mLatestBean.pkg_url, path);
                }

                @Override
                public void onFail(String err) {
                }
            }).download(mLatestBean.pkg_url);
        }
        AnalyticUtil.cancel(getContext());
    }

    @Override
    public void onLoading(int progress) {

    }

    @Override
    public void onSuccess(String path) {
        UpdateManager.installApk(getContext(), path);
        SPHelper.getInstance().setApkPath(mLatestBean.pkg_url, path);
        mProgressBar.dismiss();
        mOkView.setEnabled(true);
        mOkView.setText("安装");
    }

    protected void installPreloadApk() {
        UpdateManager.installApk(getContext(), SPHelper.getInstance().getApkPath(mLatestBean.pkg_url));
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
}
