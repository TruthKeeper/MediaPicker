package com.tk.mediapicker;

import android.app.Activity;
import android.content.Intent;

import com.tk.mediapicker.callback.Callback;
import com.tk.mediapicker.request.Request;
import com.tk.mediapicker.utils.MediaUtils;

import java.io.File;
import java.util.List;


/**
 * Created by TK on 2016/9/26.
 * MediaPicker
 */

public final class MediaPicker {
    public static final String TAG = "MediaPicker";

    /**
     * 发起请求
     *
     * @param request
     */
    public static void startRequest(Request request) {
        request.mActivity.startActivityForResult(request.initIntent(), request.mRequestCode);
    }


    /**
     * 接受回调，MediaPicker不关心requestCode(*^__^*)
     *
     * @param resultCode
     * @param data
     * @param callback
     */
    public static void onMediaResult(int resultCode, Intent data, Callback callback) {
        if (resultCode != Activity.RESULT_OK || callback == null || data == null) {
            return;
        }
        if (data.getBooleanExtra(Constants.RESULT_SINGLE, false)) {
            callback.onComplete(new File(data.getStringExtra(Constants.RESULT_DATA)));
        } else {
            List<String> pathList = data.getStringArrayListExtra(Constants.RESULT_DATA);
            callback.onComplete(MediaUtils.pathToFileList(pathList));
        }
    }


}
