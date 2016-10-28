package com.tk.mediapicker.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tk.mediapicker.Constants;
import com.tk.mediapicker.R;
import com.tk.mediapicker.base.BaseActivity;
import com.tk.mediapicker.ui.adapter.MediaPreAdapter;
import com.tk.mediapicker.widget.HackyViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TK on 2016/10/14.
 * 外部查看Media list的入口
 */

public class MediaPreActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private HackyViewPager photoviewpager;
    private ImageView back;
    private TextView title;
    private ImageView delete;
    private MediaPreAdapter mediaPreAdapter;
    private List<String> fileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_preview);
        photoviewpager = (HackyViewPager) findViewById(R.id.photo_viewpager);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        delete = (ImageView) findViewById(R.id.delete);
        back.setOnClickListener(this);
        delete.setOnClickListener(this);
        Bundle bundle = getIntent().getExtras();
        fileList = bundle.getStringArrayList(Constants.PreMediaConstants.REQUEST_DATA);
        int index = bundle.getInt(Constants.PreMediaConstants.INDEX, 0);
        if (fileList == null || fileList.size() == 0) {
            throw new IllegalArgumentException("fileList is null!");
        }
        title.setText((index + 1) + "/" + fileList.size());
        mediaPreAdapter = new MediaPreAdapter(this, fileList);
        photoviewpager.setAdapter(mediaPreAdapter);
        photoviewpager.setCurrentItem(index);
        photoviewpager.addOnPageChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            finish();
        } else if (v.getId() == R.id.delete) {
            if (fileList.size() == 1) {
                fileList.clear();
                finish();
            } else {
                int index = photoviewpager.getCurrentItem();
                fileList.remove(index);
                mediaPreAdapter.notifyDataSetChanged();
                photoviewpager.setAdapter(mediaPreAdapter);
                if (fileList.size() > index) {
                    photoviewpager.setCurrentItem(index, false);
                    title.setText((index + 1) + "/" + fileList.size());
                } else {
                    photoviewpager.setCurrentItem(fileList.size() - 1, false);
                    title.setText(fileList.size() + "/" + fileList.size());
                }
            }
        }
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(Constants.RESULT_SINGLE, false);
        intent.putStringArrayListExtra(Constants.RESULT_DATA, new ArrayList<>(fileList));
        setResult(Activity.RESULT_OK, intent);
        super.finish();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        title.setText((position + 1) + "/" + fileList.size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
