package com.tk.mediapicker.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * Created by TK on 2016/10/14.
 * viewpager的预览adapter
 */

public class PhotoPreAdapter extends PagerAdapter {
    private LinkedList<PhotoView> mCacheList = new LinkedList<PhotoView>();
    private List<String> fileList;
    private Context mContext;
    private int w;
    private int h;

    public PhotoPreAdapter(Context mContext, List<String> fileList) {
        this.mContext = mContext;
        this.fileList = fileList;
        w = mContext.getResources().getDisplayMetrics().widthPixels;
        h = mContext.getResources().getDisplayMetrics().heightPixels;
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final PhotoView photoView;
        if (mCacheList.size() == 0) {
            photoView = new PhotoView(mContext);
        } else {
            //从缓存集合中取
            photoView = mCacheList.removeFirst();
        }
        photoView.setBackgroundColor(Color.BLACK);
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
        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        ((PhotoView) object).setImageDrawable(null);
        mCacheList.add((PhotoView) object);
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


}
