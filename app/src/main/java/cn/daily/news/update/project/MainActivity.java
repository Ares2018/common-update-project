package cn.daily.news.update.project;

import android.os.Bundle;
import android.view.View;

import com.zjrb.core.common.base.BaseActivity;

import cn.daily.news.update.UpdateManager;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UpdateManager.getInstance().checkUpdate(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UpdateManager.getInstance().cancel();
    }
}
