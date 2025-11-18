package com.bokecc.vod.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.bokecc.vod.HuoDeApplication;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class CustomLogoView extends RelativeLayout {
    private ImageView imageView;
    private String img;
    private float xPosRate = 0.1f, yPosRate = 0.1f, logoWidthRate = 0.1f, logoHeightRate = 0.1f;
    private int intrinsicWidth;
    private int intrinsicHeight;

    public CustomLogoView(Context context) {
        this(context, null);
    }

    public CustomLogoView(Context context, AttributeSet attrs) {
        super(context, attrs);

        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setVisibility(INVISIBLE);
        this.addView(imageView);
    }

    /**
     * @param img 图片地址
     * @param xPosRate 相对于左上角的X轴位置偏移量与播放窗口宽度的比例
     * @param yPosRate 相对于左上角的Y轴位置偏移量播放窗口高度的比例
     * @param logoWidthRate Logo宽度相对于播放窗口宽度的比例
     * @param logoHeightRate Logo高度相对于播放窗口高度的比例
     */
    public void setCustomLogoInfo(String img, float xPosRate, float yPosRate, float logoWidthRate, float logoHeightRate) {
        this.img = img;
        this.xPosRate = xPosRate;
        this.yPosRate = yPosRate;
        if (logoWidthRate > 0) {
            this.logoWidthRate = logoWidthRate;
        }
        if (logoHeightRate > 0) {
            this.logoHeightRate = logoHeightRate;
        }
    }


    public void show() {
        if (TextUtils.isEmpty(img)) {
            return;
        }
        Glide.with(HuoDeApplication.getContext()).load(img).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                if (resource != null) {
                    if (resource instanceof GifDrawable) {
                        ((GifDrawable) resource).setLoopCount(GifDrawable.LOOP_FOREVER);
                    }

                    intrinsicWidth = resource.getIntrinsicWidth();
                    intrinsicHeight = resource.getIntrinsicHeight();
                    adjustView();
                }
                return false;
            }
        }).into(imageView);
    }

    private void adjustView() {
        ViewGroup parent = (ViewGroup) imageView.getParent();
        if (intrinsicWidth >0 && intrinsicHeight > 0 && parent.getWidth() > 0) {
            int parentWidth = parent.getWidth();
            int parentHeight = parent.getHeight();
            int xOffset = (int) (xPosRate * parentWidth);
            int yOffset = (int) (yPosRate * parentHeight);

            int logoWidth = (int) (parentWidth * logoWidthRate);
            int logoHeight = (int) (parentHeight * logoHeightRate);
            int newLogoWidth = logoWidth;
            int newLogoHeight = newLogoWidth * intrinsicHeight / intrinsicWidth;
            if (newLogoHeight > logoHeight) {
                newLogoHeight = logoHeight;
                newLogoWidth = newLogoHeight * intrinsicWidth / intrinsicHeight;
            }
            imageView.setVisibility(VISIBLE);
            LayoutParams layoutParams = (LayoutParams) imageView.getLayoutParams();
            layoutParams.width = newLogoWidth;
            layoutParams.height = newLogoHeight;
            layoutParams.leftMargin = xOffset;
            layoutParams.topMargin = yOffset;
            imageView.setLayoutParams(layoutParams);
        }
    }

    public void refreshView() {
        if (TextUtils.isEmpty(img)) {
            return;
        }
        imageView.setVisibility(INVISIBLE);
        imageView.postDelayed(new Runnable() {
            @Override
            public void run() {
               adjustView();
            }
        }, 500);
    }

}
