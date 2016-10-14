package com.tk.mediapicker.photopicker.utils;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.WindowManager;

import com.tk.mediapicker.photopicker.bean.AlbumBean;
import com.tk.mediapicker.photopicker.bean.AlbumFolderBean;
import com.tk.mediapicker.photopicker.callback.OnLoadAlbumListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TK on 2016/9/26.
 * 相册通用工具类
 */

public final class AlbumUtils {
    /**
     * 初始化数据源
     *
     * @param activity
     * @param onLoadAlbumListener
     */
    public static final void initAlbumData(FragmentActivity activity, OnLoadAlbumListener onLoadAlbumListener) {
        activity.getSupportLoaderManager().initLoader(0, null, new AlbumLoaderCallback(activity, onLoadAlbumListener));
    }

    public static final class AlbumLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        private static final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media._ID};
        private OnLoadAlbumListener onLoadAlbumListener;
        private FragmentActivity activity;

        public AlbumLoaderCallback(FragmentActivity activity, OnLoadAlbumListener onLoadAlbumListener) {
            this.activity = activity;
            this.onLoadAlbumListener = onLoadAlbumListener;
        }

        @Override
        public final Loader<Cursor> onCreateLoader(int id, Bundle args) {
            CursorLoader cursorLoader = new CursorLoader(activity,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    IMAGE_PROJECTION,
                    IMAGE_PROJECTION[4] + ">0 AND " +
                            IMAGE_PROJECTION[3] + "=? OR " +
                            IMAGE_PROJECTION[3] + "=? OR " +
                            IMAGE_PROJECTION[3] + "=? OR " +
                            IMAGE_PROJECTION[3] + "=? ",
                    new String[]{"image/jpeg", "image/png", "image/jpg", "image/gif"},
                    IMAGE_PROJECTION[2] + " DESC");
            return cursorLoader;
        }

        @Override
        public final void onLoadFinished(Loader loader, Cursor data) {
            List<AlbumFolderBean> foldList = new ArrayList<AlbumFolderBean>();
            List<AlbumBean> albumList = new ArrayList<AlbumBean>();
            if (data == null || data.getCount() == 0) {
                if (onLoadAlbumListener != null) {
                    onLoadAlbumListener.onComplete(albumList, foldList);
                }
                activity.getSupportLoaderManager().destroyLoader(0);
                return;
            }
            AlbumBean albumBean;
            AlbumFolderBean folderBean;
            while (data.moveToNext()) {
                String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                if (!exists(path)) {
                    //过滤不存在的
                    continue;
                }
                albumBean = new AlbumBean(path, name, dateTime);
                albumList.add(albumBean);
                File parent = new File(path).getParentFile();
                int index = isFolderHas(foldList, parent.getAbsolutePath());
                if (index == -1) {
                    //未存在则新建一个文件夹
                    folderBean = new AlbumFolderBean(path, parent.getAbsolutePath(), parent.getName());
                    folderBean.addBean(albumBean);
                    foldList.add(folderBean);
                } else {
                    //已存在忘文件下insert
                    foldList.get(index).addBean(albumBean);
                }
            }
            //最后补上所有文件
            AlbumFolderBean indexFolderBean = new AlbumFolderBean(albumList.get(0).getPath(),
                    "/all/",
                    "所有图片");
            indexFolderBean.addBeanList(albumList);
            foldList.add(0, indexFolderBean);
            if (onLoadAlbumListener != null) {
                onLoadAlbumListener.onComplete(albumList, foldList);
            }
            activity.getSupportLoaderManager().destroyLoader(0);
        }

        @Override
        public void onLoaderReset(Loader loader) {

        }
    }

    /**
     * 文件是否存在
     *
     * @param path
     * @return
     */
    private static final boolean exists(String path) {
        if (!TextUtils.isEmpty(path)) {
            return new File(path).exists();
        }
        return false;
    }

    /**
     * 文件夹列表中的存在位置
     *
     * @param foldList
     * @param path
     * @return
     */
    private static final int isFolderHas(List<AlbumFolderBean> foldList, String path) {
        int index = -1;
        for (int i = 0; i < foldList.size(); i++) {
            if (foldList.get(i).getFolderPath().equals(path)) {
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
    public static final void needFullScreen(Activity activity, boolean enable) {
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
