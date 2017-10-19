package cn.daily.news.update;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zjrb.core.utils.DownloadUtil;
import com.zjrb.core.utils.NetUtils;
import com.zjrb.core.utils.SettingManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


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
            NonWiFiUpdateDialog updateDialogFragment = new NonWiFiUpdateDialog();
            Bundle args = new Bundle();
            args.putParcelable(UpdateManager.Key.UPDATE_INFO, mLatestBean);
            updateDialogFragment.setArguments(args);
            updateDialogFragment.show(getFragmentManager(), "updateDialog");
        }
    }

    protected void downloadApk() {
        DownloadUtil.get().setListener(this).download(mLatestBean.pkg_url);
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
}
