package cn.daily.news.update.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SPManager {
    private static final String PREFERENCE_NAME = "update";
    private static final SPManager ourInstance = new SPManager();
    private SharedPreferences mPreferences;

    public static SPManager getInstance() {
        return ourInstance;
    }

    private Context mContext;

    private SPManager() {
    }

    public void init(Context context) {
        mContext = context;
        if (mContext != null) {
            mPreferences = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        }
    }

    public void put(String key, Object value) {
        if (mPreferences == null) {
            return;
        }

        SharedPreferences.Editor editor = mPreferences.edit();
        if (editor == null) {
            return;
        }

        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        }

        editor.commit();
    }


    public void isNeedUpdate(boolean isNeedUpdate) {
        put(Name.IS_NEED_UPDATE, isNeedUpdate);
    }

    public void setApkPath(String pkg_url, String path) {
        put(pkg_url, path);
    }

    public String getApkPath(String pkg_url) {
        if (mPreferences == null) {
            return null;
        }
        return mPreferences.getString(pkg_url, "");
    }

    public void setLastApkVersionCode(int lastVersionCode) {
        put(Name.LAST_VERSION_CODE, lastVersionCode);
    }

    public int getLastApkVersionCode() {
        if (mPreferences == null) {
            return 0;
        }
        return mPreferences.getInt(Name.LAST_VERSION_CODE, 0);
    }

    public void setApkSize(String pkg_url, long total) {
        put(pkg_url + Name.APK_SIZE, total);
    }

    public long getApkSize(String pkg_url) {
        if (mPreferences == null) {
            return 0;
        }
        return mPreferences.getLong(pkg_url + Name.APK_SIZE, 0);
    }

    interface Name {
        String IS_NEED_UPDATE = "is_need_update";
        String LAST_VERSION_CODE = "last_version_code";
        String APK_SIZE = "apk_size";
    }
}
