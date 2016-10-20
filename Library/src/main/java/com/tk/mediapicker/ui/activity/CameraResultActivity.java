package com.tk.mediapicker.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.tk.mediapicker.R;
import com.tk.mediapicker.base.BaseActivity;
import com.tk.mediapicker.utils.PermissionHelper;
import com.tk.mediapicker.Constants;
import com.tk.mediapicker.utils.MediaUtils;
import com.tk.mediapicker.widget.ConfirmButton;

import java.io.File;
import java.io.IOException;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * Created by TK on 2016/10/14.
 */

public class CameraResultActivity extends BaseActivity {
    private File tempCameraFile;
    private File tempCropFile;

    private LinearLayout headerLayout;
    private ImageView back;
    private TextView title;
    private ConfirmButton confirmBtn;
    private PhotoView photoview;

    //记录相机是否开启
    private boolean hasStart;
    //记录拍照回调
    private boolean cameraResult;

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

    /**
     * 初始化
     */
    private void init() {

        if (!hasStart) {
            hasStart = true;
            startCamera();
        }
        if (cameraResult) {
            setContentView(R.layout.activity_camera_result);
            initView();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (tempCameraFile != null) {
            outState.putString("tempCameraFile", tempCameraFile.getAbsolutePath());
        }
        if (tempCropFile != null) {
            outState.putString("tempCropFile", tempCropFile.getAbsolutePath());
        }

        outState.putBoolean("hasStart", hasStart);
        outState.putBoolean("cameraResult", cameraResult);
    }

    /**
     * 恢复数据
     *
     * @param savedInstanceState
     */
    private void restroreData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }
        String tempCameraFileP = savedInstanceState.getString("tempCameraFile");
        String tempCropFileP = savedInstanceState.getString("tempCropFile");
        if (!TextUtils.isEmpty(tempCameraFileP)) {
            tempCameraFile = new File(tempCameraFileP);
        }
        if (!TextUtils.isEmpty(tempCropFileP)) {
            tempCropFile = new File(tempCropFileP);
        }

        hasStart = savedInstanceState.getBoolean("hasStart");
        cameraResult = savedInstanceState.getBoolean("cameraResult");
    }


    /**
     * 调用Android系统拍照
     */
    private void startCamera() {
        Log.e("startCamera", "startCamera");
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // 创建临时文件，并设置系统相机拍照后的输出路径
            try {
                tempCameraFile = MediaUtils.createCameraTmpFile(this);
                if (tempCameraFile != null && tempCameraFile.exists()) {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempCameraFile));
                    startActivityForResult(cameraIntent, Constants.CAMERA_REQUEST);
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
            Toast.makeText(getApplicationContext(), "您的手机不支持相机", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * 调用Android系统裁剪
     */
    private void startCrop() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(Uri.fromFile(tempCameraFile), "image/*");
        intent.putExtra("crop", "true");
        //默认正方形
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("return-data", true);
        File file = new File(Environment.getExternalStorageDirectory() + "/tempCrop/");
        if (!file.exists()) {
            file.mkdirs();
        }
        tempCropFile = new File(file,
                System.currentTimeMillis() + ".jpeg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(tempCropFile));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, Constants.CROP_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.CAMERA_REQUEST) {
            if (resultCode != Activity.RESULT_OK) {
                cameraResult = false;
                clearTemp();
                finish();
            } else {
                if (getIntent().getExtras().getBoolean(Constants.MediaPickerConstants.NEED_CROP)) {
                    //去裁剪
                    startCrop();
                } else {
                    //展示结果
                    cameraResult = true;
                    setContentView(R.layout.activity_camera_result);
                    initView();
                }
            }
        } else if (requestCode == Constants.CROP_REQUEST) {
            if (resultCode != Activity.RESULT_OK) {
                clearTemp();
                finish();
            } else {
                //将裁减结果回调给PhotoPicker
                Intent intent = new Intent();
                intent.putExtra(Constants.MediaPickerConstants.RESULT_SINGLE, true);
                intent.putExtra(Constants.MediaPickerConstants.RESULT_DATA, tempCropFile.getAbsolutePath());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    }

    /**
     * 延迟初始化界面元素
     */
    private void initView() {

        headerLayout = (LinearLayout) findViewById(R.id.header_layout);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        confirmBtn = (ConfirmButton) findViewById(R.id.confirm_btn);
        photoview = (PhotoView) findViewById(R.id.photoview);
        headerLayout.setBackgroundColor(0xBB000000);

        title.setText("1/1");
        confirmBtn.setEnabled(true);

        final PhotoViewAttacher attacher = new PhotoViewAttacher(photoview);
        Glide.with(this)
                .load(tempCameraFile)
                .asBitmap()
                .override(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels)
                .fitCenter()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        photoview.setImageBitmap(resource);
                        attacher.update();
                    }
                });
        confirmBtn.setOnClickListener(v -> {
            //点击完成将拍照结果回调给PhotoPicker
            Intent data = new Intent();
            data.putExtra(Constants.MediaPickerConstants.RESULT_SINGLE, true);
            data.putExtra(Constants.MediaPickerConstants.RESULT_DATA, tempCameraFile.getAbsolutePath());
            setResult(Activity.RESULT_OK, data);
            finish();
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearTemp();
                finish();
            }
        });
    }

    /**
     * 清除临时文件
     */
    private void clearTemp() {
        if (tempCameraFile != null) {
            if (tempCameraFile.exists()) {
                tempCameraFile.delete();
            }
        }
        if (tempCropFile != null) {
            if (tempCropFile.exists()) {
                tempCropFile.delete();
            }
        }
    }
}
