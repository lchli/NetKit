package com.lch.netkit.imageloader.module;

import android.content.Context;
import android.util.Log;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;
import com.lch.netkit.imageloader.LiImageLoader;

@GlideModule
public class BAFAppGlideModule extends AppGlideModule {
    private static final String TAG = "BAFAppGlideModule";

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        LiImageLoader.Setting setting = LiImageLoader.instance().setting();
        if (setting == null) {
            return;
        }

        builder.setMemoryCache(new LruResourceCache(setting.getMemoryCacheSize()));
        builder.setDiskCache(new DiskLruCacheFactory(setting.getDiskCacheFolder(), setting.getDiskCacheSize()));
        builder.setLogLevel(setting.isLog() ? Log.DEBUG : Log.ERROR);
    }
}