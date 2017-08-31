package cn.daily.news.update;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zjrb.coreprojectlibrary.utils.UIUtils;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE_READ_EXTERNAL_STORAGE = 101;       //申请读取图片权限
    private static final int FEEDBACK_IMAGE_COLUMN = 3;                                 //反馈图片列数
    private static final int FEEDBACK_INFO_MAX_COUNT = 500;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UIUtils.init(getApplication());

        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this,UpdateDialogActivity.class));
                UpdateManager.getInstance().createForceUpdateDialog(MainActivity.this,300,false,"http://zj.zjol.com.cn/d/android/ZhejiangDaily-bianfeng-release.apk","1.型工能双方的身份\n2.策划ID方式发送到");

            }
        });



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE_READ_EXTERNAL_STORAGE);
            return;
        }

//        UpdateManager.getInstance().installApk(this, "/storage/emulated/0/24h/download/ZhejiangDaily-bianfeng-release.apk");
    }
}