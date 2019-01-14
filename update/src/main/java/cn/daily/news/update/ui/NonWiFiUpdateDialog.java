package cn.daily.news.update.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import cn.daily.news.update.util.AnalyticUtil;

/**
 * Created by lixinke on 2017/10/19.
 */

public class NonWiFiUpdateDialog extends UpdateDialogFragment {
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTitleView.setVisibility(View.GONE);
    }

    @Override
    protected String getRemark() {
        return "您正在使用非Wi-Fi网络，更新版本可能产生超额流量费用";
    }

    @Override
    protected String getOKText() {
        return "继续更新";
    }

    @Override
    public void updateApk(View view) {
        downloadApk();
        AnalyticUtil.ok(getContext());
    }
}
