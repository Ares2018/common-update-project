package cn.daily.news.update;

/**
 * Created by lixinke on 2017/8/31.
 */

public class UpdateResponse {
    public int code;
    public DataBean data;

    public static class DataBean {
        public CurrentBean current;
        public LatestBean latest;

        public static class CurrentBean {
            public int id;
            public String version;
            public int version_code;
            public String pkg_url;
            public long publish_time;
            public boolean force_upgraded;
            public String remark;
        }

        public static class LatestBean {
            public int id;
            public String version;
            public int version_code;
            public String pkg_url;
            public long publish_time;
            public boolean force_upgraded;
            public String remark;
        }
    }
}
