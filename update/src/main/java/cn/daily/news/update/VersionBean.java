package cn.daily.news.update;

import java.io.Serializable;

public class VersionBean implements Serializable {
    /**
     * 最新版本号versionName
     */
    public String version;
    /**
     * 最新版本code versionCode
     */
    public int version_code;
    /**
     * 最新APK下载地址
     */
    public String pkg_url;
    /**
     * 是否强制更新
     */
    public boolean force_upgraded;
    /**
     * 更新信息说明
     */
    public String remark;
}