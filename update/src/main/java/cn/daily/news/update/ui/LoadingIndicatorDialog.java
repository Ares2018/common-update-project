package cn.daily.news.update.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.daily.news.update.R;
import cn.daily.news.update.listenter.ICancelListener;

/**
 * 自定义加载中Dialog 双击back键取消操作
 *
 * @author a_liYa
 * @date 16/8/23 11:15.
 */
public class LoadingIndicatorDialog extends Dialog {

    private TextView mTvToast;
    private ImageView mIvIcon;

    // 提醒文本
    private String alertText;
    // 结束文本
    private String finishText;
    // 撤销提醒内容
    private String cancelText = "双击撤销!";

    private ICancelListener mCancelListener;

    public static final String TEXT_SUCCESS = "成功";
    public static final String TEXT_FAILURE = "失败";
    private static final int FINISH_DELAYED = 1200;

    public void setCancelListener(ICancelListener cancelListener) {
        this.mCancelListener = cancelListener;
    }

    public LoadingIndicatorDialog(Context context) {
        super(context, R.style.confirm_dialog);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); // 清除背景变暗
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    /**
     * 立刻关闭
     */
    public void finish() {
        try {
            dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @see #finish(boolean, String)
     */
    public void finish(boolean isSuccess) {
        finish(isSuccess, isSuccess ? TEXT_SUCCESS : TEXT_FAILURE);
    }

    /**
     * @see #finish(String, int)
     */
    public void finish(boolean isSuccess, String text) {
        finish(text, isSuccess
                ? R.mipmap.module_core_icon_loading_success
                : R.mipmap.module_core_icon_loading_failure);
    }

    /**
     * 设置厨师textView显示内容
     *
     * @param s
     */
    public void setToastText(String s) {
        if (mTvToast != null && !TextUtils.isEmpty(s)) {
            mTvToast.setText(s);
        }
    }

    /**
     * 显示结果，延迟 {@link #FINISH_DELAYED} 时间后关闭
     *
     * @param text  结果显示的文本
     * @param resId 结果显示的图标资源
     */
    public void finish(String text, @DrawableRes int resId) {
        mTvToast.setText(text);
        mIvIcon.setImageResource(resId);
        mTvToast.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, FINISH_DELAYED);
    }

    /**
     * 设置取消提醒内容文本
     *
     * @param cancelText
     */
    public void setCancelText(String cancelText) {
        this.cancelText = cancelText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View contentView = View.inflate(getContext(),
                R.layout.module_core_loading_alert_dialog, null);

        mIvIcon = contentView.findViewById(R.id.iv_icon);
        mTvToast = contentView.findViewById(R.id.tv_toast);
        mIvIcon.setImageResource(R.mipmap.module_core_icon_loading_progress);
        mIvIcon.startAnimation(getAnimation());
        setContentView(contentView);
        setCanceledOnTouchOutside(false);
    }

    @NonNull
    private Animation getAnimation() {
        Animation anim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setFillAfter(true);
        anim.setDuration(5000);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setRepeatMode(Animation.RESTART);
        anim.setInterpolator(new LinearInterpolator());
        return anim;
    }

    private long clickTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if ((System.currentTimeMillis() - clickTime) > 1000) {
                Toast.makeText(getContext(), cancelText, Toast.LENGTH_SHORT).show();
                clickTime = System.currentTimeMillis();
                return true;
            } else {
                if (mCancelListener != null) {
                    mCancelListener.onCancel();
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
