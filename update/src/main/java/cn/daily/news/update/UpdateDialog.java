package cn.daily.news.update;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

/**
 * Created by lixinke on 2017/8/31.
 */

public class UpdateDialog extends AlertDialog {
    protected UpdateDialog(@NonNull Context context) {
        super(context,R.style.UpdateDialogTheme);
    }
}
