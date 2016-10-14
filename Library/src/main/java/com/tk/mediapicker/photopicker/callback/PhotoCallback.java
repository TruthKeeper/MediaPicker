package com.tk.mediapicker.photopicker.callback;

import java.io.File;
import java.util.List;

/**
 * Created by TK on 2016/9/30.
 */

public interface PhotoCallback {
    //不会同时回调
    void onComplete(File source);

    void onComplete(List<File> sourceList);
}
