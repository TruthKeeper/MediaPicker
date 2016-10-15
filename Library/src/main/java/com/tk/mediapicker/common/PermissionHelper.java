package com.tk.mediapicker.common;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TK on 2016/10/15.
 */

public class PermissionHelper {
    public static final int PERMISSION_REQUEST = 10000;
    public static final String[] PHOTO_PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public static final String[] VIDEO_PERMISSIONS = new String[]{
    };

    /**
     * 入口6.0以上获取权限
     *
     * @param activity
     * @param permissions
     * @return 1 成功 0 待获取  -1 失败
     */
    public static final int getPermission(Activity activity, String[] permissions) {
        if (permissions == null) {
            return -1;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //需要申请的
            List<String> needRequire = new ArrayList<String>();
            for (String s : permissions) {
                if (ContextCompat.checkSelfPermission(activity, s) != PackageManager.PERMISSION_GRANTED) {
                    //额外的拒绝集合
                    needRequire.add(s);
                }
            }
            if (needRequire.size() == 0) {
                return 1;
            }
            ActivityCompat.requestPermissions(activity, needRequire.toArray(new String[needRequire.size()]), PERMISSION_REQUEST);
            return 0;
        }
        //6.0- 无法校验╮(╯▽╰)╭
        return 1;
    }

    /**
     * 检查回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @param onPermissionListener
     */
    public static final void checkOnResult(int requestCode, String[] permissions, int[] grantResults, OnPermissionListener onPermissionListener) {
        if (requestCode == PERMISSION_REQUEST && onPermissionListener != null) {
            List<String> fList = new ArrayList<String>();
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    //用户不给权限
                    fList.add(permissions[i]);
                }
            }
            if (fList.size() == 0) {
                onPermissionListener.onSuccess();
            } else {
                onPermissionListener.onFailure(fList.toArray(new String[fList.size()]));
            }
        }
    }


    public interface OnPermissionListener {

        void onFailure(String[] failurePermissions);

        void onSuccess();
    }
}
