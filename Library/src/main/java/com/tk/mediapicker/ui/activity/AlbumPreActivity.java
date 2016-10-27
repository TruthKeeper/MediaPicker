package com.tk.mediapicker.ui.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tk.mediapicker.Constants;
import com.tk.mediapicker.R;
import com.tk.mediapicker.base.BaseActivity;
import com.tk.mediapicker.bean.MediaBean;
import com.tk.mediapicker.ui.adapter.AlbumPreAdapter;
import com.tk.mediapicker.ui.adapter.MediaPreAdapter;
import com.tk.mediapicker.widget.AlbumCheckView;
import com.tk.mediapicker.widget.ConfirmButton;
import com.tk.mediapicker.widget.HackyViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by TK on 2016/9/29.
 * 相册预览List
 */

public class AlbumPreActivity extends BaseActivity implements ViewPager.OnPageChangeListener, MediaPreAdapter.OnMediaClickListener, View.OnClickListener {

    private ImageView back;
    private HackyViewPager albumViewpager;
    private TextView title;
    private ConfirmButton confirmBtn;
    private LinearLayout headerLayout;
    private RelativeLayout bottomLayout;
    private LinearLayout checkLayout;
    private AlbumCheckView checkView;

    private AlbumPreAdapter albumPreAdapter;
    private List<MediaBean> albumList;
    private List<MediaBean> checkList;

    private int limit;
    private int firstIndex;
    private boolean fullScreen = true;
    private ValueAnimator valueAnimator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_preview);
        initViews();
        initConstants();
        initAnim();

        albumPreAdapter = new AlbumPreAdapter(this, albumList);
        albumViewpager.setAdapter(albumPreAdapter);
        albumViewpager.setCurrentItem(firstIndex, false);
        //初始化index
        title.setText((firstIndex + 1) + "/" + albumList.size());
        confirmBtn.setEnabled(true);
        refreshSelect(firstIndex);

        albumViewpager.setOffscreenPageLimit(3);
        albumViewpager.addOnPageChangeListener(this);
        albumPreAdapter.setOnMediaClickListener(this);

    }

    /**
     * 初始化views
     */
    private void initViews() {
        back = (ImageView) findViewById(R.id.back);
        albumViewpager = (HackyViewPager) findViewById(R.id.album_viewpager);
        title = (TextView) findViewById(R.id.title);
        confirmBtn = (ConfirmButton) findViewById(R.id.confirm_btn);
        headerLayout = (LinearLayout) findViewById(R.id.header_layout);
        bottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        checkLayout = (LinearLayout) findViewById(R.id.check_layout);
        checkView = (AlbumCheckView) findViewById(R.id.check_view);

        headerLayout.setBackgroundDrawable(bottomLayout.getBackground());

        back.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        checkLayout.setOnClickListener(this);
    }

    /**
     * 接受，并处理参数
     */
    private void initConstants() {
        albumList = getIntent().getParcelableArrayListExtra(Constants.PreAlbumConstants.ALBUM_LIST);
        checkList = getIntent().getParcelableArrayListExtra(Constants.PreAlbumConstants.CHECK_LIST);
        firstIndex = getIntent().getIntExtra(Constants.PreAlbumConstants.INDEX, 0);
        limit = getIntent().getIntExtra(Constants.PreAlbumConstants.LIMIT, 0);
    }

    /**
     * 初始化值动画
     */
    private void initAnim() {
        valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        valueAnimator.setDuration(500);
        valueAnimator.addUpdateListener(animation -> {
            float f = (float) animation.getAnimatedValue();
            RelativeLayout.LayoutParams headP = (RelativeLayout.LayoutParams) headerLayout.getLayoutParams();
            RelativeLayout.LayoutParams bottomP = (RelativeLayout.LayoutParams) bottomLayout.getLayoutParams();
            if (fullScreen) {
                //消失
                headP.setMargins(0, (int) (-headP.height * f), 0, 0);
                bottomP.setMargins(0, 0, 0, (int) (-bottomP.height * f));
            } else {
                //显示
                headP.setMargins(0, (int) (-headP.height * (1 - f)), 0, 0);
                bottomP.setMargins(0, 0, 0, (int) (-bottomP.height * (1 - f)));
            }
            headerLayout.setLayoutParams(headP);
            bottomLayout.setLayoutParams(bottomP);
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                fullScreen = !fullScreen;
                //// TODO: 2016/9/29 界面闪动，记录上次位移的矩阵？
//                if (fullScreen) {
//                    findViewById(android.R.id.content).setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//                } else {
//                    findViewById(android.R.id.content).setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
//                }
//                AlbumUtils.needFullScreen(AlbumPreActivity.this, fullScreen);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    public void onClick(View view) {
        if (view.getId() == R.id.back) {
            refreshOnResult(false);
        } else if (view.getId() == R.id.confirm_btn) {
            refreshOnResult(true);
        } else if (view.getId() == R.id.check_layout) {
            if (checkView.isChecked()) {
                /// /反选
                checkList.remove(albumList.get(albumViewpager.getCurrentItem()));
                refreshSelect(albumViewpager.getCurrentItem());
            } else {
                if (checkList.size() < limit) {
                    checkList.add(albumList.get(albumViewpager.getCurrentItem()));
                    refreshSelect(albumViewpager.getCurrentItem());
                } else {
                    Toast.makeText(this, "您最多只能选" + limit + "张照片", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            refreshOnResult(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 回调刷新点击的项目
     *
     * @param finish 直接完成
     */
    private void refreshOnResult(boolean finish) {
        Intent data = new Intent();
        if (finish && checkList.size() == 0) {
            //仿微信人性化设计，checkList==0空时添加当前的index
            checkList.add(albumList.get(albumViewpager.getCurrentItem()));
        }
        data.putParcelableArrayListExtra(Constants.PreAlbumConstants.CHECK_LIST, new ArrayList<>(checkList));
        data.putExtra(Constants.PreAlbumConstants.FINISH, finish);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    /**
     * 刷新选中数目
     *
     * @param position
     */
    private void refreshSelect(int position) {
        checkView.setChecked(checkList.contains(albumList.get(position)));
        if (checkList.size() == 0) {
            confirmBtn.setText("完成");
        } else {
            confirmBtn.setText("完成(" + checkList.size() + "/" + limit + ")");
        }
    }

    @Override
    public void onClick(int position) {
        //点击屏幕隐藏ui
        if (!valueAnimator.isRunning()) {
            valueAnimator.start();
        }
    }

    /**
     * 播放视频
     *
     * @param position
     */
    @Override
    public void onClickVideo(int position) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(albumList.get(position).getPath())), "video/mp4");
        startActivity(intent);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        title.setText((position + 1) + "/" + albumList.size());
        refreshSelect(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }


}
