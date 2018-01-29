package com.lch.netkit.imageloader.transformation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import com.apkfuns.logutils.LogUtils;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;
import java.util.Locale;

public class RoundedRecTransformation extends BitmapTransformation {

    private static final String TAG = "RoundedRecTransformation";

    private final Paint borderPaint = new Paint();
    private final Paint paint = new Paint();

    {
        paint.setAntiAlias(true);

        borderPaint.setAntiAlias(true);
        borderPaint.setDither(true);
        borderPaint.setStyle(Paint.Style.STROKE);
    }

    private final float[] radii = new float[8];

    private float topLeftRadius;
    private float topRightRadius;
    private float bottomLefRadius;
    private float bottomRightRadius;

    private int borderColor;
    private float borderWidth;


    public RoundedRecTransformation(float borderWidth, int borderColor, float topLeftRadius, float topRightRadius, float bottomLefRadius, float bottomRightRadius) {

        this.topLeftRadius = topLeftRadius;
        this.topRightRadius = topRightRadius;
        this.bottomLefRadius = bottomLefRadius;
        this.bottomRightRadius = bottomRightRadius;

        radii[0] = topLeftRadius;
        radii[1] = topLeftRadius;

        radii[2] = topRightRadius;
        radii[3] = topRightRadius;

        radii[4] = bottomRightRadius;
        radii[5] = bottomRightRadius;

        radii[6] = bottomLefRadius;
        radii[7] = bottomLefRadius;

        this.borderWidth = borderWidth;
        this.borderColor = borderColor;

        borderPaint.setStrokeWidth(borderWidth);
        borderPaint.setColor(borderColor);
    }

    public RoundedRecTransformation(float borderWidth, int borderColor, float radius) {
        this(borderWidth, borderColor, radius, radius, radius, radius);
    }


    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {

        LogUtils.e(TAG, String.format(Locale.ENGLISH, "outWidth=%d,outHeight=%d", outWidth, outHeight));

        Bitmap bitmap = pool.get(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        bitmap.setHasAlpha(true);

        Canvas canvas = new Canvas(bitmap);

        RectF rectF = new RectF(0, 0, outWidth, outHeight);

        Path path = new Path();
        path.addRoundRect(rectF, radii, Path.Direction.CW);

        canvas.drawPath(path, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        rectF.inset(borderWidth / 2, borderWidth / 2);

        canvas.drawBitmap(toTransform, null, rectF, paint);

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
        return "RoundedRecTransformation(borderColor=" + borderColor + ", borderWidth=" + borderWidth + ", topLeftRadius="
                + topLeftRadius + ", topRightRadius=" + topRightRadius + ", bottomLefRadius=" + bottomLefRadius + ", bottomRightRadius=" + bottomRightRadius + ")";
    }

}