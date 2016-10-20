package com.tk.mediapicker;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;

/**
 * Created by TK on 2016/9/29.
 */

public class MyGlideModule implements com.bumptech.glide.module.GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        //设置图片内存缓存占用八分之一
        int memoryCacheSize = (int) Runtime.getRuntime().maxMemory() / 8;
        //50Mb
        int diskCacheSize = 1024 * 1024 * 50;
        builder
                //设置内存缓存大小
                .setMemoryCache(new LruResourceCache(memoryCacheSize))
                //设置内部磁盘缓存
                .setDiskCache(new InternalCacheDiskCacheFactory(context, "Glide", diskCacheSize))
                .setDecodeFormat(DecodeFormat.PREFER_RGB_565);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }
}
