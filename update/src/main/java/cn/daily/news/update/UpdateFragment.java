package cn.daily.news.update;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zjrb.coreprojectlibrary.utils.DownloadUtil;
import com.zjrb.coreprojectlibrary.utils.NetUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class UpdateFragment extends Fragment implements DownloadUtil.OnDownloadListener{
    private Unbinder mUnbinder;
    private String mApkUrl;

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
    private String mApkPath;

    public UpdateFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rooView = inflater.inflate(R.layout.fragment_update, container);
        mUnbinder = ButterKnife.bind(this, rooView);
        return rooView;
    }
    @OnClick(R2.id.update_dialog_ok)
    public void updateApk(View view) {
        if (!TextUtils.isEmpty(mApkUrl)) {
            if (NetUtils.isWifi()) {
                view.setEnabled(false);
                mProgressBar.setVisibility(View.VISIBLE);
                mMsgView.setVisibility(View.GONE);
                DownloadUtil.get().setListener(this).download(mApkUrl);
            } else {
                mTitleView.setVisibility(View.GONE);
                mMsgView.setText("您正在使用非WIFI环境，更新版本可能产生超额流量费用");
                mOkView.setText("继续更新");
            }
        }

    }

    @OnClick(R2.id.update_dialog_cancel)
    public void cancelUpdate(View view) {
        getActivity().finish();
    }

    @Override
    public void onLoading(int progress) {

    }

    @Override
    public void onSuccess(String path) {

        UpdateManager.getInstance().installApk(getActivity(), path);

        mApkPath = path;

        mMsgView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mOkView.setEnabled(true);
        mOkView.setText("安装");
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
        mUnbinder.unbind();
    }
}
