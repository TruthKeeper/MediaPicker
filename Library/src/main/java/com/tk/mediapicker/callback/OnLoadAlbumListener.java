package com.tk.mediapicker.callback;


import com.tk.mediapicker.bean.MediaBean;
import com.tk.mediapicker.bean.MediaFolderBean;

import java.util.List;

/**
 * Created by TK on 2016/9/26.
 * load相册完毕回调
 */

public interface OnLoadAlbumListener {

    void onComplete(List<MediaBean> albumList, List<MediaFolderBean> albumFolderList);
}
