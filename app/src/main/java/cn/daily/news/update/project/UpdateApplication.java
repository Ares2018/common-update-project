package cn.daily.news.update.project;

import android.app.Application;

import com.aliya.uimode.UiModeManager;
import com.zjrb.core.db.ThemeMode;
import com.zjrb.core.utils.AppUtils;
import com.zjrb.core.utils.SettingManager;
import com.zjrb.core.utils.UIUtils;

/**
 * Created by lixinke on 2017/10/19.
 */

public class UpdateApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        UIUtils.init(this);
        UIUtils.init(this);
        ThemeMode.initTheme(R.style.AppTheme, R.style.AppThemeNight);
        UiModeManager.init(this, R.styleable.SupportUiMode);
        ThemeMode.setUiMode(false);
        AppUtils.setChannel("update");
    }
}
