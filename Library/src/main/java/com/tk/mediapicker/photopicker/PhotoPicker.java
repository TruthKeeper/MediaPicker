package com.tk.mediapicker.photopicker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tk.mediapicker.photopicker.bean.AlbumBean;
import com.tk.mediapicker.photopicker.callback.PhotoCallback;
import com.tk.mediapicker.photopicker.ui.activity.AlbumActivity;
import com.tk.mediapicker.photopicker.ui.activity.CameraResultActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.tk.mediapicker.photopicker.Constants.PhotoPickConstants.CHECK_LIMIT;
import static com.tk.mediapicker.photopicker.Constants.PhotoPickConstants.DEFAULT_LIMIT;
import static com.tk.mediapicker.photopicker.Constants.PhotoPickConstants.IS_SINGLE;
import static com.tk.mediapicker.photopicker.Constants.PhotoPickConstants.NEED_CROP;
import static com.tk.mediapicker.photopicker.Constants.PhotoPickConstants.RESULT_DATA;
import static com.tk.mediapicker.photopicker.Constants.PhotoPickConstants.SHOW_CAMERA;
import static com.tk.mediapicker.photopicker.Constants.PhotoPickConstants.START_CAMERA;


/**
 * Created by TK on 2016/9/26.
 * 图片选择
 */

public final class PhotoPicker {
    public static final String TAG = "PhotoPicker";
    public static int themeColor = 0xFF45C01A;

    public static final Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Bundle bundle;

        public Builder() {
            bundle = new Bundle();
            bundle.putBoolean(START_CAMERA, false);
            bundle.putBoolean(NEED_CROP, false);
            bundle.putBoolean(SHOW_CAMERA, false);
            bundle.putBoolean(IS_SINGLE, true);
            bundle.putInt(CHECK_LIMIT, 1);
        }

        /**
         * 拍照模式，默认相册模式,开启后只关注是否需要裁剪
         *
         * @return
         */
        public Builder takePhotoNow() {
            bundle.putBoolean(START_CAMERA, true);
            return this;
        }

        /**
         * 相册首格是否显示拍照，默认false
         *
         * @return
         */
        public Builder showCamera(boolean show) {
            bundle.putBoolean(SHOW_CAMERA, show);
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
        public Builder setPhotoCheckLimit(int limit) {
            bundle.putInt(CHECK_LIMIT, limit);
            return this;
        }

        /**
         * 是否需要裁剪，默认false，仅限单选模式有用
         *
         * @return
         */
        public Builder needCrop() {
            bundle.putBoolean(NEED_CROP, true);
            return this;
        }


        /**
         * 设置风格颜色，默认微信绿
         *
         * @return
         */
        public Builder setThemeColor(int themeColor) {
            PhotoPicker.themeColor = themeColor;
            return this;
        }

        /**
         * 开启，走你
         *
         * @param activity
         * @return true 可添加动画切换效果
         */
        public boolean checkAndStart(Activity activity, int requestCode) {
            Intent intent = null;
            if (bundle.getBoolean(START_CAMERA)) {
                //拍照
                intent = new Intent(activity, CameraResultActivity.class);
                intent.putExtras(bundle);
                activity.startActivityForResult(intent, requestCode);
                return true;
            }
            if (bundle.getBoolean(IS_SINGLE)) {
                //单选
                intent = new Intent(activity, AlbumActivity.class);
                intent.putExtras(bundle);
                activity.startActivityForResult(intent, requestCode);
                return true;
            }
            int limit = bundle.getInt(CHECK_LIMIT);
            if (limit < 1
                    || limit > DEFAULT_LIMIT
                    || (limit > 1 && bundle.getBoolean(NEED_CROP))
                    || activity == null) {
                //配置错误
                Log.e(TAG, "build failure,check your parameter");
                return false;
            }
            intent = new Intent(activity, AlbumActivity.class);
            intent.putExtras(bundle);
            activity.startActivityForResult(intent, requestCode);
            return true;
        }
    }

    /**
     * 接受回调，PhotoPicker不关心requestCode(*^__^*)
     *
     * @param resultCode
     * @param data
     * @param callback
     */
    public static final void onActivityResult(int resultCode, Intent data, PhotoCallback callback) {
        if (resultCode != Activity.RESULT_OK || callback == null) {
            return;
        }
        if (data == null) {
            return;
        }
        if (data.getBooleanExtra(Constants.PhotoPickConstants.RESULT_SINGLE, false)) {
            callback.onComplete(new File(data.getStringExtra(RESULT_DATA)));
        } else {
            List<AlbumBean> checkList = data.getParcelableArrayListExtra(RESULT_DATA);
            List<File> sourceList = new ArrayList<File>();
            for (int i = 0; i < checkList.size(); i++) {
                sourceList.add(new File(checkList.get(i).getPath()));
            }
            callback.onComplete(sourceList);
        }
    }


}
