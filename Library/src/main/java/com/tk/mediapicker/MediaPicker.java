package com.tk.mediapicker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tk.mediapicker.bean.MediaBean;
import com.tk.mediapicker.callback.Callback;
import com.tk.mediapicker.ui.activity.AlbumActivity;
import com.tk.mediapicker.ui.activity.CameraResultActivity;
import com.tk.mediapicker.ui.activity.RECAResultActivity;
import com.tk.mediapicker.utils.ThemeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.tk.mediapicker.Constants.MediaPickerConstants.*;


/**
 * Created by TK on 2016/9/26.
 * MediaPicker
 */

public final class MediaPicker {
    public static final String TAG = "MediaPicker";

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Bundle bundle;

        public Builder() {
            bundle = new Bundle();
            bundle.putBoolean(SHOW_CAMERA, true);
            bundle.putBoolean(NEED_CROP, false);
            bundle.putBoolean(SHOW_VIDEO, true);
            bundle.putBoolean(IS_SINGLE, true);
            bundle.putInt(CHECK_LIMIT, 1);
        }

        /**
         * 相册首格是否显示拍照，默认false
         *
         * @param showCameraIndex
         * @return
         */
        public Builder showCameraIndex(boolean showCameraIndex) {
            bundle.putBoolean(SHOW_CAMERA, showCameraIndex);
            return this;
        }

        /**
         * 是否需要裁剪，默认false，仅限单选模式&&选中图片
         *
         * @return
         */
        public Builder needCrop() {
            bundle.putBoolean(NEED_CROP, true);
            return this;
        }

        /**
         * 设置相册中是否显示视频文件
         *
         * @param showVedioContent
         * @return
         */
        public Builder showVideoContent(boolean showVedioContent) {
            bundle.putBoolean(SHOW_VIDEO, showVedioContent);
            return this;
        }

        /**
         * 设置是否单选模式，默认true
         *
         * @param asSingle
         * @return
         */
        public Builder asSingle(boolean asSingle) {
            bundle.putBoolean(IS_SINGLE, asSingle);
            return this;
        }

        /**
         * 设置多选时选择数目限制，单选时无效
         *
         * @param limit
         * @return
         */
        public Builder setCheckLimit(int limit) {
            bundle.putInt(CHECK_LIMIT, limit);
            return this;
        }


        /**
         * 设置风格颜色，默认微信绿
         *
         * @return
         */
        public Builder setThemeColor(int themeColor) {
            ThemeUtils.themeColor = themeColor;
            return this;
        }

        /**
         * 开启相册
         *
         * @param activity
         * @param requestCode
         */
        public void startAlbum(Activity activity, int requestCode) {
            int limit = bundle.getInt(CHECK_LIMIT);
            if (limit < 1
                    || limit > DEFAULT_LIMIT
                    || (limit > 1 && bundle.getBoolean(NEED_CROP))) {
                //配置冲突
                throw new IllegalArgumentException("build failure,check your parameter");
            }
            Intent intent = new Intent(activity, AlbumActivity.class);
            intent.putExtras(bundle);
            activity.startActivityForResult(intent, requestCode);
        }


        /**
         * 开启拍照
         *
         * @param activity
         * @param requestCode
         */
        public void startCamera(Activity activity, int requestCode) {
            Intent intent = new Intent(activity, CameraResultActivity.class);
            intent.putExtras(bundle);
            activity.startActivityForResult(intent, requestCode);
        }

        /**
         * 开启录像
         *
         * @param activity
         * @param requestCode
         */
        public void startREC(Activity activity, int requestCode) {
            Intent intent = new Intent(activity, RECAResultActivity.class);
            intent.putExtras(bundle);
            activity.startActivityForResult(intent, requestCode);
        }

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

        if (data.getBooleanExtra(Constants.MediaPickerConstants.RESULT_SINGLE, false)) {
            callback.onComplete(new File(data.getStringExtra(RESULT_DATA)));
        } else {
            List<MediaBean> checkList = data.getParcelableArrayListExtra(RESULT_DATA);
            List<File> sourceList = new ArrayList<File>();
            for (int i = 0; i < checkList.size(); i++) {
                sourceList.add(new File(checkList.get(i).getPath()));
            }
            callback.onComplete(sourceList);
        }
    }


}
