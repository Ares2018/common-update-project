package cn.daily.news.update;

import java.io.Serializable;

public class VersionBean implements Serializable {
    public int id;
    public int device_type;
    public String version;
    public int version_code;
    public String pkg_url;
    public long publish_time;
    public boolean force_upgraded;
    public String remark;
    public boolean isNeedUpdate;
    public String preloadPath;
}
