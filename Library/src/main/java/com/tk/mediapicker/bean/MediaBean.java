package com.tk.mediapicker.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by TK on 2016/10/18.
 * 多媒体bean
 */
public class MediaBean implements Parcelable, Comparable<MediaBean> {
    //路径
    private String path;
    //文件名
    private String name;
    //日期
    private long date;
    //是否视频
    private boolean isVideo;
    //文件大小
    private long size;

    public MediaBean(String path, String name, long date, boolean isVideo, long size) {
        this.path = path;
        this.name = name;
        this.date = date;
        this.isVideo = isVideo;
        this.size = size;
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

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MediaBean mediaBean = (MediaBean) o;

        if (date != mediaBean.date) return false;
        if (isVideo != mediaBean.isVideo) return false;
        if (size != mediaBean.size) return false;
        if (path != null ? !path.equals(mediaBean.path) : mediaBean.path != null) return false;
        return name != null ? name.equals(mediaBean.name) : mediaBean.name == null;

    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (int) (date ^ (date >>> 32));
        result = 31 * result + (isVideo ? 1 : 0);
        result = 31 * result + (int) (size ^ (size >>> 32));
        return result;
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
        dest.writeByte(this.isVideo ? (byte) 1 : (byte) 0);
        dest.writeLong(this.size);
    }

    protected MediaBean(Parcel in) {
        this.path = in.readString();
        this.name = in.readString();
        this.date = in.readLong();
        this.isVideo = in.readByte() != 0;
        this.size = in.readLong();
    }

    public static final Parcelable.Creator<MediaBean> CREATOR = new Parcelable.Creator<MediaBean>() {
        @Override
        public MediaBean createFromParcel(Parcel source) {
            return new MediaBean(source);
        }

        @Override
        public MediaBean[] newArray(int size) {
            return new MediaBean[size];
        }
    };


    @Override
    public int compareTo(MediaBean another) {
        //倒序
        if (this.date > another.getDate()) {
            return -1;
        }
        return 1;
    }
}
