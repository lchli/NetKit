package com.lch.netkit.imageloader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.lch.netkit.common.base.SingletonHolder;
import com.lch.netkit.imageloader.transformation.CircleTransformation;
import com.lch.netkit.imageloader.transformation.RoundedRecTransformation;

import java.io.File;


/**
 * Created by Administrator on 2017/6/15.
 */
public final class LiImageLoader {

    private static final SingletonHolder<LiImageLoader> holder = new SingletonHolder<LiImageLoader>() {
        @Override
        protected LiImageLoader create(Object... objects) {
            return new LiImageLoader();
        }
    };
    private static final String TAG = "LiImageLoader";

    private Setting mSetting;


    public static Setting newSetting(@NonNull Context context) {
        return new Setting(context.getApplicationContext());
    }


    public static LiImageLoader instance() {
        return holder.get();
    }

    public void init(@NonNull Setting setting) {
        if (mSetting != null) {
            return;
        }
        mSetting = setting;
    }

    public Setting setting() {
        return mSetting;
    }

    private boolean isInitialized() {
        return mSetting != null;
    }

    public Builder builder() {
        return new Builder();
    }


    public static class Setting {
        private int memoryCacheSize = 5 * 1024 * 1024;
        private int diskCacheSize = DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE;
        private boolean log = false;
        private String diskCacheFolder;
        private Context context;

        private Setting(Context context) {
            this.context = context;
            File dir = context.getExternalCacheDir();
            if (dir != null) {
                diskCacheFolder = dir.getAbsolutePath();
            }
        }

        public Setting setMemoryCacheSize(int memoryCacheSize) {
            this.memoryCacheSize = memoryCacheSize;
            return this;
        }

        public int getMemoryCacheSize() {
            return memoryCacheSize;
        }

        public Setting setDiskCacheSize(int diskCacheSize) {
            this.diskCacheSize = diskCacheSize;
            return this;
        }

        public int getDiskCacheSize() {
            return diskCacheSize;
        }

        public Setting setLog(boolean log) {
            this.log = log;
            return this;
        }

        public boolean isLog() {
            return log;
        }

        public void setDiskCacheFolder(String diskCacheFolder) {
            this.diskCacheFolder = diskCacheFolder;
        }

        public String getDiskCacheFolder() {
            return diskCacheFolder;
        }

        public Context getContext() {
            return context;
        }
    }

    @SuppressWarnings("CheckResult")
    public class Builder {
        private final Context context;
        private String source;

        private ImageView view;
        private Integer width;
        private Integer height;

        private Integer failResId;
        private Drawable failDrawable;

        private Integer placeHolderResId;
        private Drawable placeHolderDrawable;

        private boolean circleImage = false;
        private boolean gif = false;
        private Integer fadeDuration;

        private int borderColor = Color.BLACK;
        private float borderWidth;

        private float topLeftRadius;
        private float topRightRadius;
        private float bottomLefRadius;
        private float bottomRightRadius;

        private Builder() {
            this.context = mSetting.getContext();
        }

        public void display(Activity activity) {
            display(Glide.with(activity));
        }

        public void display(Fragment fragment) {
            display(Glide.with(fragment));
        }

        public void display(Context context) {
            display(Glide.with(context));
        }

        private void display(RequestManager requestManager) {

            if (!isInitialized()) {
                return;
            }

            if (view == null) {
                return;
            }

            if (this.source == null) {
                return;
            }

            final RequestBuilder<Drawable> requestBuilder = requestManager.asDrawable();
            RequestOptions requestOptions = new RequestOptions();
            DrawableTransitionOptions transitionOptions = DrawableTransitionOptions.withCrossFade(0);

            if (width != null && height != null) {
                requestOptions.override(width, height);
            }

            if (failDrawable != null) {
                requestOptions.error(failDrawable);
            } else if (failResId != null) {
                requestOptions.error(failResId);
            }

            if (placeHolderDrawable != null) {
                requestOptions.placeholder(placeHolderDrawable);
            } else if (placeHolderResId != null) {
                requestOptions.placeholder(placeHolderResId);
            }

            if (fadeDuration != null) {
                transitionOptions = DrawableTransitionOptions.withCrossFade(fadeDuration);
            }

            if (!gif) {
                if (circleImage) {
                    requestOptions.transform(new CircleTransformation(borderWidth, borderColor));
                } else {
                    requestOptions.transform(new RoundedRecTransformation(borderWidth, borderColor, topLeftRadius, topRightRadius, bottomLefRadius, bottomRightRadius));
                }
            }

            requestBuilder.apply(requestOptions)
                    .transition(transitionOptions)
                    .load(source).into(view);
        }

        public Builder source(String src) {
            this.source = src;
            return this;
        }

        /**
         * @param name - name of file in assets
         * @return
         */
        public Builder asset(String name) {
            String asset = "file:///android_asset/" + name;
            return this.source(asset);
        }

        /**
         * @param file - file in local storage
         * @return
         */
        public Builder file(File file) {
            String local = "file://" + file.getAbsolutePath();
            return this.source(local);
        }

        /**
         * @param uri - uri of a content
         * @return
         */
        public Builder content(Uri uri) {
            String content = uri.toString();
            return this.source(content);
        }

        /**
         * @param name - raw resource name
         * @return
         */
        public Builder raw(String name) {
            String source = "android.resource://" + context.getPackageName() + "/raw/" + name;
            return this.source(source);
        }

        /**
         * @param name - drawable name
         * @return
         */
        public Builder drawable(String name) {
            String source = "android.resource://" + context.getPackageName() + "/drawable/" + name;
            return this.source(source);
        }


        public Builder view(ImageView view) {
            this.view = view;
            return this;
        }

        public Builder resize(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder failImage(int resId) {
            this.failResId = resId;
            return this;
        }

        public Builder failImage(Drawable drawable) {
            this.failDrawable = drawable;
            return this;
        }

        public Builder placeHolderImage(int resId) {
            this.placeHolderResId = resId;
            return this;
        }

        public Builder placeHolderImage(Drawable drawable) {
            this.placeHolderDrawable = drawable;
            return this;
        }


        public Builder roundCorner(float radius) {
            return roundCorner(radius, radius, radius, radius);
        }

        public Builder roundCorner(float topLeftRadius, float topRightRadius, float bottomLefRadius, float bottomRightRadius) {
            this.topLeftRadius = topLeftRadius;
            this.topRightRadius = topRightRadius;
            this.bottomLefRadius = bottomLefRadius;
            this.bottomRightRadius = bottomRightRadius;
            return this;
        }

        public Builder circle() {
            this.circleImage = true;
            return this;
        }

        public Builder fadeDuration(int durationMills) {
            this.fadeDuration = durationMills;
            return this;
        }

        public Builder border(int color, float width) {
            this.borderColor = color;
            this.borderWidth = width;
            return this;
        }

        public Builder asGif() {
            this.gif = true;
            return this;
        }


    }

}
