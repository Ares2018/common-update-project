package cn.daily.news.update;

public interface Constants {
    interface Key {
        String UPDATE_INFO = "update_info";
        String VERSION_CODE = "version_code";
        String SCHEME = "scheme";
        String APK_URL = "download_apk_url";
        String APK_PATH = "download_apk_local_path";
        String APK_VERSION = "download_apk_version";
        String APK_FOLDER = "download";
    }

    interface Action {
        String DOWNLOAD_COMPLETE = "download_complete";
        String DOWNLOAD_RETRY = "download_retry";
    }
}
