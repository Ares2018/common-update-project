package cn.daily.news.update.ui;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import cn.daily.news.update.Constants;
import cn.daily.news.update.R;
import cn.daily.news.update.UpdateManager;
import cn.daily.news.update.UpdateType;
import cn.daily.news.update.model.VersionBean;
import cn.daily.news.update.notify.NotifyDownloadManager;
import cn.daily.news.update.util.DownloadAPKManager;
import cn.daily.news.update.network.NetworkHelper;
import cn.daily.news.update.util.SPManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateDialogFragment extends DialogFragment implements View.OnClickListener {
    protected TextView mTitleView;
    protected TextView mMsgView;
    protected View mCancelView;
    protected TextView mOkView;

    protected VersionBean mLatestBean;
    private DownloadAPKManager mDownloadManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mLatestBean = (VersionBean) getArguments().getSerializable(Constants.Key.UPDATE_INFO);
        }
        mDownloadManager = new DownloadAPKManager(getContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);

        int layoutId = UpdateManager.getLayoutId();
        if (layoutId == 0) {
            layoutId = R.layout.module_update_fragment_update_dialog;
        }
        View rootView = inflater.inflate(layoutId, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mTitleView = view.findViewById(R.id.update_title);
        mMsgView = view.findViewById(R.id.update_remark);
        mMsgView.setMovementMethod(ScrollingMovementMethod.getInstance());
        mOkView = view.findViewById(R.id.update_ok);
        mOkView.setOnClickListener(this);
        mCancelView = view.findViewById(R.id.update_cancel);
        mCancelView.setOnClickListener(this);
        if (mLatestBean != null) {
            if (!TextUtils.isEmpty(getRemark())) {
                mMsgView.setText(Html.fromHtml(getRemark()));
            } else {
                mMsgView.setText(getString(R.string.text_default_remark));
            }
            mTitleView.setText(getTitle());
            mOkView.setText(getOKText());
        }
    }

    protected SpannableString getTitle() {
        String title = getString(R.string.text_default_title) + "\n";
        String version = " " + mLatestBean.version + " ";
        SpannableString spannableString = new SpannableString(title + version);
        spannableString.setSpan(new AbsoluteSizeSpan(12, true), title.length(), title.length() + version.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ffffff")), title.length(), title.length() + version.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.update_top_title_tip_color)), title.length(), title.length() + version.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    protected String getRemark() {
        return mLatestBean.remark;
    }

    protected String getOKText() {
        return getString(R.string.update_ok);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.update_ok) {
            updateApk(v);
        } else if (id == R.id.update_cancel) {
            cancelUpdate(v);
        }
        if (UpdateManager.getOnOperateListener() != null) {
            UpdateManager.getOnOperateListener().onOperate(getType(), v.getId());
        }
    }

    public void updateApk(View view) {
        if (NetworkHelper.isWifi(getActivity())) {
            downloadApk();
        } else {
            dismissAllowingStateLoss();
            NonWiFiUpdateDialog dialog = new NonWiFiUpdateDialog();
            Bundle args = new Bundle();
            args.putSerializable(Constants.Key.UPDATE_INFO, mLatestBean);
            dialog.setArguments(args);
            dialog.show(getFragmentManager(), "updateDialog");
        }
    }

    protected UpdateType getType() {
        return UpdateType.NORMAL;
    }

    public void cancelUpdate(View view) {
        dismissAllowingStateLoss();
        if (!UpdateManager.isHasPreloadApk(mLatestBean.pkg_url) && NetworkHelper.isWifi(getActivity())) {
            mDownloadManager.setListener(new CancelDownloadListener()).download(mLatestBean.pkg_url);
        }
    }

    protected void downloadApk() {
        dismissAllowingStateLoss();
        new NotifyDownloadManager(getActivity(), mDownloadManager, mLatestBean.version, mLatestBean.pkg_url).startDownloadApk();
    }


    protected void installPreloadApk() {
        UpdateManager.installApk(getContext(), SPManager.getInstance().getApkPath(mLatestBean.pkg_url));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private class CancelDownloadListener implements DownloadAPKManager.OnDownloadListener {
        @Override
        public void onLoading(int progress) {
        }

        @Override
        public void onSuccess(String path) {
            SPManager.getInstance().setApkPath(mLatestBean.pkg_url, path);
        }

        @Override
        public void onFail(String err) {
        }

        @Override
        public void onStart(long total) {
            SPManager.getInstance().setApkSize(mLatestBean.pkg_url, total);
        }
    }
}
