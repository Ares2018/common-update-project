package cn.daily.news.update;

/**
 * Created by lixinke on 2017/8/31.
 */

public class UpdateResponse {
    public int code;
    public DataBean data;
    public static class DataBean {
        public VersionBean latest;
    }
}
