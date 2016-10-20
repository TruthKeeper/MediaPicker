package com.tk.mediapicker.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TK on 2016/10/18.
 * 多媒体文件夹预览bean
 */
public class MediaFolderBean {
    //封面路径
    private String indexPath;
    //文件夹名
    private String folderName;
    //封面是否video
    private boolean isIndexVideo;
    //文件夹下集合
    private List<MediaBean> mediaList = new ArrayList<MediaBean>();

    public MediaFolderBean(String indexPath, String folderName) {
        this.indexPath = indexPath;
        this.folderName = folderName;
    }


    public void addBean(MediaBean mediaBean) {
        this.mediaList.add(mediaBean);
    }

    public String getIndexPath() {
        return indexPath;
    }

    public void setIndexPath(String indexPath) {
        this.indexPath = indexPath;
    }


    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public boolean isIndexVideo() {
        return isIndexVideo;
    }

    public void setIndexVideo(boolean indexVideo) {
        isIndexVideo = indexVideo;
    }

    public List<MediaBean> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<MediaBean> mediaList) {
        this.mediaList = mediaList;
    }

}
