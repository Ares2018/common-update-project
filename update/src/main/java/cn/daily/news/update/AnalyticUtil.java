package cn.daily.news.update;

import android.content.Context;

import cn.daily.news.analytics.Analytics;

public class AnalyticUtil {
    public static void cancel(Context context) {
        new Analytics.AnalyticsBuilder(context, "100012", "100012", "AppTabClick", false)
                .setEvenName("升级弹框取消按钮点击")
                .setPageType("引导页")
                .pageType("引导页")
                .clickTabName("取消升级")
                .build()
                .send();
    }

    public static void ok(Context context) {
        new Analytics.AnalyticsBuilder(context, "100011", "100011", "AppTabClick", false)
                .setEvenName("引导老版本用户升级安装点击")
                .setPageType("引导页")
                .pageType("引导页")
                .clickTabName("升级")
                .build()
                .send();
    }
}
