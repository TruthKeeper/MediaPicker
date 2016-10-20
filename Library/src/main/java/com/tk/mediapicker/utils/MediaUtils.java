package com.tk.mediapicker.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;


/**
 * Created by TK on 2016/9/30.
 */
public final class MediaUtils {

    private static final String IMG_PREFIX = "IMG_";
    private static final String IMG_SUFFIX = ".jpeg";

    private static final String VIDEO_PREFIX = "VIDEO_";
    private static final String VIDEO_SUFFIX = ".mp4";

    /**
     * 创建临时文件
     *
     * @param context
     * @return
     * @throws IOException
     */
    public static final File createCameraTmpFile(Context context) throws IOException {
        File dir = null;
        if (TextUtils.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED)) {
            dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Camera/");
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } else {
            dir = new File(Environment.getExternalStorageDirectory() + "/tempCamera/");
        }
        return File.createTempFile(IMG_PREFIX, IMG_SUFFIX, dir);
    }
    /**
     * 创建临时文件
     *
     * @param context
     * @return
     * @throws IOException
     */
    public static final File createVideoTmpFile(Context context) throws IOException {
        File dir = null;
        if (TextUtils.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED)) {
            dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Camera/");
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } else {
            dir = new File(Environment.getExternalStorageDirectory() + "/tempCamera/");
        }
        return File.createTempFile(VIDEO_PREFIX, VIDEO_SUFFIX, dir);
    }

}
