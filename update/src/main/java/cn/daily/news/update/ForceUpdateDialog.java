package cn.daily.news.update;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.daily.news.analytics.Analytics;

/**
 * Created by lixinke on 2017/10/19.
 */

public class ForceUpdateDialog extends UpdateDialogFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        setCancelable(false);
        hideCancel();
        return rootView;
    }

    @Override
    public void updateApk(View view) {
        forceDownloadApk();
        AnalyticUtil.ok(getContext());
    }

    @Override
    protected String getOKText() {
        return "更新";
    }
}
