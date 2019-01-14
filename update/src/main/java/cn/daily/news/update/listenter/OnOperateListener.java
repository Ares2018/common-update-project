package cn.daily.news.update.listenter;


import cn.daily.news.update.UpdateType;

/**
 * 检测更新操作符监听
 */
public interface OnOperateListener {
    /**
     * 检测更新按钮操作符
     *
     * @param type 更新类型 {@link UpdateType#NON_WIFI,UpdateType#NORMAL,UpdateType#FORCE}
     * @param id   按钮ID{update_ok,update_cancel}
     */
    void onOperate(UpdateType type, int id);
}