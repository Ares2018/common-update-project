package cn.daily.news.update.project;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cn.daily.news.update.UpdateManager;
import cn.daily.news.update.model.VersionBean;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new UpdateManager().checkUpdate(MainActivity.this, null,);


                VersionBean versionBean=new VersionBean();
                versionBean.pkg_url="https://stcbeta.8531.cn/assets/20181227/1545903113798_5c249c099949d8436136e653.apk";
                versionBean.remark="<p>1、测试 5.5.0 升级到 5.6.2</p>\n" +
                        "\n" +
                        "<p>2、测试 5.6.1 升级到 5.6.2</p>\n" +
                        "\n" +
                        "<p>3、本地登陆用户，app 升级后，有极小概率出现无法启动的现象</p>\n" +
                        "\n" +
                        "<p>4、4.4.4 的大屏幕安装失败的情况</p>\n";
                versionBean.version="5.0.0";
                versionBean.version_code=5000;
                UpdateManager.checkUpdate(MainActivity.this,versionBean);
            }
        });

    }


}
