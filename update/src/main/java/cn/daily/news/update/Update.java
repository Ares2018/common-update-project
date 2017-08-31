package cn.daily.news.update;

/**
 * Created by lixinke on 2017/8/31.
 */

public interface Update {
    interface Key {
        String title = "title";
        String message = "message";
        String url = "url";
        String isForce = "isForce";
        String lastVersionCode = "lastVersionCode";
    }
}
