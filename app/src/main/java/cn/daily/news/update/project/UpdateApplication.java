package cn.daily.news.update.project;

import android.app.Application;

import cn.daily.news.update.UpdateManager;

/**
 * Created by lixinke on 2017/10/19.
 */

public class UpdateApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        UIUtils.init(this);
//        SettingManager.init(this);
//        SettingManager.init(this);
//        UIUtils.init(this);
//        UiModeManager.init(this, R.styleable.SupportUiMode);
//        ThemeMode.setUiMode(false);

        UpdateManager.init(this);
    }
}
