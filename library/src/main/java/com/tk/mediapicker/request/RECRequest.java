package com.tk.mediapicker.request;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tk.mediapicker.ui.activity.RECResultActivity;

/**
 * Created by TK on 2016/10/27.
 * 录像请求
 */

public final class RECRequest extends Request {
    private RECRequest(Activity activity, Bundle bundle, int requestCode) {
        this.mActivity = activity;
        this.mBundle = bundle;
        this.mRequestCode = requestCode;
    }


    @Override
    public Intent initIntent() {
        Intent intent = new Intent(mActivity, RECResultActivity.class);
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
         * 录像是否调用系统支持的录像
         *
         * @return
         */
//        public Builder asSystem(boolean asSystem) {
//            bundle.putBoolean("System", asSystem);
//            return this;
//        }
        public RECRequest build() {
            return new RECRequest(activity, bundle, requestCode);
        }
    }
}
