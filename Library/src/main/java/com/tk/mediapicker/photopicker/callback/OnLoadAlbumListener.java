package com.tk.mediapicker.photopicker.callback;


import com.tk.mediapicker.photopicker.bean.AlbumBean;
import com.tk.mediapicker.photopicker.bean.AlbumFolderBean;

import java.util.List;

/**
 * Created by TK on 2016/9/26.
 * load相册完毕回调
 */

public interface OnLoadAlbumListener {

    void onComplete(List<AlbumBean> albumList, List<AlbumFolderBean> albumFolderList);
}
