package cn.daily.news.update;

import android.view.View;

/**
 * Created by lixinke on 2017/10/19.
 */

public class PreloadUpdateDialog extends UpdateDialogFragment {
    @Override
    protected String getOKText() {
        return "更新";
    }

    @Override
    public void updateApk(View view) {
        installPreloadApk();
    }

    @Override
    protected String getTitle() {
        return "已在WIFI下为您预下载了最新版本"+mLatestBean.version+"(版本号),是否立即更新?";
    }
}
