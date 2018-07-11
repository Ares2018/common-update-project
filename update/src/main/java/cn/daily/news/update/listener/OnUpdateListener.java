package cn.daily.news.update.listener;

import cn.daily.news.update.UpdateResponse;

public interface OnUpdateListener {
    void onUpdate(UpdateResponse.DataBean dataBean);

    void onError(String errMsg, int errCode);
}
