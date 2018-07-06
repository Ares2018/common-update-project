package cn.daily.news.update;

public interface OnUpdateListener {
    void onUpdate(UpdateResponse.DataBean dataBean);

    void onError(String errMsg, int errCode);
}
