package com.tk.mediapicker.request;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tk.mediapicker.ui.activity.CameraResultActivity;

import static com.tk.mediapicker.Constants.CameraRequestConstants.NEED_CROP;

/**
 * Created by TK on 2016/10/27.
 * 拍照请求
 */

public final class CameraRequest extends Request {
    private CameraRequest(Activity activity, Bundle bundle, int requestCode) {
        this.mActivity = activity;
        this.mBundle = bundle;
        this.mRequestCode = requestCode;
    }


    @Override
    public Intent initIntent() {
        Intent intent = new Intent(mActivity, CameraResultActivity.class);
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
         * 拍照是否需要裁剪，默认false
         *
         * @return
         */
        public Builder needCrop(boolean needCrop) {
            bundle.putBoolean(NEED_CROP, needCrop);
            return this;
        }

        /**
         * 拍照是否调用系统支持的拍照
         *
         * @return
         */
//        public Builder asSystem(boolean asSystem) {
//            bundle.putBoolean("System", asSystem);
//            return this;
//        }
        public CameraRequest build() {
            return new CameraRequest(activity, bundle, requestCode);
        }
    }
}
