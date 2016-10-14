package com.tk.mediapicker.photopicker.ui.activity;

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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tk.mediapicker.R;
import com.tk.mediapicker.photopicker.Constants;
import com.tk.mediapicker.photopicker.bean.AlbumBean;
import com.tk.mediapicker.photopicker.bean.AlbumFolderBean;
import com.tk.mediapicker.photopicker.callback.OnFolderListener;
import com.tk.mediapicker.photopicker.ui.adapter.AlbumAdapter;
import com.tk.mediapicker.photopicker.ui.adapter.FolderAdapter;
import com.tk.mediapicker.photopicker.ui.fragment.AlbumFragment;
import com.tk.mediapicker.photopicker.utils.FolderUtils;
import com.tk.mediapicker.photopicker.widget.ConfirmButton;
import com.tk.mediapicker.photopicker.widget.FolderItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.tk.mediapicker.R.id.folder_layout;
import static com.tk.mediapicker.photopicker.Constants.CROP_REQUEST;


/**
 * Created by TK on 2016/8/10.
 */
public class AlbumActivity extends AppCompatActivity implements OnFolderListener, FolderAdapter.onFolderClickListener, AlbumAdapter.OnAlbumSelectListener, View.OnClickListener {


    private ImageView back;
    private ConfirmButton confirmBtn;
    private RecyclerView folderRecyclerview;
    private TextView folderText;
    private TextView previewText;
    private LinearLayout folderLayout;
    private LinearLayout previewLayout;
    private View shadow;
    private LinearLayout shadowLayout;

    private AlbumFragment albumFragment = new AlbumFragment();
    private FolderAdapter folderAdapter;
    //文件夹list
    private List<AlbumFolderBean> albumFolderList = new ArrayList<AlbumFolderBean>();

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
        setContentView(R.layout.activity_album);
        initViews();
        initConstants();

        shadowArgb = new ArgbEvaluator();
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
        folderLayout = (LinearLayout) findViewById(folder_layout);
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
     * 接收并处理PhotoPick的配置
     */
    private void initConstants() {
        bundle = getIntent().getExtras();
        if (bundle.getBoolean(Constants.PhotoPickConstants.IS_SINGLE)) {
            //单选模式
            previewLayout.setVisibility(View.GONE);
            confirmBtn.setVisibility(View.GONE);
        }
    }

