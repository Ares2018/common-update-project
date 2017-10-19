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

        findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateManager().checkUpdate(MainActivity.this, null);
            }
        });
    }


}
