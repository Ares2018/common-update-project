package cn.daily.news.update.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import cn.daily.news.update.R;
import cn.daily.news.update.UpdateType;
import cn.daily.news.update.util.DownloadAPKManager;

/**
 * Created by lixinke on 2017/10/19.
 */

public class ForceUpdateDialog extends UpdateDialogFragment  {
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
        updateUI();
    }

    @Override
    protected UpdateType getType() {
        return UpdateType.FORCE;
    }

    @Override
    protected String getOKText() {
        return getString(R.string.update_ok);
    }

    @Override
    protected void startDownloadApk(String pkg_url) {
        new DownloadAPKManager(getContext()).setListener(this).download(pkg_url);
    }
}
