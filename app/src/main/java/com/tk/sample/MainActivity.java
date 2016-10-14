package com.tk.sample;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tk.mediapicker.photopicker.PhotoPicker;
import com.tk.mediapicker.photopicker.callback.CompressCallback;
import com.tk.mediapicker.photopicker.callback.PhotoCallback;
import com.tk.mediapicker.photopicker.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.checkbox1)
    CheckBox checkbox1;
    @BindView(R.id.iv_show)
    ImageView ivShow;
    @BindView(R.id.upload_size)
    TextView uploadSize;
    @BindView(R.id.upload_path)
    TextView uploadPath;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.checkbox2)
    CheckBox checkbox2;
    private NinePreAdapter preAdapter;
    private List<File> fileList = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("加载中");

        preAdapter = new NinePreAdapter(this, fileList);
        recyclerview.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerview.setHasFixedSize(true);
        recyclerview.setAdapter(preAdapter);
        preAdapter.setOnPreClickListener(new NinePreAdapter.OnPreClickListener() {
            @Override
            public void onPre(int position) {
                fileList.remove(position);
                preAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onInsert(int other) {
                PhotoPicker.builder()
                        .asSingle(false)
                        .setPhotoCheckLimit(NinePreAdapter.MAX - fileList.size())
                        .showCamera(true)
                        .checkAndStart(MainActivity.this, 4);
            }
        });
    }

    @OnClick({R.id.iv_show, R.id.camera_and_crop, R.id.album})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_show:
                PhotoPicker.builder()
                        .asSingle(true)
                        .showCamera(true)
                        .checkAndStart(this, 1);
                break;
            case R.id.camera_and_crop:
                PhotoPicker.builder()
                        .takePhotoNow()
                        .needCrop()
                        .checkAndStart(this, 2);
                break;
            case R.id.album:
                PhotoPicker.builder()
                        .asSingle(true)
                        .showCamera(false)
                        .needCrop()
                        .checkAndStart(this, 3);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                PhotoPicker.onActivityResult(resultCode, data, checkbox1.isChecked() ? new AlbumCompressCallBack(this) : new AlbumCallBack());
                break;
            case 2:
                PhotoPicker.onActivityResult(resultCode, data, checkbox1.isChecked() ? new AlbumCompressCallBack(this) : new AlbumCallBack());
                break;
            case 3:
                PhotoPicker.onActivityResult(resultCode, data, checkbox1.isChecked() ? new AlbumCompressCallBack(this) : new AlbumCallBack());
                break;
            case 4:
                PhotoPicker.onActivityResult(resultCode, data, checkbox1.isChecked() ? new FriendCompressCallBack(this) : new FriendCallBack());
                break;
        }

    }

    private class AlbumCallBack implements PhotoCallback {
        @Override
        public void onComplete(File source) {
            int s = getResources().getDimensionPixelOffset(R.dimen.normal_avatar);
            Glide.with(MainActivity.this)
                    .load(source)
                    .asBitmap()
                    .override(s, s)
                    .centerCrop()
                    .into(ivShow);
            uploadSize.setText(FileUtils.getImaSize(source));
            uploadPath.setText(source.getAbsolutePath());
        }

        @Override
        public void onComplete(List<File> sourceList) {
        }
    }

    private class AlbumCompressCallBack extends CompressCallback {

        public AlbumCompressCallBack(Context mContext) {
            super(mContext);
        }

        @Override
        protected void onFailure(Throwable throwable) {

        }

        @Override
        protected void onSuccess(File compressFile) {
            int s = getResources().getDimensionPixelOffset(R.dimen.normal_avatar);
            Glide.with(MainActivity.this)
                    .load(compressFile)
                    .asBitmap()
                    .override(s, s)
                    .centerCrop()
                    .into(ivShow);
            uploadSize.setText(FileUtils.getImaSize(compressFile));
            uploadPath.setText(compressFile.getAbsolutePath());
        }

        @Override
        protected void onSuccess(List<File> compressFileList) {
        }

        @Override
        protected void onStart() {
            progressDialog.show();
        }

        @Override
        protected void onFinish() {
            progressDialog.dismiss();
        }
    }

    private class FriendCallBack implements PhotoCallback {
        @Override
        public void onComplete(File source) {
            fileList.add(source);
            preAdapter.notifyDataSetChanged();
        }

        @Override
        public void onComplete(List<File> sourceList) {
            fileList.addAll(sourceList);
            preAdapter.notifyDataSetChanged();
        }
    }

    private class FriendCompressCallBack extends CompressCallback {

        public FriendCompressCallBack(Context mContext) {
            super(mContext);
        }

        @Override
        protected void onFailure(Throwable throwable) {

        }

        @Override
        protected void onSuccess(File compressFile) {
            fileList.add(compressFile);
            preAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onSuccess(List<File> compressFileList) {
            fileList.addAll(compressFileList);
            preAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onStart() {
            progressDialog.show();
        }

        @Override
        protected void onFinish() {
            progressDialog.dismiss();
        }
    }

}
