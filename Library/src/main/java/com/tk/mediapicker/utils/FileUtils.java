package com.tk.mediapicker.utils;

import android.text.TextUtils;

import java.io.File;
import java.math.BigDecimal;

/**
 * Created by TK on 2016/9/30.
 */

public final class FileUtils {

    /**
     * 文件是否存在
     *
     * @param path
     * @return
     */
    public static boolean exists(String path) {
        if (!TextUtils.isEmpty(path)) {
            return new File(path).exists();
        }
        return false;
    }

    /**
     * 是否视频文件
     *
     * @param fileName
     * @return
     */
    public static boolean isVideo(String fileName) {
        return !(fileName.endsWith(".jpeg") || fileName.endsWith(".png")
                || fileName.endsWith(".jpg") || fileName.endsWith(".gif")
                || fileName.endsWith(".bmp"));
    }

    /**
     * 读取图片大小
     *
     * @param file
     * @return
     */
    public static String getFileSize(File file) {
        return getFormatSize(file.length());
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
//            return size + "Byte";
            return "0K";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

}
