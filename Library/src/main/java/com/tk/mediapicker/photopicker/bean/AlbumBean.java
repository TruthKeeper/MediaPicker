package com.tk.mediapicker.photopicker.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by TK on 2016/8/24.
 * 相册bean
 */
public class AlbumBean implements Parcelable {
    private String path;
    private String name;
    private long date;

    public AlbumBean(String path, String name, long date) {
        this.path = path;
        this.name = name;
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AlbumBean) {
            AlbumBean bean = (AlbumBean) o;
            return bean.getPath().equals(path)
                    && bean.getName().equals(name)
                    && bean.getDate() == date;
        }
        return false;
    }

    @Override
    public String toString() {
        return "AlbumBean{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", date=" + date +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeString(this.name);
        dest.writeLong(this.date);
    }

    protected AlbumBean(Parcel in) {
        this.path = in.readString();
        this.name = in.readString();
        this.date = in.readLong();
    }

    public static final Creator<AlbumBean> CREATOR = new Creator<AlbumBean>() {
        @Override
        public AlbumBean createFromParcel(Parcel source) {
            return new AlbumBean(source);
        }

        @Override
        public AlbumBean[] newArray(int size) {
            return new AlbumBean[size];
        }
    };
}
