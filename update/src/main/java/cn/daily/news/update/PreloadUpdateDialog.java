package cn.daily.news.update;

import android.view.View;

/**
 * Created by lixinke on 2017/10/19.
 */

public class PreloadUpdateDialog extends UpdateDialogFragment {
    @Override
    protected String getOKText() {
        return "安装";
    }

    @Override
    public void updateApk(View view) {
        installPreloadApk();
    }
}