    public void onClick(View view) {
        if (view.getId() == R.id.back) {
            finish();
        } else if (view.getId() == R.id.confirm_btn) { //完成选择，回调
            Intent intent = new Intent();
            List<AlbumBean> checkList = albumFragment.getSelectList();
            if (bundle.getInt(Constants.PhotoPickConstants.CHECK_LIMIT) == 1
                    || checkList.size() == 1) {
                intent.putExtra(Constants.PhotoPickConstants.RESULT_SINGLE, true);
                intent.putExtra(Constants.PhotoPickConstants.RESULT_DATA, checkList.get(0).getPath());
            } else {
                intent.putExtra(Constants.PhotoPickConstants.RESULT_SINGLE, false);
                intent.putParcelableArrayListExtra(Constants.PhotoPickConstants.RESULT_DATA, new ArrayList<>(checkList));
            }
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else if (view.getId() == folder_layout) {//文件夹
            if (animLock || albumFolderList.size() == 1) {
                return;
            }
            startFolderAnim();
        } else if (view.getId() == R.id.preview_layout) {   //预览已选中的Album List
            if (albumFragment.getSelectList().size() == 0) {
                return;
            }
            Intent preIntent = new Intent(this, AlbumPreActivity.class);
            preIntent.putParcelableArrayListExtra(Constants.PreAlbumConstants.ALBUM_LIST, new ArrayList<>(albumFragment.getSelectList()));
            preIntent.putParcelableArrayListExtra(Constants.PreAlbumConstants.CHECK_LIST, new ArrayList<>(albumFragment.getSelectList()));
            preIntent.putExtra(Constants.PreAlbumConstants.INDEX, 0);
            preIntent.putExtra(Constants.PreAlbumConstants.LIMIT, bundle.getInt(Constants.PhotoPickConstants.CHECK_LIMIT));
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
        if (folderRecyclerview.getVisibility() == View.GONE) {
            showAnim.start();
        } else {
            dismiss.start();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (folderRecyclerview.getVisibility() == View.VISIBLE) {
                startFolderAnim();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onFolderComplete(List<AlbumFolderBean> albumFolderList) {
        //取的数据后的回调，初始化相应操作
        folderRecyclerview.setHasFixedSize(true);
        folderRecyclerview.addItemDecoration(new FolderItemDecoration(this));
        folderRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        this.albumFolderList = albumFolderList;
        folderAdapter = new FolderAdapter(this, albumFolderList);
        folderAdapter.setOnFolderClickListener(this);
        folderRecyclerview.setAdapter(folderAdapter);
        //// TODO: 2016/9/30 顶破天？
        FolderUtils.setFolderHeight(folderRecyclerview, 5);
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

    @Override
    public void onClick(int position, boolean change) {
        handler.postDelayed(() -> {
            dismiss.start();
            if (change) {
                folderText.setText(albumFolderList.get(position).getFolderName());
                albumFragment.setAlbumList(albumFolderList.get(position).getAlbumList(), position == 0);
            }
        }, 350);

    }

    @Override
    public void onCamera() {
        //相册中点击拍照
        Intent intent = new Intent(this, CameraResultActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, Constants.DEFAULT_REQUEST);
    }

    @Override
    public void onClick(int position) {
        if (bundle.getInt(Constants.PhotoPickConstants.CHECK_LIMIT) != 1) {
            //预览AlbumList
            Intent intent = new Intent(this, AlbumPreActivity.class);
            intent.putParcelableArrayListExtra(Constants.PreAlbumConstants.ALBUM_LIST, new ArrayList<>(albumFragment.getAlbumList()));
            intent.putParcelableArrayListExtra(Constants.PreAlbumConstants.CHECK_LIST, new ArrayList<>(albumFragment.getSelectList()));
            intent.putExtra(Constants.PreAlbumConstants.INDEX, position);
            intent.putExtra(Constants.PreAlbumConstants.LIMIT, bundle.getInt(Constants.PhotoPickConstants.CHECK_LIMIT));
            startActivityForResult(intent, Constants.PreAlbumConstants.PRE_REQUEST);
        } else {
            if (bundle.getBoolean(Constants.PhotoPickConstants.NEED_CROP, false)) {
                //裁剪后再回调
                AlbumBean bean = albumFragment.getAlbumList().get(position);
                startCrop(new File(bean.getPath()));
            } else {
                //直接回调
                Intent data = new Intent();
                data.putExtra(Constants.PhotoPickConstants.RESULT_SINGLE, true);
                data.putExtra(Constants.PhotoPickConstants.RESULT_DATA, albumFragment.getAlbumList().get(position).getPath());
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        }
    }

    @Override
    public void onSelect(int select) {
        //点击触发刷新ui
        if (select != 0) {
            previewText.setText("预览(" + select + ")");
            if (bundle.getInt(Constants.PhotoPickConstants.CHECK_LIMIT, 1) == 1 && select == 1) {
                confirmBtn.setText("完成");
            } else {
                confirmBtn.setText("完成(" + select + "/" + bundle.getInt(Constants.PhotoPickConstants.CHECK_LIMIT) + ")");
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
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == Constants.PreAlbumConstants.PRE_REQUEST) {
            if (data.getBooleanExtra(Constants.PreAlbumConstants.FINISH, false)) {
                //预览回调
                List<AlbumBean> checkList = data.getParcelableArrayListExtra(Constants.PreAlbumConstants.CHECK_LIST);
                Intent intent = new Intent();
                if (checkList.size() == 1) {
                    intent.putExtra(Constants.PhotoPickConstants.RESULT_SINGLE, true);
                    intent.putExtra(Constants.PhotoPickConstants.RESULT_DATA, checkList.get(0).getPath());
                } else {
                    intent.putExtra(Constants.PhotoPickConstants.RESULT_SINGLE, false);
                    intent.putParcelableArrayListExtra(Constants.PhotoPickConstants.RESULT_DATA, new ArrayList<>(checkList));
                }
                setResult(Activity.RESULT_OK, intent);
                finish();
            } else {
                //刷新更改的
                List<AlbumBean> checkList = data.getParcelableArrayListExtra(Constants.PreAlbumConstants.CHECK_LIST);
                albumFragment.setCheckList(checkList);
                onSelect(checkList.size());
            }
        } else if (requestCode == CROP_REQUEST) {
            //裁剪完毕回调
            Intent intent = new Intent();
            intent.putExtra(Constants.PhotoPickConstants.RESULT_SINGLE, true);
            intent.putExtra(Constants.PhotoPickConstants.RESULT_DATA, tempCropFile.getPath());
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else if (requestCode == Constants.DEFAULT_REQUEST) {
            //CameraResultActivity处理结果
            setResult(Activity.RESULT_OK, data);
            finish();
        }
    }

    /**
     * 调用Android系统裁剪
     */
    private void startCrop(File source) {
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
        startActivityForResult(intent, Constants.CROP_REQUEST);
    }


}
