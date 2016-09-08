package xyz.yhsj.easynet.request;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

/**
 * 图片加载，可以随时替换加载工具
 * <p/>
 * Created by LOVE on 2016/04/18.
 */
public class ImageLoadUtils {
    public static void loadImage(Context context, ImageView imageView, String url) {
        if (!TextUtils.isEmpty(url)) {
            Glide
                    .with(context)
                    .load(url)
                    .dontAnimate()
//                    .error(R.mipmap.image_def_img)
//                    .placeholder(R.mipmap.image_def_img)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    //.override(600, 600)
                    .into(imageView);
        }
    }

    public static void loadImage(Context context, ImageView imageView, String url, BitmapTransformation bitmapTransformation) {
        if (!TextUtils.isEmpty(url)) {
            Glide
                    .with(context)
                    .load(url)
                    .dontAnimate()
                    .transform(bitmapTransformation)
//                    .error(R.mipmap.image_def_img)
//                    .placeholder(R.mipmap.image_def_img)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    //.override(600, 600)
                    .into(imageView);
        }
    }

    public static void loadGIF(Context context, ImageView imageView, int srcID) {
        if (srcID > 0) {
            Glide
                    .with(context)
                    .load(srcID)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    //.override(600, 600)
                    .into(new GlideDrawableImageViewTarget(imageView,1));
        }
    }


    /**
     * 获取视频缩略图
     *
     * @param videoPath 视频路径
     * @param width     图片宽度
     * @param height    图片高度
     * @param kind      eg:MediaStore.Video.Thumbnails.MICRO_KIND   MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return
     */

    public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }
}
