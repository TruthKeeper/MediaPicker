package com.tk.mediapicker.request;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by TK on 2016/10/27.
 */

public abstract class Request {
    public Activity mActivity;
    public Bundle mBundle = new Bundle();
    public int mRequestCode;

    public abstract Intent initIntent();


}
