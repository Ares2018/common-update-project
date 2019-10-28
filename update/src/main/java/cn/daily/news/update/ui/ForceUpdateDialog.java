package cn.daily.news.update.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import cn.daily.news.update.R;
import cn.daily.news.update.UpdateManager;
import cn.daily.news.update.UpdateType;
import cn.daily.news.update.util.DownloadAPKManager;
import cn.daily.news.update.util.SPManager;

/**
 * Created by lixinke on 2017/10/19.
 */

public class ForceUpdateDialog extends UpdateDialogFragment implements DownloadAPKManager.OnDownloadListener {
    private UpdateProgressBar mProgressBar;
    private View mDividerView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCancelView.setVisibility(View.GONE);
        mDividerView = view.findViewById(R.id.update_btn_divider);
        mProgressBar=view.findViewById(R.id.update_dialog_progressBar);
        if (mDividerView != null) {
            mDividerView.setVisibility(View.GONE);
        }

    }

    @Override
    public void updateApk(View view) {
        forceDownloadApk();
    }

    @Override
    protected UpdateType getType() {
        return UpdateType.FORCE;
    }

    @Override
    protected String getOKText() {
        return getString(R.string.text_update);
    }

    protected void forceDownloadApk() {
        if (UpdateManager.isHasPreloadApk(mLatestBean.pkg_url)) {
            UpdateManager.installApk(getContext(), SPManager.getInstance().getApkPath(mLatestBean.pkg_url));
        } else {
            mMsgView.setVisibility(View.INVISIBLE);
           new DownloadAPKManager(getContext()).setListener(this).download(mLatestBean.pkg_url);
        }
    }

    @Override
    public void onStart(long total) {
        SPManager.getInstance().setApkSize(mLatestBean.pkg_url,total);
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setMax(100);
        mProgressBar.setProgress(0);
    }

    @Override
    public void onLoading(int progress) {
        mProgressBar.setProgress(progress);
    }

    @Override
    public void onSuccess(String path) {
        mProgressBar.setProgress(100);
        UpdateManager.installApk(getContext(), path);
        SPManager.getInstance().setApkPath(mLatestBean.pkg_url, path);
        mProgressBar.setVisibility(View.GONE);
        mOkView.setEnabled(true);
    }

    @Override
    public void onFail(String err) {
        mProgressBar.setVisibility(View.GONE);
        mMsgView.setText(getString(R.string.text_tip_fail));
        mMsgView.setVisibility(View.VISIBLE);
    }
}
