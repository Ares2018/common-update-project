package cn.daily.news.update;

public class Constants {
    public interface Key {
        String UPDATE_INFO = "update_info";
        String SCHEME = "scheme";
        String APK_VERSION_CODE = "versionCode";
        String APK_URL = "download_apk_url";
        String APK_PATH = "download_apk_local_path";
        String APK_VERSION_NAME = "download_apk_version";
    }

    public interface Action {
        String DOWNLOAD_COMPLETE = "download_complete";
        String DOWNLOAD_RETRY = "download_retry";
    }
}
