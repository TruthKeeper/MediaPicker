package com.tk.sample;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tk.mediapicker.MediaPicker;
import com.tk.mediapicker.MediaPreviewer;
import com.tk.mediapicker.callback.Callback;
import com.tk.mediapicker.callback.CompressCallback;
import com.tk.mediapicker.utils.AlbumUtils;
import com.tk.mediapicker.utils.FileUtils;

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
    @BindView(R.id.video_size)
    TextView videoSize;
    @BindView(R.id.video_path)
    TextView videoPath;
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
                MediaPreviewer.prePhotos(MainActivity.this, 6, fileList, position);
            }

            @Override
            public void onInsert(int other) {
                MediaPicker.builder()
                        .asSingle(false)
                        .setCheckLimit(NinePreAdapter.MAX - fileList.size())
                        .showCameraIndex(true)
                        .showVideoContent(false)
                        .startAlbum(MainActivity.this, 7);
            }
        });
    }

    @OnClick({R.id.iv_show, R.id.camera_and_crop, R.id.album, R.id.select_im, R.id.start_rec})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_show:
                MediaPicker.builder()
                        .asSingle(true)
                        .showVideoContent(false)
                        .showCameraIndex(true)
                        .startAlbum(this, 1);
                break;
            case R.id.camera_and_crop:
                MediaPicker.builder()
                        .needCrop()
                        .startCamera(this, 2);
                break;
            case R.id.album:
                MediaPicker.builder()
                        .asSingle(true)
                        .showVideoContent(false)
                        .showCameraIndex(false)
                        .needCrop()
                        .startAlbum(this, 3);
                break;
            case R.id.select_im:
                MediaPicker.builder()
                        .asSingle(false)
                        .showVideoContent(true)
                        .showCameraIndex(false)
                        .setCheckLimit(3)
                        .startAlbum(this, 4);
                break;
            case R.id.start_rec:
                //开始录像
                MediaPicker.builder()
                        .startREC(this, 5);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                MediaPicker.onMediaResult(resultCode, data, checkbox1.isChecked() ?
                        new AlbumCompressCallBack(this) : new AlbumCallBack());
                break;
            case 2:
                MediaPicker.onMediaResult(resultCode, data, checkbox1.isChecked() ?
                        new AlbumCompressCallBack(this) : new AlbumCallBack());
                break;
            case 3:
                MediaPicker.onMediaResult(resultCode, data, checkbox1.isChecked() ?
                        new AlbumCompressCallBack(this) : new AlbumCallBack());
                break;
            case 4:
                //仅仅看看
                break;
            case 5:
                //录像
                MediaPicker.onMediaResult(resultCode, data, new Callback() {
                    @Override
                    public void onComplete(File source) {
                        Log.e("onComplete", source.getAbsolutePath() + "\n" + AlbumUtils.getFormatSize(source.length()));
                    }

                    @Override
                    public void onComplete(List<File> sourceList) {

                    }
                });
                break;
            case 6:
                List<File> tempList = MediaPreviewer.onActivityResult(resultCode, data);
                if (tempList != null) {
                    fileList.clear();
                    fileList.addAll(tempList);
                    preAdapter.notifyDataSetChanged();
                }
                break;
            case 7:
                MediaPicker.onMediaResult(resultCode, data, checkbox1.isChecked() ?
                        new FriendCompressCallBack(this) : new FriendCallBack());
                break;
        }

    }


    private class AlbumCallBack implements Callback {
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

    private class FriendCallBack implements Callback {
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
