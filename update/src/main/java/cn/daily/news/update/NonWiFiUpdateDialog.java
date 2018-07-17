package cn.daily.news.update;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import cn.daily.news.analytics.Analytics;

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
        JSONObject properties=new JSONObject();
        try {
            properties.put("pageType","引导页");
            properties.put("clickTabName","取消升级");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Analytics.AnalyticsBuilder(getContext(),"100011","100011","appTabClick",false)
                .setEvenName("引导老版本用户升级安装点击")
                .setPageType("引导页")
                .setProperties(properties)
                .build()
                .send();
    }
}
