package com.tk.mediapicker.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.tk.mediapicker.R;
import com.tk.mediapicker.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * Created by TK on 2016/10/14.
 * viewpager的预览adapter
 */

public class MediaPreAdapter extends PagerAdapter {
    private LinkedList<View> mCacheList = new LinkedList<View>();
    private ArrayList<String> fileList;
    private Context mContext;
    private OnMediaClickListener onMediaClickListener;
    private LayoutInflater mInflater;
    private int w;
    private int h;


    public MediaPreAdapter(Context mContext, ArrayList<String> fileList) {
        this.mContext = mContext;
        this.fileList = fileList;
        mInflater = LayoutInflater.from(mContext);
        w = mContext.getResources().getDisplayMetrics().widthPixels;
        h = mContext.getResources().getDisplayMetrics().heightPixels;
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View contentView = null;
        PhotoView photoView;
        if (FileUtils.isVideo(new File(fileList.get(position)).getName())) {
            if (mCacheList.size() != 0 && mCacheList.getFirst() instanceof RelativeLayout) {
                //从缓存集合中取
                contentView = mCacheList.removeFirst();
            } else {
                contentView = mInflater.inflate(R.layout.container_preview_video, null);
            }
            photoView = (PhotoView) contentView.findViewById(R.id.item);
            contentView.findViewById(R.id.video).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onMediaClickListener != null) {
                        onMediaClickListener.onClickVideo(position);
                    }
                }
            });
            ((TextView) contentView.findViewById(R.id.size)).setText("视频大小：" + FileUtils.getFileSize(new File(fileList.get(position))));
        } else {
            if (mCacheList.size() != 0 && mCacheList.getFirst() instanceof PhotoView) {
                //从缓存集合中取
                contentView = mCacheList.removeFirst();
            } else {
                contentView = mInflater.inflate(R.layout.container_preview_photo, null);
            }
            photoView = (PhotoView) contentView;
        }
        final PhotoViewAttacher attacher = new PhotoViewAttacher(photoView);
        Glide.with(mContext)
                .load(new File(fileList.get(position)))
                .asBitmap()
                .override(w, h)
                .fitCenter()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        photoView.setImageBitmap(resource);
                        attacher.update();
                    }
                });
        attacher.setOnViewTapListener((view, x, y) -> {
            if (onMediaClickListener != null) {
                onMediaClickListener.onClick(position);
            }
        });
        container.addView(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return contentView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mCacheList.add((View) object);
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setOnMediaClickListener(OnMediaClickListener onMediaClickListener) {
        this.onMediaClickListener = onMediaClickListener;
    }

    public interface OnMediaClickListener {
        void onClick(int position);

        void onClickVideo(int position);
    }
}
