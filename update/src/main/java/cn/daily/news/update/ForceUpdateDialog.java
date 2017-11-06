package cn.daily.news.update;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    }

    @Override
    protected String getOKText() {
        return "已在WIFI下为您预下载了最新版本"+mLatestBean.version+"(版本号),是否立即更新?";
    }
}
