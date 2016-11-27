package com.tk.mediapicker.ui.activity;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tk.mediapicker.Constants;
import com.tk.mediapicker.MediaPicker;
import com.tk.mediapicker.R;
import com.tk.mediapicker.base.BaseActivity;
import com.tk.mediapicker.bean.MediaBean;
import com.tk.mediapicker.bean.MediaFolderBean;
import com.tk.mediapicker.callback.OnFolderListener;
import com.tk.mediapicker.request.CameraRequest;
import com.tk.mediapicker.ui.adapter.AlbumAdapter;
import com.tk.mediapicker.ui.adapter.FolderAdapter;
import com.tk.mediapicker.ui.fragment.AlbumFragment;
import com.tk.mediapicker.utils.DocumentUtils;
import com.tk.mediapicker.utils.FolderUtils;
import com.tk.mediapicker.utils.MediaUtils;
import com.tk.mediapicker.utils.PermissionHelper;
import com.tk.mediapicker.widget.ConfirmButton;
import com.tk.mediapicker.widget.FolderItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by TK on 2016/8/10.
 */
public class AlbumActivity extends BaseActivity implements OnFolderListener,
        FolderAdapter.onFolderClickListener, AlbumAdapter.OnAlbumSelectListener, View.OnClickListener {


    private ImageView back;
    private ConfirmButton confirmBtn;
    private RecyclerView folderRecyclerview;
    private TextView folderText;
    private TextView previewText;
    private LinearLayout folderLayout;
    private LinearLayout previewLayout;
    private View shadow;
    private LinearLayout shadowLayout;

    private AlbumFragment albumFragment;
    private FolderAdapter folderAdapter;
    //文件夹list
    private List<MediaFolderBean> mediaFolderList = new ArrayList<MediaFolderBean>();

    private ValueAnimator showAnim;
    private ValueAnimator dismiss;
    private ArgbEvaluator shadowArgb;
    private boolean animLock;
    private boolean shadowFlag;
    private Bundle bundle;
    private Handler handler = new Handler();
    private File tempCropFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getIntent().getExtras();
        if (bundle.getBoolean(Constants.AS_SYSTEM, false)) {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, Constants.DEFAULT_REQUEST);
            return;
        }
        setContentView(R.layout.activity_album);
        initViews();
        initConstants();
        initAnim();
        initFragment();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.checkOnResult(requestCode, permissions, grantResults, new PermissionHelper.OnPermissionListener() {
            @Override
            public void onFailure(String[] failurePermissions) {
                Toast.makeText(getApplicationContext(), R.string.permission_photo_null, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onSuccess() {
                albumFragment.setHasPermission(true);
                albumFragment.initData();
            }
        });
    }

    /**
     * 初始化碎片
     */
    private void initFragment() {
        //校验权限then初始化数据源
        int result = PermissionHelper.getPermission(this, PermissionHelper.PHOTO_PERMISSIONS);
        if (result == -1) {
            finish();
        }
        albumFragment = new AlbumFragment();
        if (result == 1) {
            albumFragment.setHasPermission(true);
        }
        //继续传递
        albumFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.main_album, albumFragment).commit();
        albumFragment.setOnFolderListener(this);
        albumFragment.setOnAlbumSelectListener(this);

    }


    /**
     * 初始化views
     */
    private void initViews() {
        back = (ImageView) findViewById(R.id.back);
        confirmBtn = (ConfirmButton) findViewById(R.id.confirm_btn);
        folderRecyclerview = (RecyclerView) findViewById(R.id.folder_recyclerview);
        folderText = (TextView) findViewById(R.id.folder_text);

        previewText = (TextView) findViewById(R.id.preview_text);
        folderLayout = (LinearLayout) findViewById(R.id.folder_layout);
        previewLayout = (LinearLayout) findViewById(R.id.preview_layout);
        shadow = findViewById(R.id.shadow);
        shadowLayout = (LinearLayout) findViewById(R.id.shadow_layout);

        back.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        folderLayout.setOnClickListener(this);
        previewLayout.setOnClickListener(this);
        shadow.setOnClickListener(this);
    }

    /**
     * 接收并处理配置
     */
    private void initConstants() {
        if (bundle.getBoolean(Constants.AlbumRequestConstants.AS_SINGLE, true)) {
            //单选模式
            previewLayout.setVisibility(View.GONE);
            confirmBtn.setVisibility(View.GONE);
        }
        //刷新底部
        if (bundle.getBoolean(Constants.AlbumRequestConstants.SHOW_VIDEO, false)) {
            folderText.setText(R.string.all_media);
        } else {
            folderText.setText(R.string.all_photo);
        }
    }

    public void onClick(View view) {
        if (view.getId() == R.id.back) {
            finish();
        } else if (view.getId() == R.id.confirm_btn) {
            //完成选择，回调
            Intent intent = new Intent();
            List<MediaBean> checkList = albumFragment.getSelectList();
            if (checkList.size() == 1) {
                intent.putExtra(Constants.RESULT_SINGLE, true);
                intent.putExtra(Constants.RESULT_DATA, checkList.get(0).getPath());
            } else {
                intent.putExtra(Constants.RESULT_SINGLE, false);
                intent.putStringArrayListExtra(Constants.RESULT_DATA, MediaUtils.beanToPathList(checkList));
            }
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else if (view.getId() == R.id.folder_layout) {
            //文件夹
            if (animLock || mediaFolderList.size() <= 1) {
                return;
            }
            startFolderAnim();
        } else if (view.getId() == R.id.preview_layout) {
            //预览已选中的Album List
            if (albumFragment.getSelectList().size() == 0) {
                return;
            }
            Intent preIntent = new Intent(this, AlbumPreActivity.class);
            preIntent.putParcelableArrayListExtra(Constants.PreAlbumConstants.ALBUM_LIST, new ArrayList<>(albumFragment.getSelectList()));
            preIntent.putParcelableArrayListExtra(Constants.PreAlbumConstants.CHECK_LIST, new ArrayList<>(albumFragment.getSelectList()));
            preIntent.putExtra(Constants.PreAlbumConstants.INDEX, 0);
            preIntent.putExtra(Constants.PreAlbumConstants.LIMIT, bundle.getInt(Constants.AlbumRequestConstants.CHECK_LIMIT, 1));
            startActivityForResult(preIntent, Constants.PreAlbumConstants.PRE_REQUEST);

        } else if (view.getId() == R.id.shadow) {
            if (shadowFlag) {
                startFolderAnim();
            }
        }
    }

    /**
     * 切入切出动画
     */
    private void startFolderAnim() {
        if (animLock) {
            return;
        }
        if (folderRecyclerview.getVisibility() == View.GONE) {
            showAnim.start();
        } else {
            dismiss.start();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (folderRecyclerview != null
                    && folderRecyclerview.getVisibility() == View.VISIBLE) {
                startFolderAnim();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 回调文件夹List数据
     *
     * @param albumFolderList
     */
    @Override
    public void onFolderComplete(List<MediaFolderBean> albumFolderList) {
        //取的数据后的回调，初始化相应操作
        folderRecyclerview.setHasFixedSize(true);
        folderRecyclerview.addItemDecoration(new FolderItemDecoration(this));
        folderRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        this.mediaFolderList = albumFolderList;
        folderAdapter = new FolderAdapter(this, albumFolderList);
        folderAdapter.setOnFolderClickListener(this);
        folderRecyclerview.setAdapter(folderAdapter);
        //// TODO: 2016/9/30 顶破天？
        FolderUtils.setFolderHeight(folderRecyclerview, 5);

    }

    /**
     * 初始化动画参数
     */
    private void initAnim() {
        shadowArgb = new ArgbEvaluator();
        showAnim = ValueAnimator.ofFloat(1f, 0f);
        showAnim.setDuration(300);
        showAnim.addUpdateListener(animation -> changeUI((float) animation.getAnimatedValue()));
        showAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                animLock = true;
                folderRecyclerview.setVisibility(View.VISIBLE);
                shadowLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animLock = false;
                shadowFlag = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animLock = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        dismiss = ValueAnimator.ofFloat(0f, 1f);
        dismiss.setDuration(showAnim.getDuration());
        dismiss.addUpdateListener(animation -> changeUI((float) animation.getAnimatedValue()));
        dismiss.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                animLock = true;
                shadowFlag = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animLock = false;
                shadowLayout.setVisibility(View.INVISIBLE);
                folderRecyclerview.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animLock = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    /**
     * 动画动态改变位置
     *
     * @param value
     */
    private void changeUI(float value) {
        LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) folderRecyclerview.getLayoutParams();
        p.setMargins(0, 0, 0, -(int) (value * p.height));
        folderRecyclerview.setLayoutParams(p);
        shadowLayout.setBackgroundColor((Integer) shadowArgb.evaluate(value, 0x50000000, Color.TRANSPARENT));
    }

    /**
     * 文件夹点击回调
     *
     * @param position
     * @param change
     */
    @Override
    public void onFloderClick(int position, boolean change) {
        handler.postDelayed(() -> {
            dismiss.start();
            if (change) {
                folderText.setText(mediaFolderList.get(position).getFolderName());
                albumFragment.setAlbumList(mediaFolderList.get(position).getMediaList(), position == 0);
            }
        }, 350);

    }

    /**
     * 相册中点击拍照
     */
    @Override
    public void onCamera() {
        MediaPicker.startRequest(new CameraRequest.Builder(this, Constants.CAMERA_REQUEST)
                .needCrop(bundle.getBoolean(Constants.AlbumRequestConstants.NEED_CROP, false))
                .build());
    }

    /**
     * 点击Item
     *
     * @param position
     */
    @Override
    public void onAlbumClick(int position) {
        if (bundle.getBoolean(Constants.AlbumRequestConstants.AS_SINGLE, true)) {
            if (bundle.getBoolean(Constants.AlbumRequestConstants.NEED_CROP, false)) {
                //裁剪后再回调
                MediaBean bean = albumFragment.getMediaList().get(position);
                startCrop(new File(bean.getPath()), Constants.CROP_REQUEST);
            } else {
                //直接回调
                Intent data = new Intent();
                data.putExtra(Constants.RESULT_SINGLE, true);
                data.putExtra(Constants.RESULT_DATA, albumFragment.getMediaList().get(position).getPath());
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        } else {
            //预览AlbumList
            Intent intent = new Intent(this, AlbumPreActivity.class);

            intent.putParcelableArrayListExtra(Constants.PreAlbumConstants.ALBUM_LIST, new ArrayList<>(albumFragment.getMediaList()));
            intent.putParcelableArrayListExtra(Constants.PreAlbumConstants.CHECK_LIST, new ArrayList<>(albumFragment.getSelectList()));
            intent.putExtra(Constants.PreAlbumConstants.INDEX, position);
            intent.putExtra(Constants.PreAlbumConstants.LIMIT, bundle.getInt(Constants.AlbumRequestConstants.CHECK_LIMIT, 1));
            startActivityForResult(intent, Constants.PreAlbumConstants.PRE_REQUEST);
        }
    }

    @Override
    public void onSelect(int select) {
        //点击触发刷新ui
        if (select != 0) {
            previewText.setText("预览(" + select + ")");
            if (bundle.getInt(Constants.AlbumRequestConstants.CHECK_LIMIT, 1) == 1 && select == 1) {
                confirmBtn.setText("完成");
            } else {
                confirmBtn.setText("完成(" + select + "/" + bundle.getInt(Constants.AlbumRequestConstants.CHECK_LIMIT, 1) + ")");
            }
            previewText.setEnabled(true);
            confirmBtn.setEnabled(true);
        } else {
            previewText.setEnabled(false);
            confirmBtn.setEnabled(false);
            previewText.setText("预览");
            confirmBtn.setText("完成");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.DEFAULT_REQUEST) {
            if (resultCode != Activity.RESULT_OK) {
                finish();
                return;
            }
            //系统相册处理结果
            String path = DocumentUtils.getPath(this, data.getData());
            if (bundle.getBoolean(Constants.AlbumRequestConstants.NEED_CROP, false)) {
                startCrop(new File(path), Constants.DEFAULT_PLUS_REQUEST);
                return;
            }
            Intent intent = new Intent();
            intent.putExtra(Constants.RESULT_SINGLE, true);
            intent.putExtra(Constants.RESULT_DATA, path);
            setResult(Activity.RESULT_OK, intent);
            finish();
            return;
        }
        if (requestCode == Constants.DEFAULT_PLUS_REQUEST) {
            if (resultCode != Activity.RESULT_OK) {
                finish();
                return;
            }
            //系统相册+裁剪处理结果
            Intent intent = new Intent();
            intent.putExtra(Constants.RESULT_SINGLE, true);
            intent.putExtra(Constants.RESULT_DATA, tempCropFile.getPath());
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == Constants.PreAlbumConstants.PRE_REQUEST) {
            if (data.getBooleanExtra(Constants.PreAlbumConstants.FINISH, false)) {
                //预览回调
                List<MediaBean> checkList = data.getParcelableArrayListExtra(Constants.PreAlbumConstants.CHECK_LIST);
                Intent intent = new Intent();
                if (checkList.size() == 1) {
                    intent.putExtra(Constants.RESULT_SINGLE, true);
                    intent.putExtra(Constants.RESULT_DATA, checkList.get(0).getPath());
                } else {
                    intent.putExtra(Constants.RESULT_SINGLE, false);
                    intent.putStringArrayListExtra(Constants.RESULT_DATA, MediaUtils.beanToPathList(checkList));
                }
                setResult(Activity.RESULT_OK, intent);
                finish();
            } else {
                //刷新更改的
                List<MediaBean> checkList = data.getParcelableArrayListExtra(Constants.PreAlbumConstants.CHECK_LIST);
                albumFragment.setCheckList(checkList);
                onSelect(checkList.size());
            }
        } else if (requestCode == Constants.CROP_REQUEST) {
            //裁剪完毕回调
            Intent intent = new Intent();
            intent.putExtra(Constants.RESULT_SINGLE, true);
            intent.putExtra(Constants.RESULT_DATA, tempCropFile.getPath());
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else if (requestCode == Constants.CAMERA_REQUEST) {
            //CameraResultActivity处理结果
            setResult(Activity.RESULT_OK, data);
            finish();
        }
    }

    /**
     * 调用Android系统裁剪
     */
    private void startCrop(File source, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(Uri.fromFile(source), "image/*");
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
        startActivityForResult(intent, requestCode);
    }


}
