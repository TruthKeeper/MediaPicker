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
import com.tk.mediapicker.bean.MediaBean;
import com.tk.mediapicker.utils.AlbumUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * Created by TK on 2016/9/29.
 * viewpager的预览adapter
 */

public class AlbumPreAdapter extends PagerAdapter {
    private LinkedList<View> mCacheList = new LinkedList<View>();
    private List<MediaBean> albumList;
    private Context mContext;
    private OnPhotoListener onPhotoListener;
    private LayoutInflater mInflater;
    private int w;
    private int h;


    public AlbumPreAdapter(Context mContext, List<MediaBean> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
        mInflater = LayoutInflater.from(mContext);
        w = mContext.getResources().getDisplayMetrics().widthPixels;
        h = mContext.getResources().getDisplayMetrics().heightPixels;
    }

    @Override
    public int getCount() {
        return albumList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View contentView = null;
        PhotoView photoView;
        if (albumList.get(position).isVideo()) {
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
                    if (onPhotoListener != null) {
                        onPhotoListener.onClickVideo(position);
                    }
                }
            });
            ((TextView) contentView.findViewById(R.id.size)).setText("视频大小：" + AlbumUtils.getFormatSize(albumList.get(position).getSize()));
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
                .load(new File(albumList.get(position).getPath()))
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
            if (onPhotoListener != null) {
                onPhotoListener.onClick(position);
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

    public void setOnPhotoListener(OnPhotoListener onPhotoListener) {
        this.onPhotoListener = onPhotoListener;
    }

    public interface OnPhotoListener {
        void onClick(int position);

        void onClickVideo(int position);
    }
}
