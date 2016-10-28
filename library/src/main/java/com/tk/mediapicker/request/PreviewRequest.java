package com.tk.mediapicker.request;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tk.mediapicker.ui.activity.MediaPreActivity;
import com.tk.mediapicker.utils.MediaUtils;

import java.io.File;
import java.util.List;

import static com.tk.mediapicker.Constants.PreMediaConstants.INDEX;
import static com.tk.mediapicker.Constants.PreMediaConstants.REQUEST_DATA;

/**
 * Created by TK on 2016/10/28.
 * 预览请求
 */

public final class PreviewRequest extends Request {

    private PreviewRequest(Activity activity, Bundle bundle, int requestCode, List<File> sourceList) {
        this.mActivity = activity;
        this.mBundle = bundle;
        this.mRequestCode = requestCode;
        mBundle.putStringArrayList(REQUEST_DATA, MediaUtils.fileToPathList(sourceList));
    }


    @Override
    public Intent initIntent() {
        Intent intent = new Intent(mActivity, MediaPreActivity.class);
        intent.putExtras(mBundle);
        return intent;
    }

    public static Builder builder(Activity activity, int requestCode, List<File> sourceList) {
        return new Builder(activity, requestCode, sourceList);
    }

    public static final class Builder {
        private Bundle bundle;
        private Activity activity;
        private int requestCode;
        private List<File> sourceList;

        public Builder(Activity activity, int requestCode, List<File> sourceList) {
            bundle = new Bundle();
            this.activity = activity;
            this.requestCode = requestCode;
            this.sourceList = sourceList;
        }

        /**
         * 设置初始位置，默认0
         *
         * @return
         */
        public Builder setIndex(int index) {
            bundle.putInt(INDEX, index);
            return this;
        }


        public PreviewRequest build() {
            return new PreviewRequest(activity, bundle, requestCode, sourceList);
        }
    }
}
