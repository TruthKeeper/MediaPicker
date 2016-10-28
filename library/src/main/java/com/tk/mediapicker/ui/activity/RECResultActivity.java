package com.tk.mediapicker.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.tk.mediapicker.Constants;
import com.tk.mediapicker.R;
import com.tk.mediapicker.base.BaseActivity;
import com.tk.mediapicker.utils.MediaUtils;
import com.tk.mediapicker.utils.PermissionHelper;

import java.io.File;
import java.io.IOException;

import static com.tk.mediapicker.Constants.DEFAULT_REQUEST;


/**
 * Created by TK on 2016/10/14.
 */

public class RECResultActivity extends BaseActivity {
    private File tempFile;
    private boolean hasStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restroreData(savedInstanceState);
        //校验权限
        int result = PermissionHelper.getPermission(this, PermissionHelper.PHOTO_PERMISSIONS);
        if (result == -1) {
            finish();
        }
        if (result == 1) {
            init();
        }
    }

    private void restroreData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }
        String tempFileP = savedInstanceState.getString("tempFile");
        if (!TextUtils.isEmpty(tempFileP)) {
            tempFile = new File(tempFileP);
        }
        hasStart = savedInstanceState.getBoolean("hasStart", false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (tempFile != null) {
            outState.putString("tempFile", tempFile.getAbsolutePath());
        }
        outState.putBoolean("hasStart", hasStart);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.checkOnResult(requestCode, permissions, grantResults, new PermissionHelper.OnPermissionListener() {
            @Override
            public void onFailure(String[] failurePermissions) {
                Toast.makeText(getApplicationContext(), R.string.permission_camera_null, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onSuccess() {
                init();
            }
        });
    }

    private void init() {
        if (hasStart) {
            return;
        }
        Log.e("startREC", "startREC");
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // 创建临时文件，并设置系统相机拍照后的输出路径
            try {
                tempFile = MediaUtils.createVideoTmpFile(this);
                if (tempFile != null && tempFile.exists()) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                    startActivityForResult(intent, DEFAULT_REQUEST);
                    hasStart = true;
                } else {
                    Toast.makeText(getApplicationContext(), "创建缓存文件失败", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "创建缓存文件失败", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "您的手机不支持录像", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.DEFAULT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Intent intent = new Intent();
                intent.putExtra(Constants.RESULT_SINGLE, true);
                intent.putExtra(Constants.RESULT_DATA, tempFile.getAbsolutePath());
                setResult(Activity.RESULT_OK, intent);
            } else {
                clearTemp();
            }
            finish();
        }
    }

    /**
     * 清除临时文件
     */
    private void clearTemp() {
        if (tempFile != null) {
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}
