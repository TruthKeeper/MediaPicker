package com.tk.mediapicker.photopicker.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TK on 2016/8/24.
 * 相册文件夹预览bean
 */
public class AlbumFolderBean {
    //封面路径
    private String indexPath;
    //文件夹路径
    private String folderPath;
    //文件夹名
    private String folderName;
    //文件夹下图片数量
    private List<AlbumBean> albumList = new ArrayList<AlbumBean>();

    public AlbumFolderBean(String indexPath, String folderPath, String folderName) {
        this.indexPath = indexPath;
        this.folderPath = folderPath;
        this.folderName = folderName;
    }

    public void addBean(AlbumBean albumBean) {
        this.albumList.add(albumBean);
    }

    public void addBeanList(List<AlbumBean> albumList) {
        this.albumList.addAll(albumList);
    }

    public String getIndexPath() {
        return indexPath;
    }

    public void setIndexPath(String indexPath) {
        this.indexPath = indexPath;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public List<AlbumBean> getAlbumList() {
        return albumList;
    }

    public void setAlbumList(List<AlbumBean> albumList) {
        this.albumList = albumList;
    }
}
