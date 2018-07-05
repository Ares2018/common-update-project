package cn.daily.news.update.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.daily.news.update.analytic.OperationType;
import cn.daily.news.update.UpdateManager;
import cn.daily.news.update.analytic.UpdateType;


/**
 * Created by lixinke on 2017/10/19.
 */

public class NonWiFiUpdateDialog extends UpdateDialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        setTitleVisible(View.GONE);
        return rootView;
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
        if(UpdateManager.getIAnalytic()!=null){
            UpdateManager.getIAnalytic().onAnalytic(UpdateType.NON_WIFI, OperationType.UPDATE);
        }
    }
}
