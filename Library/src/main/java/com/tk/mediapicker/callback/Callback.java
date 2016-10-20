package com.tk.mediapicker.callback;

import java.io.File;
import java.util.List;

/**
 * Created by TK on 2016/9/30.
 */

public interface Callback {
    //不会同时回调
    void onComplete(File source);

    void onComplete(List<File> sourceList);
}
