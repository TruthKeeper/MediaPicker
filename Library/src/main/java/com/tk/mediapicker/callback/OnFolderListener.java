package com.tk.mediapicker.callback;


import com.tk.mediapicker.bean.MediaFolderBean;

import java.util.List;

/**
 * Created by TK on 2016/9/27.
 * 文件夹数据回调
 */

public interface OnFolderListener {

    void onFolderComplete(List<MediaFolderBean> albumFolderList);
}
