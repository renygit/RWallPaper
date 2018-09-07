package com.git.reny.wallpaper.utils.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.git.reny.wallpaper.R;

import java.io.File;


/**
 * Created by reny on 2017/8/11.
 */

public class GlideHelper {

    private static Drawable[] placeholderArr = new Drawable[3];//placeholder、error、fall

    private static Drawable getDrawable(Context context, @DrawableRes int imgId){
        return ContextCompat.getDrawable(context, imgId);
    }

    private static Drawable[] getPlaceholders(Context context, @DrawableRes int[] imgIds){
        if(null == placeholderArr[0]){
            placeholderArr[0] = getDrawable(context, R.mipmap.img_placeholder);
            placeholderArr[1] = getDrawable(context, R.mipmap.img_placeholder);
            placeholderArr[2] = getDrawable(context, R.mipmap.img_placeholder);
        }

        if(imgIds.length > 0) {
            Drawable[] placeholders = new Drawable[3];
            for (int i = 0; i < imgIds.length; i++) {
                placeholders[i] = getDrawable(context, imgIds[i]);
                if (i == 2) {
                    break;//跳出循环 最多取0 1 2
                }
            }
            if(null == placeholders[1]){
                placeholders[1] = placeholders[0];
            }
            if(null == placeholders[2]){
                placeholders[2] = placeholders[0];
            }
            return placeholders;
        }
        return placeholderArr;
    }

    public static void setBgBitmap(View view, Bitmap bitmap){
        BitmapDrawable bd = new BitmapDrawable(view.getContext().getResources(), bitmap);
        setBgDrawable(view, bd);
    }

    public static void setBgDrawable(View view, Drawable drawable){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        }else {
            view.setBackgroundDrawable(drawable);
        }
    }

    /***
     * 常用显示图片的方法
     * @param view 要显示图片的view 支持ImageView 和 其它View,其它view会设置到背景
     * @param model 图片地址
     * @param imgIds 缺省参数，可设置占位图，加载失败图，加载为空图 不传有默认值
     */
    public static void display(@NonNull View view, Object model, @DrawableRes int... imgIds){
        try {
            Drawable[] placeholders = getPlaceholders(view.getContext(), imgIds);

            if (view instanceof ImageView) {
                GlideApp.with(view).load(model).placeholder(placeholders[0]).error(placeholders[1]).fallback(placeholders[2]).into((ImageView) view);
            } else {
                GlideApp.with(view).asBitmap().load(model).placeholder(placeholders[0]).error(placeholders[1]).fallback(placeholders[2]).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        setBgBitmap(view, resource);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        setBgDrawable(view, errorDrawable);
                    }
                });
            }
        }catch (Exception e){e.printStackTrace();}
    }

    public static void disPlayRound(@NonNull ImageView view, Object model, int radiusDp, boolean isCenterCrop, @DrawableRes int... imgIds){
        try {
            Drawable[] placeholders = getPlaceholders(view.getContext(), imgIds);

            if(isCenterCrop){
                GlideApp.with(view).load(model).placeholder(placeholders[0]).error(placeholders[1]).fallback(placeholders[2]).transforms(new CenterCrop(), new RoundedCorners(radiusDp)).into(view);
            }else {
                GlideApp.with(view).load(model).placeholder(placeholders[0]).error(placeholders[1]).fallback(placeholders[2]).transform(new RoundedCorners(radiusDp)).into(view);
            }
        }catch (Exception e){e.printStackTrace();}
    }

    //提前调用过downloadOnly  再使用此方法
    public static void displayPreLoad(@NonNull View view, Object model, @DrawableRes int... imgIds){
        try {
            Drawable[] placeholders = getPlaceholders(view.getContext(), imgIds);

            if (view instanceof ImageView) {
                GlideApp.with(view).load(model).placeholder(placeholders[0]).error(placeholders[1]).fallback(placeholders[2]).onlyRetrieveFromCache(true).into((ImageView) view);
            } else {
                GlideApp.with(view).asBitmap().load(model).placeholder(placeholders[0]).error(placeholders[1]).fallback(placeholders[2]).onlyRetrieveFromCache(true).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        setBgBitmap(view, resource);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        setBgDrawable(view, errorDrawable);
                    }
                });
            }
        }catch (Exception e){e.printStackTrace();}
    }

    //从网络获取一个Bitmap
    public static void loadBitmap(@NonNull Context context, Object model, @NonNull final LoadBitmapCallBack callBack) {
        try {
            GlideApp.with(context).asBitmap().load(model).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                    callBack.onSuccess(resource);
                }

                @Override
                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                    callBack.onFailed(errorDrawable);
                }
            });
        }catch (Exception e){e.printStackTrace();}
    }


    //缓存到磁盘中 非内存中
    public static void downloadOnly(@NonNull Context context, Object model, int... widthHeight){
        try {
            if(widthHeight.length == 0) {
                GlideApp.with(context).download(model).preload();
            }else if(widthHeight.length == 1){
                GlideApp.with(context).download(model).preload(widthHeight[0], widthHeight[0]);
            }else{
                GlideApp.with(context).download(model).preload(widthHeight[0], widthHeight[1]);
            }
        }catch (Exception e){e.printStackTrace();}
    }


    /***
     * 下载图片
     * @param context 上下文
     * @param model 图片地址
     * @param dir 要保存的文件夹
     * @param imgName 要保存的图片名 可以加后缀.jpg .png 也可以不加，不加会保存为.jpg
     * @param callBack 回调
     */
    public static void downLoadImg(@NonNull Context context, final Object model, @NonNull final String dir, @NonNull final String imgName, final DownLoadCallBack callBack){
        try {
            GlideThreadTask.addTask(() -> GlideApp.with(context).download(model).into(new SimpleTarget<File>() {
                @Override
                public void onResourceReady(File resource, Transition<? super File> transition) {
                    GlideHelperUtils.saveImgToSDCard(resource, dir, imgName, callBack);
                }

                @Override
                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                    if (null != callBack) {
                        callBack.onFailed("图片下载失败");
                    }
                }
            }));
        }catch (Exception e){e.printStackTrace();}
    }

    public static void gcBitmap(Bitmap bitmap) {
        if(null != bitmap && !bitmap.isRecycled()){
            // 回收并且置为null
            bitmap.recycle();
            bitmap = null;
            System.gc();
        }
    }

    public static void pauseRequests(@NonNull Context context) {
        GlideApp.with(context).pauseRequests();
    }

    public static void resumeRequests(@NonNull Context context) {
        GlideApp.with(context).resumeRequests();
    }

    public static void clearMemory(@NonNull Context context) {
        Glide.get(context).clearMemory();
    }

    public static void clearDiskCache(@NonNull final Context context) {
        GlideThreadTask.addTask(() -> {
            // This method must be called on a background thread.
            Glide.get(context).clearDiskCache();
        });
    }

    public static void cleanAll(@NonNull Context context) {
        clearDiskCache(context);
        clearMemory(context);
    }


    public interface LoadBitmapCallBack {
        void onSuccess(Bitmap bitmap);
        void onFailed(Drawable error);
    }

    public interface DownLoadCallBack {
        void onSuccess(File file);
        void onFailed(String err);
    }

}
