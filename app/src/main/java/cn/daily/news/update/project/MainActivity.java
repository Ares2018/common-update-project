package cn.daily.news.update.project;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.zjrb.core.common.base.BaseActivity;

import java.io.File;

import cn.daily.news.update.UpdateManager;
import cn.daily.news.update.VersionBean;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UpdateManager.getInstance().setLayoutId(R.layout.custom_update_layout).checkUpdate(this);

        findViewById(R.id.update).setOnClickListener(this);
        findViewById(R.id.clear).setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UpdateManager.getInstance().cancel();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.update) {
            VersionBean bean = new VersionBean();
            bean.force_upgraded = true;
            bean.pkg_url = "https://stc-new.8531.cn/android/zhejiangdaily-bianfeng-release.apk";
            bean.version_code = 50003;
            bean.version="5.0.3";
            bean.remark = "<div>1.新增直播预告功能，精彩直播不容错过<br>2.视频支持重力感应横竖屏切换，体验更流畅<br>3.详情页加载速度优化，缩短加载时间<br>4.修复bug，提升稳定性</div>";
            UpdateManager.getInstance().setLayoutId(0).checkUpdate(this, bean);
        } else if (id == R.id.clear) {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), getString(R.string.app_name) + ".apk");
            if (file != null && file.exists()) {
                file.delete();
            }
            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
        }
    }
}
