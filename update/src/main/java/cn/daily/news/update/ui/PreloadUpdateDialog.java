package cn.daily.news.update.ui;

import android.view.View;

import cn.daily.news.update.R;

/**
 * Created by lixinke on 2017/10/19.
 */

public class PreloadUpdateDialog extends UpdateDialogFragment {
    @Override
    protected String getOKText() {
        return getString(R.string.text_install);
    }

    @Override
    public void updateApk(View view) {
        installPreloadApk();
    }

    @Override
    protected String getTitle() {
        return getString(R.string.text_title_preload, mLatestBean.version);
    }
}
