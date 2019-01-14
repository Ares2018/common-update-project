package cn.daily.news.update.ui;

import android.view.View;

import cn.daily.news.update.R;
import cn.daily.news.update.UpdateManager;
import cn.daily.news.update.UpdateType;
import cn.daily.news.update.util.AnalyticUtil;

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
        if(UpdateManager.getOnOperateListener()!=null){
            UpdateManager.getOnOperateListener().onOperate(UpdateType.PRELOAD,view.getId());
        }
        AnalyticUtil.ok(getContext());
        installPreloadApk();
    }

    @Override
    public void cancelUpdate(View view) {
        super.cancelUpdate(view);
    }

    @Override
    protected String getTitle() {
        return getString(R.string.text_title_preload, mLatestBean.version);
    }

    @Override
    protected UpdateType getType() {
        return UpdateType.PRELOAD;
    }
}
