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
                .build()
                .send();
    }

    @Override
    protected String getOKText() {
        return "更新";
    }
}
