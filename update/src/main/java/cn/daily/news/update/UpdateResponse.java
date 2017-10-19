package cn.daily.news.update;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lixinke on 2017/8/31.
 */

public class UpdateResponse {
    public int code;
    public DataBean data;
    public static class DataBean {
        public LatestBean latest;
        public static class LatestBean implements Parcelable {
            public int id;
            public int device_type;
            public String version;
            public int version_code;
            public String pkg_url;
            public long publish_time;
            public boolean force_upgraded;
            public String remark;
            public boolean isNeedUpdate;
            public String preloadPath;

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(this.id);
                dest.writeInt(this.device_type);
                dest.writeString(this.version);
                dest.writeInt(this.version_code);
                dest.writeString(this.pkg_url);
                dest.writeLong(this.publish_time);
                dest.writeByte(this.force_upgraded ? (byte) 1 : (byte) 0);
                dest.writeString(this.remark);
                dest.writeByte(this.isNeedUpdate ? (byte) 1 : (byte) 0);
                dest.writeString(this.preloadPath);
            }

            public LatestBean() {
            }

            protected LatestBean(Parcel in) {
                this.id = in.readInt();
                this.device_type = in.readInt();
                this.version = in.readString();
                this.version_code = in.readInt();
                this.pkg_url = in.readString();
                this.publish_time = in.readLong();
                this.force_upgraded = in.readByte() != 0;
                this.remark = in.readString();
                this.isNeedUpdate = in.readByte() != 0;
                this.preloadPath = in.readString();
            }

            public static final Parcelable.Creator<LatestBean> CREATOR = new Parcelable.Creator<LatestBean>() {
                @Override
                public LatestBean createFromParcel(Parcel source) {
                    return new LatestBean(source);
                }

                @Override
                public LatestBean[] newArray(int size) {
                    return new LatestBean[size];
                }
            };
        }
    }
}
