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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tk.mediapicker.MediaPicker;
import com.tk.mediapicker.callback.Callback;
import com.tk.mediapicker.callback.CompressCallback;
import com.tk.mediapicker.request.AlbumRequest;
import com.tk.mediapicker.request.CameraRequest;
import com.tk.mediapicker.request.FileRequest;
import com.tk.mediapicker.request.PreviewRequest;
import com.tk.mediapicker.request.RECRequest;
import com.tk.mediapicker.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.checkbox1)
    CheckBox checkbox1;
    @BindView(R.id.checkbox2)
    CheckBox checkbox2;
    @BindView(R.id.iv_show)
    CircleImageView ivShow;
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
        MediaPicker.setThemeColor(0xffff6262);
        preAdapter = new NinePreAdapter(this, fileList);
        recyclerview.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerview.setHasFixedSize(true);
        recyclerview.setAdapter(preAdapter);
        preAdapter.setOnPreClickListener(new NinePreAdapter.OnPreClickListener() {
            @Override
            public void onPre(int position) {
                MediaPicker.startRequest(new PreviewRequest.Builder(MainActivity.this, 5, fileList)
                        .setIndex(position)
                        .build());

            }

            @Override
            public void onInsert(int other) {
                MediaPicker.startRequest(new AlbumRequest.Builder(MainActivity.this, 4)
                        .needCrop(false)
                        .asSingle(false)
                        .asSystem(checkbox2.isChecked())
                        .setCheckLimit(NinePreAdapter.MAX - fileList.size())
                        .showCameraIndex(true)
                        .showVideoContent(false)
                        .build());
            }
        });
    }

    @OnClick({R.id.iv_show, R.id.camera_and_crop, R.id.album, R.id.select_im, R.id.start_rec, R.id.start_file})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_show:
                MediaPicker.startRequest(new AlbumRequest.Builder(MainActivity.this, 1)
                        .needCrop(true)
                        .asSystem(checkbox2.isChecked())
                        .asSingle(true)
                        .showCameraIndex(true)
                        .showVideoContent(false)
                        .build());
                break;
            case R.id.camera_and_crop:
                MediaPicker.startRequest(new CameraRequest.Builder(MainActivity.this, 2)
                        .needCrop(false)
                        .build());
                break;
            case R.id.album:
                MediaPicker.startRequest(new AlbumRequest.Builder(MainActivity.this, 3)
                        .needCrop(true)
                        .asSystem(checkbox2.isChecked())
                        .asSingle(true)
                        .showCameraIndex(false)
                        .showVideoContent(false)
                        .build());
                break;
            case R.id.select_im:
                MediaPicker.startRequest(new AlbumRequest.Builder(MainActivity.this, 6)
                        .needCrop(false)
                        .asSystem(checkbox2.isChecked())
                        .asSingle(false)
                        .showCameraIndex(true)
                        .setCheckLimit(NinePreAdapter.MAX)
                        .showVideoContent(true)
                        .build());
                break;
            case R.id.start_rec:
                //开始录像
                MediaPicker.startRequest(new RECRequest.Builder(MainActivity.this, 7)
                        .build());
                break;
            case R.id.start_file:
                //寻找文件
                MediaPicker.startRequest(new FileRequest.Builder(MainActivity.this, 8)
                        .asSystem(checkbox2.isChecked())
                        .build());
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
                MediaPicker.onMediaResult(resultCode, data, checkbox1.isChecked() ?
                        new FriendCompressCallBack(this) : new FriendCallBack());
                break;
            case 5:
                //预览结果
                MediaPicker.onMediaResult(resultCode, data, new Callback() {
                    @Override
                    public void onComplete(File source) {
                    }

                    @Override
                    public void onComplete(List<File> sourceList) {
                        fileList.clear();
                        fileList.addAll(sourceList);
                        preAdapter.notifyDataSetChanged();
                    }
                });


                break;
            case 6:
                //IM
                break;
            case 7:
                MediaPicker.onMediaResult(resultCode, data, new Callback() {
                    @Override
                    public void onComplete(File source) {
                        videoSize.setText(FileUtils.getFileSize(source));
                        videoPath.setText(source.getAbsolutePath());
                    }

                    @Override
                    public void onComplete(List<File> sourceList) {
                    }
                });
                break;
            case 8:
                MediaPicker.onMediaResult(resultCode, data, new Callback() {
                    @Override
                    public void onComplete(File source) {
                        Toast.makeText(MainActivity.this, FileUtils.getFileSize(source), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete(List<File> sourceList) {
                    }
                });
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
            uploadSize.setText(FileUtils.getFileSize(source));
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
            uploadSize.setText(FileUtils.getFileSize(compressFile));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy", "onDestroy");
    }
}
