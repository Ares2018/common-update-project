package cn.daily.news.update.analytic;

/**
 * 检测更新数据采集接口
 */
public interface IAnalytic {
    /**
     * 点击更新或者取消时此方法会被调用
     * @param updateType { NORMAL//正常更新,FORCE//强制更新,NON_WIFI//移动网络更新}
     * @param operationType { UPDATE//更新按钮,CANCEL//取消按钮}
     */
    void onAnalytic(UpdateType updateType, OperationType operationType);
}
