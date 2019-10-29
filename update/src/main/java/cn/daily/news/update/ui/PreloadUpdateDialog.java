package cn.daily.news.update.ui;

import android.text.SpannableString;
import android.view.View;

import cn.daily.news.update.R;
import cn.daily.news.update.UpdateType;

/**
 * Created by lixinke on 2017/10/19.
 */

public class PreloadUpdateDialog extends UpdateDialogFragment {
    @Override
    protected String getOKText() {
        return getString(R.string.update_ok);
    }

    @Override
    public void updateApk(View view) {
        installPreloadApk();
    }

    @Override
    public void cancelUpdate(View view) {
        super.cancelUpdate(view);
    }

    @Override
    protected SpannableString getTitle() {
        return new SpannableString(getString(R.string.text_title_preload, mLatestBean.version));
    }

    @Override
    protected UpdateType getType() {
        return UpdateType.PRELOAD;
    }
}
