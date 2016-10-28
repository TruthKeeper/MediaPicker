package com.tk.mediapicker.request;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tk.mediapicker.ui.activity.AlbumActivity;

import static com.tk.mediapicker.Constants.AS_SYSTEM;
import static com.tk.mediapicker.Constants.AlbumRequestConstants.*;

/**
 * Created by TK on 2016/10/28.
 * 相册请求
 */

public final class AlbumRequest extends Request {
    private AlbumRequest(Activity activity, Bundle bundle, int requestCode) {
        this.mActivity = activity;
        this.mBundle = bundle;
        this.mRequestCode = requestCode;
    }


    @Override
    public Intent initIntent() {
        Intent intent = new Intent(mActivity, AlbumActivity.class);
//        if (mBundle.getBoolean("System", false)) {
//        }
        intent.putExtras(mBundle);
        return intent;
    }

    public static Builder builder(Activity activity, int requestCode) {
        return new Builder(activity, requestCode);
    }

    public static final class Builder {
        private Bundle bundle;
        private Activity activity;
        private int requestCode;

        public Builder(Activity activity, int requestCode) {
            bundle = new Bundle();
            this.activity = activity;
            this.requestCode = requestCode;
        }

        /**
         * 相册是否单选，默认单选
         *
         * @return
         */
        public Builder asSingle(boolean asSingle) {
            bundle.putBoolean(AS_SINGLE, asSingle);
            return this;
        }

        /**
         * 选择数量限制
         *
         * @return
         */
        public Builder setCheckLimit(int checkLimit) {
            if (checkLimit <= 1) {
                return asSingle(true);
            }
            bundle.putInt(CHECK_LIMIT, checkLimit);
            return this;
        }

        /**
         * 相册中是否显示视频文件，默认不显示
         *
         * @return
         */
        public Builder showVideoContent(boolean showVideoContent) {
            bundle.putBoolean(SHOW_VIDEO, showVideoContent);
            return this;
        }

        /**
         * 首格是否显示拍照，默认不显示
         *
         * @return
         */
        public Builder showCameraIndex(boolean showCameraIndex) {
            bundle.putBoolean(SHOW_CAMERA, showCameraIndex);
            return this;
        }

        /**
         * 拍照是否需要裁剪，默认false
         *
         * @return
         */
        public Builder needCrop(boolean needCrop) {
            bundle.putBoolean(NEED_CROP, needCrop);
            return this;
        }

        /**
         * 是否调用系统支持的相册,默认false
         * 系统限制单选
         *
         * @return
         */
        public Builder asSystem(boolean asSystem) {
            bundle.putBoolean(AS_SYSTEM, asSystem);
            return this;
        }

        public AlbumRequest build() {
            return new AlbumRequest(activity, bundle, requestCode);
        }
    }
}
