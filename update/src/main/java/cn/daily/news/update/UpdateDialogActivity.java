package cn.daily.news.update;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zjrb.core.utils.DownloadUtil;
import com.zjrb.core.utils.NetUtils;
import com.zjrb.core.utils.SettingManager;
import com.zjrb.core.utils.UIUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class UpdateDialogActivity extends AppCompatActivity implements DownloadUtil.OnDownloadListener {
    private Unbinder mUnBinder;
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

    private int mLastVersionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_dialog);
        mUnBinder = ButterKnife.bind(this);
        setFinishOnTouchOutside(false);
        UIUtils.init(getApplication());
        SettingManager.init(getApplication());

        Intent intent = getIntent();
        if (intent != null) {
            mApkUrl = intent.getStringExtra(Update.Key.url);
            mTitleView.setText(intent.getStringExtra(Update.Key.title));
            mMsgView.setText(intent.getStringExtra(Update.Key.message));
            if (intent.getBooleanExtra(Update.Key.isForce, false)) {
                mCancelView.setVisibility(View.GONE);
            }
            mLastVersionCode = intent.getIntExtra(Update.Key.lastVersionCode, 0);

            if (isHasPreloadedApk(mLastVersionCode)) {
                mOkView.setText("安装");
                mApkPath = SettingManager.getInstance().getApkPath();
            } else if (mLastVersionCode != 0) {
                SettingManager.getInstance().setLastApkVersionCode(mLastVersionCode);
                SettingManager.getInstance().setApkPath(null);
            }
        }
    }

    private boolean isHasPreloadedApk(int lastVersionCode) {
        if (lastVersionCode == SettingManager.getInstance().getLastApkVersionCode()) {
            String path = SettingManager.getInstance().getApkPath();
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                if (file.exists()) {
                    return true;
                }
            }
        }
        return false;
    }

    @OnClick(R2.id.update_dialog_ok)
    public void updateApk(View view) {
        if (!TextUtils.isEmpty(mApkUrl) && ("更新".equals(mOkView.getText()) || "继续更新".equals(mOkView.getText()))) {
            if (NetUtils.isWifi() || "继续更新".equals(mOkView.getText())) {
                view.setEnabled(false);
                mProgressBar.setVisibility(View.VISIBLE);
                mMsgView.setVisibility(View.GONE);
                DownloadUtil.get().setListener(this).download(mApkUrl);
            } else {
                mMsgView.setText("您正在使用非WIFI环境，更新版本可能产生超额流量费用");
                mOkView.setText("继续更新");
            }
        }

        if (!TextUtils.isEmpty(mApkPath) && "安装".equals(mOkView.getText())) {
            UpdateManager.getInstance().installApk(this, mApkPath);
        }
    }

    @OnClick(R2.id.update_dialog_cancel)
    public void cancelUpdate(View view) {
        if (!isHasPreloadedApk(mLastVersionCode) && NetUtils.isWifi()) {
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
            }).download(mApkUrl);
        }
        finish();
    }

    @Override
    public void onLoading(int progress) {

    }

    @Override
    public void onSuccess(String path) {

        UpdateManager.getInstance().installApk(this, path);
        SettingManager.getInstance().setApkPath(path);

        mApkPath = path;

        mMsgView.setVisibility(View.VISIBLE);
        if (getIntent() != null) {
            mMsgView.setText(getIntent().getStringExtra(Update.Key.message));
        }
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (getIntent() != null && getIntent().getBooleanExtra(Update.Key.isForce, false)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnBinder.unbind();
        DownloadUtil.get().setListener(null);
    }
}
