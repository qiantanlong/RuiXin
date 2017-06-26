package com.hongzhen.ruixin.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hongzhen.ruixin.R;

/**
 * Created by yuhongzhen on 2017/6/14.
 */

public class GlideUtils {
    public static void setImageToImageView(Context context,ImageView imageView, String url) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.mipmap.default_useravatar)
                .error(R.mipmap.default_useravatar)
                .centerInside();
        Glide.with(context)
                .load(url)
                .apply(requestOptions)
                .into(imageView);
    }
}
