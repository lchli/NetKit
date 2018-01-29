package com.lch.netkit.imageloader.transformation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;
import java.util.Locale;

public class CircleTransformation extends BitmapTransformation {

    private static final String TAG = "CircleTransformation";

    private final Paint borderPaint = new Paint();
    private final Paint paint = new Paint();

    {
        paint.setAntiAlias(true);

        borderPaint.setAntiAlias(true);
        borderPaint.setDither(true);
        borderPaint.setStyle(Paint.Style.STROKE);
    }

    private int borderColor;
    private float borderWidth;


    public CircleTransformation(float borderWidth, int borderColor) {
        this.borderWidth = borderWidth;
        this.borderColor = borderColor;
        borderPaint.setStrokeWidth(borderWidth);
        borderPaint.setColor(borderColor);
    }


    @Override
    protected Bitmap transform(@NonNull BitmapPool pool,

                               @NonNull Bitmap toTransform, int outWidth, int outHeight) {

        LogUtils.e(TAG, String.format(Locale.ENGLISH, "outWidth=%d,outHeight=%d", outWidth, outHeight));

        Bitmap bitmap = pool.get(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        bitmap.setHasAlpha(true);

        float r = Math.min(outHeight, outWidth) / 2;
        float cx = outWidth / 2;
        float cy = outHeight / 2;

        Path path = new Path();
        path.addCircle(cx, cy, r, Path.Direction.CW);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawPath(path, paint);

        RectF rectFCircle = new RectF(cx - r, cy - r, cx + r, cy + r);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(toTransform, null, rectFCircle, paint);

        if (borderWidth > 0) {
            canvas.clipPath(path);
            canvas.drawPath(path, borderPaint);
        }

        return bitmap;

    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(key().getBytes());
    }

    private String key() {
        return "CircleTransformation(borderColor=" + borderColor + ", borderWidth=" + borderWidth + ")";
    }

}