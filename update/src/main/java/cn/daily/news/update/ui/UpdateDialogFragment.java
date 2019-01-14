package cn.daily.news.update.ui;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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

import cn.daily.news.update.Constants;
import cn.daily.news.update.R;
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
public class UpdateDialogFragment extends DialogFragment implements View.OnClickListener {
    protected TextView mTitleView;
    protected TextView mMsgView;
    protected View mCancelView;
    protected TextView mOkView;

    protected VersionBean mLatestBean;
    private DownloadUtil mDownloadUtil;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        Bundle args = getArguments();
        if (args != null) {
            mLatestBean = (VersionBean) getArguments().getSerializable(Constants.Key.UPDATE_INFO);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        View rootView = inflater.inflate(R.layout.fragment_update_dialog, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mTitleView = view.findViewById(R.id.update_dialog_title);
        mMsgView = view.findViewById(R.id.update_dialog_msg);
        mMsgView.setMovementMethod(ScrollingMovementMethod.getInstance());
        mOkView = view.findViewById(R.id.update_dialog_ok);
        mOkView.setOnClickListener(this);
        mCancelView = view.findViewById(R.id.update_dialog_cancel);
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

    private void checkPermission() {
        int write = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (write != PackageManager.PERMISSION_GRANTED || read != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    protected String getTitle() {
        return getString(R.string.text_default_title, mLatestBean.version);
    }

    protected String getRemark() {
        return mLatestBean.remark;
    }

    protected String getOKText() {
        return getString(R.string.text_update);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.update_dialog_ok) {
            updateApk(v);
        } else if (id == R.id.update_dialog_cancel) {
            cancelUpdate(v);
        }
    }

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


    protected void installPreloadApk() {
        UpdateManager.installApk(getContext(), SPHelper.getInstance().getApkPath(mLatestBean.pkg_url));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
