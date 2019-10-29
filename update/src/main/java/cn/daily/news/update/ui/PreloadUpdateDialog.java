package cn.daily.news.update.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;

import cn.daily.news.update.R;
import cn.daily.news.update.UpdateType;

/**
 * Created by lixinke on 2017/10/19.
 */

public class PreloadUpdateDialog extends UpdateDialogFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTitleView.setGravity(Gravity.LEFT);
    }

    @Override
    protected String getOKText() {
        return getString(R.string.update_ok);
    }

    @Override
    public void updateApk(View view) {
        installPreloadApk();
    }

    @Override
    public void cancelUpdate(View view) {
        super.cancelUpdate(view);
    }

    @Override
    protected SpannableString getTitle() {
        String title = getString(R.string.text_title_preload) + "\t";
        String version = " " + mLatestBean.version + " ";
        SpannableString spannableString = new SpannableString(title + version);
        spannableString.setSpan(new AbsoluteSizeSpan(12, true), title.length(), title.length() + version.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ffffff")), title.length(), title.length() + version.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.update_top_title_tip_color)), title.length(), title.length() + version.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    @Override
    protected UpdateType getType() {
        return UpdateType.PRELOAD;
    }
}
