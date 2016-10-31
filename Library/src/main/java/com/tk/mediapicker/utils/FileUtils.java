package com.tk.mediapicker.utils;

import android.content.Context;
import android.os.storage.StorageManager;
import android.text.TextUtils;

import com.tk.mediapicker.bean.FileBean;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    /**
     * 获得某个路径下的文件夹列表
     *
     * @param dir
     * @return
     */
    public static List<FileBean> getDataList(File dir) {
        List<FileBean> list = new ArrayList<FileBean>();
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return list;
        }

        FileBean bean;
        for (File file : files) {
            bean = new FileBean(file.getName(), file.getAbsolutePath(), file.isFile());
            list.add(bean);
        }
        Collections.sort(list);
        return list;
    }

    /**
     * 初始化文件夹列表
     *
     * @param context
     * @return
     */
    public static List<FileBean> initFileDataList(Context context) {
        String sdPath = getStoragePath(context, true);
        String innerPath = getStoragePath(context, false);
        if (!hasSd(context)) {
            return getDataList(new File(innerPath));
        }
        List<FileBean> list = new ArrayList<FileBean>();
        FileBean bean = new FileBean("内部存储", innerPath, false);
        list.add(bean);
        bean = new FileBean("SD卡", sdPath, false);
        list.add(bean);
        return list;
    }

    /**
     * 是否存在SD卡
     *
     * @param context
     * @return
     */
    public static boolean hasSd(Context context) {
        String sdPath = getStoragePath(context, true);
        if (sdPath == null) {
            return false;
        } else {
            File sdFile = new File(sdPath);
            if (!sdFile.exists() || sdFile.length() == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 得到存储路径
     *
     * @param context
     * @param getSD
     * @return
     */
    public static String getStoragePath(Context context, boolean getSD) {
        StorageManager mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (getSD == removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取文件类型
     *
     * @param fileName
     * @return
     */
    public static String getMIMEType(String fileName) {
        String type = "";
        String end = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length())
                .toLowerCase();
        if (end.equals("apk")) {
            return "application/vnd.android.package-archive";
        } else if (end.equals("mp4") || end.equals("avi") || end.equals("3gp")
                || end.equals("rmvb")) {
            type = "video";
        } else if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
                || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            type = "audio";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
                || end.equals("jpeg") || end.equals("bmp")) {
            type = "image";
        } else if (end.equals("txt") || end.equals("log")) {
            type = "text";
        } else {
            type = "*";
        }
        type += "/*";
        return type;
    }
}
