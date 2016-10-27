package com.tk.mediapicker.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.util.Pair;
import android.view.WindowManager;

import com.tk.mediapicker.R;
import com.tk.mediapicker.bean.MediaBean;
import com.tk.mediapicker.bean.MediaFolderBean;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.tk.mediapicker.utils.FileUtils.exists;

/**
 * Created by TK on 2016/9/26.
 * 相册通用工具类
 */

public final class AlbumUtils {
    private static final String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media._ID};
    private static final String[] VIDEO_PROJECTION = {
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media._ID};

    /**
     * Init Data
     *
     * @param context
     * @param needVideo
     * @return
     */
    public static Pair<List<MediaBean>, List<MediaFolderBean>> initData(Context context, boolean needVideo) {
        List<MediaBean> newMediaList = new ArrayList<MediaBean>();
        List<MediaFolderBean> newMediaFolderList = new ArrayList<MediaFolderBean>();
        if (needVideo) {
            Pair<List<MediaBean>, List<MediaFolderBean>> photoPair = getPhotoData(context);

            List<MediaBean> photoList = photoPair == null ? new ArrayList<MediaBean>() : photoPair.first;
            List<MediaFolderBean> photoFolderList = photoPair == null ? new ArrayList<MediaFolderBean>() : photoPair.second;
            List<MediaBean> videoList = getVideoData(context);

            newMediaList.addAll(photoList);
            newMediaList.addAll(videoList);
            newMediaFolderList = photoFolderList;

            //根据时间倒序
            Collections.sort(newMediaList);

            //有的话补上视频
            if (videoList.size() != 0) {
                MediaFolderBean videoBean = new MediaFolderBean(videoList.get(0).getPath(), context.getString(R.string.all_video));
                videoBean.setIndexVideo(true);
                videoBean.setMediaList(videoList);
                newMediaFolderList.add(0, videoBean);
            }
            //有数据的话补上所有
            if (newMediaList.size() != 0) {
                MediaFolderBean allBean = new MediaFolderBean(newMediaList.get(0).getPath(), context.getString(R.string.all_media));
                allBean.setIndexVideo(newMediaList.get(0).isVideo());
                newMediaFolderList.add(0, allBean);
            }
        } else {
            Pair<List<MediaBean>, List<MediaFolderBean>> photoPair = getPhotoData(context);

            List<MediaBean> photoList = photoPair == null ? new ArrayList<MediaBean>() : photoPair.first;
            List<MediaFolderBean> photoFolderList = photoPair == null ? new ArrayList<MediaFolderBean>() : photoPair.second;

            newMediaList.addAll(photoList);
            newMediaFolderList = photoFolderList;

            //有数据的话补上所有
            if (newMediaList.size() != 0) {
                MediaFolderBean allBean = new MediaFolderBean(newMediaList.get(0).getPath(), context.getString(R.string.all_photo));
                newMediaFolderList.add(0, allBean);
            }
        }
        return new Pair<>(newMediaList, newMediaFolderList);
    }


    /**
     * 得到图片数据
     *
     * @return
     */
    private static Pair<List<MediaBean>, List<MediaFolderBean>> getPhotoData(Context context) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                IMAGE_PROJECTION,
                IMAGE_PROJECTION[4] + ">0 AND " +
                        IMAGE_PROJECTION[3] + "=? OR " +
                        IMAGE_PROJECTION[3] + "=? OR " +
                        IMAGE_PROJECTION[3] + "=? OR " +
                        IMAGE_PROJECTION[3] + "=? OR " +
                        IMAGE_PROJECTION[3] + "=? ",
                new String[]{"image/jpeg", "image/png", "image/jpg", "image/gif", "image/bmp"},
                IMAGE_PROJECTION[2] + " DESC");

        List<MediaFolderBean> foldList = new ArrayList<MediaFolderBean>();
        List<MediaBean> albumList = new ArrayList<MediaBean>();
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        MediaBean mediaBean;
        MediaFolderBean folderBean;
        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
            long dateTime = cursor.getLong(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
            long size = cursor.getLong(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
            if (!FileUtils.exists(path)) {
                //过滤不存在的
                continue;
            }
            mediaBean = new MediaBean(path, name, dateTime, false, size);
            albumList.add(mediaBean);
            File parent = new File(path).getParentFile();
            int index = isFolderHas(foldList, parent.getName());
            if (index == -1) {
                //未存在则新建一个文件夹
                folderBean = new MediaFolderBean(path, parent.getName());
                folderBean.addBean(mediaBean);
                foldList.add(folderBean);
            } else {
                //已存在忘文件下insert
                foldList.get(index).addBean(mediaBean);
            }
        }
        cursor.close();
        return new Pair<>(albumList, foldList);
    }

    /**
     * 得到视频数据
     *
     * @return
     */
    private static List<MediaBean> getVideoData(Context context) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                VIDEO_PROJECTION,
                VIDEO_PROJECTION[4] + ">0",
                null,
                VIDEO_PROJECTION[2] + " DESC");

        List<MediaBean> videoList = new ArrayList<MediaBean>();
        if (cursor == null || cursor.getCount() == 0) {
            return videoList;
        }
        MediaBean mediaBean;
        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[0]));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[1]));
            long dateTime = cursor.getLong(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[2]));
            long size = cursor.getLong(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[4]));
            if (!exists(path)) {
                //过滤不存在的
                continue;
            }
            mediaBean = new MediaBean(path, name, dateTime, true, size);
            videoList.add(mediaBean);
        }
        cursor.close();
        return videoList;
    }


    /**
     * 文件夹列表中的存在位置
     *
     * @param foldList
     * @param floderName
     * @return
     */
    private static int isFolderHas(List<MediaFolderBean> foldList, String floderName) {
        int index = -1;
        for (int i = 0; i < foldList.size(); i++) {
            if (foldList.get(i).getFolderName().equals(floderName)) {
                index = i;
                return index;
            }
        }
        return index;
    }


    /**
     * 大图查看是的是否需要动态全屏
     *
     * @param activity
     * @param enable
     */
    public static void needFullScreen(Activity activity, boolean enable) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        if (enable) {
            lp.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        }
        activity.getWindow().setAttributes(lp);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
}
