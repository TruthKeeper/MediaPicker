package com.tk.mediapicker.photopicker.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;


/**
 * Created by TK on 2016/9/30.
 */
public final class CameraUtils {

    private static final String FILE_PREFIX = "IMG_";
    private static final String FILE_SUFFIX = ".jpeg";

    /**
     * 创建临时文件
     *
     * @param context
     * @return
     * @throws IOException
     */
    public static final File createTmpFile(Context context) throws IOException {
        File dir = null;
        if (TextUtils.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED)) {
            dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Camera/");
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } else {
            dir = new File(Environment.getExternalStorageDirectory() + "/tempCamera/");
        }
        return File.createTempFile(FILE_PREFIX, FILE_SUFFIX, dir);
    }

}
