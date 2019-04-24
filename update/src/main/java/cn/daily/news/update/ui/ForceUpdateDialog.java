package cn.daily.news.update.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import cn.daily.news.update.R;
import cn.daily.news.update.UpdateManager;
import cn.daily.news.update.UpdateType;
import cn.daily.news.update.util.AnalyticUtil;
import cn.daily.news.update.util.DownloadUtil;
import cn.daily.news.update.util.SPHelper;

/**
 * Created by lixinke on 2017/10/19.
 */

public class ForceUpdateDialog extends UpdateDialogFragment implements DownloadUtil.OnDownloadListener {
    private LoadingIndicatorDialog mProgressBar;
    private View mDividerView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCancelView.setVisibility(View.GONE);
        mDividerView = view.findViewById(R.id.update_btn_divider);
        if (mDividerView != null) {
            mDividerView.setVisibility(View.GONE);
        }

    }

    @Override
    public void updateApk(View view) {
        forceDownloadApk();
        AnalyticUtil.ok(getContext());
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
            UpdateManager.installApk(getContext(), SPHelper.getInstance().getApkPath(mLatestBean.pkg_url));
        } else {
            mProgressBar = new LoadingIndicatorDialog(getActivity());
            mProgressBar.show();
            DownloadUtil.get().setListener(this).download(mLatestBean.pkg_url);
        }
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
    }

    @Override
    public void onFail(String err) {
        mProgressBar.dismiss();
        mMsgView.setText(getString(R.string.text_tip_fail));
        mMsgView.setVisibility(View.VISIBLE);
    }
}
