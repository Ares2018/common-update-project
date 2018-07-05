package cn.daily.news.update;

import java.io.Serializable;

public class VersionBean implements Serializable {
    public String version;
    public int version_code;
    public String pkg_url;
    public boolean force_upgraded;
    public String remark;

    public boolean isNeedUpdate;
    protected String preloadPath;
}