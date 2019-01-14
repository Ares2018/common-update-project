package cn.daily.news.update.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import cn.daily.news.update.R;
import cn.daily.news.update.UpdateType;
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
        return getString(R.string.text_non_wifi_remark);
    }

    @Override
    protected String getOKText() {
        return getString(R.string.text_non_wifi_update);
    }

    @Override
    public void updateApk(View view) {
        downloadApk();
        AnalyticUtil.ok(getContext());
    }

    @Override
    protected UpdateType getType() {
        return UpdateType.NON_WIFI;
    }
}
