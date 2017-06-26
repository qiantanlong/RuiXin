/*
 * Copyright (c) 2017.
 */

package com.hongzhen.ruixin.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.hongzhen.ruixin.main.RXApplication;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 图片工具类
 * Created by yuhongzhen on 2017/6/14.
 */

public class ImageUtils {


    public static final int CROP_FROM_CAMERA = 32151;
    public static int GET_LOCAl_IMAGE_REQUEST_CODE = 0;


    /**
     * 跳转至相册
     */
    public static void toLocalImage(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(intent, GET_LOCAl_IMAGE_REQUEST_CODE);
    }




    /**
     * 压缩图片质量
     *
     * @param path      文图片路径
     * @param reqWidth  要求的宽
     * @param reqHeight 要求的高
     * @return 压缩质量后的bitmap
     */
    public static Bitmap decodeBitmapFromResource(String path, int reqWidth, int reqHeight) {
        Bitmap bitmap = null;
        //第一次解析将inJustDecodeBounds 设置为true,获取图片的大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(new FileInputStream(new File(path)), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //计算缩放比率calculateInSampleSize(options,reqWidth,reqHeight)
        // 使用获取到的inSampleSize值再次解析图片
        options.inSampleSize = 4;
        int orWidth = options.outWidth;
        int orHeight = options.outHeight;
        //缩放比率
        //计算缩放比率
        if (orWidth < reqWidth || reqHeight > orHeight) {
            float widthRatio = (float) orWidth / (float) reqWidth;
            float heightRatio = (float) orHeight / (float) reqHeight;
            options.inSampleSize = Math.max((int) widthRatio, (int) heightRatio);
        }
        options.inPreferredConfig = Bitmap.Config.ALPHA_8;
        /*
         ALPHA_8：每个像素占用1byte内存
         ARGB_4444:每个像素占用2byte内存
         ARGB_8888:每个像素占用4byte内存
         RGB_565:每个像素占用2byte内存
		 */
        options.inJustDecodeBounds = false;
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(new File(path)), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    public static Bitmap decodeBitmapFromResource(File file) {
        Bitmap bitmap = null;
        //第一次解析将inJustDecodeBounds 设置为true,获取图片的大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(new FileInputStream(file), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //计算缩放比率calculateInSampleSize(options,reqWidth,reqHeight)
        // 使用获取到的inSampleSize值再次解析图片
        options.inSampleSize = 4;
        int orWidth = options.outWidth;
        int orHeight = options.outHeight;
        //缩放比率
       /* //计算缩放比率
        if (orWidth < reqWidth || reqHeight > orHeight) {
            float widthRatio = (float) orWidth / (float) reqWidth;
            float heightRatio = (float) orHeight / (float) reqHeight;
            options.inSampleSize = Math.max((int) widthRatio, (int) heightRatio);
        }*/
        options.inPreferredConfig = Bitmap.Config.ALPHA_8;
        /*
         ALPHA_8：每个像素占用1byte内存
         ARGB_4444:每个像素占用2byte内存
         ARGB_8888:每个像素占用4byte内存
         RGB_565:每个像素占用2byte内存
		 */
        options.inJustDecodeBounds = false;
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    /**
     * 计算压缩比率
     *
     * @param options   图片
     * @param reqWidth  要求的宽度
     * @param reqHeight 要求的高度
     * @return 压缩比率
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        //图片原始宽高
        int orWidth = options.outWidth;
        int orHeight = options.outHeight;
        //缩放比率
        int inSampleSize = 4;
        //计算缩放比率
        if (orWidth < reqWidth || reqHeight > orHeight) {
            float widthRatio = (float) orWidth / (float) reqWidth;
            float heightRatio = (float) orHeight / (float) reqHeight;
            inSampleSize = Math.max((int) orWidth, (int) orHeight);
        }
        return inSampleSize;
    }


    /**
     * 以图片的宽为基准等比缩放图片
     *
     * @param bitmap
     * @param reW
     * @param @return bitmap 压缩后的Bitmap
     */
    public static Bitmap reSizeBitmap(Bitmap bitmap, int reW) {

        Bitmap orBitmap = bitmap;
        int orWidth = bitmap.getWidth();
        int orHeight = bitmap.getHeight();

        int newWidth = reW;
        //计算缩放比率
        float widthRatio = (float) newWidth / (float) orWidth;
        Matrix matrix = new Matrix();
        matrix.postScale(widthRatio, widthRatio);
        // 参数: 原始bitmap，x，y轴坐标（缩放的锚点）,原始的宽高,缩放模板,
        Bitmap resBitmap = orBitmap.createBitmap(orBitmap, 0, 0, orWidth, orHeight, matrix, true);
        return resBitmap;
    }
    //Bitmap对象保存味图片文件
    public static void saveBitmapToFile(Bitmap bitmap,String path,String name){
        File file=new File(path+name);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解决小米手机上获取图片路径为null的情况
     *
     * @param intent
     * @return
     */
    public static Uri geturi(android.content.Intent intent) {
        Uri uri = intent.getData();
        String type = intent.getType();
        if (uri.getScheme().equals("file") && (type.contains("image/"))) {
            String path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = RXApplication.getContext().getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=")
                        .append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.ImageColumns._ID},
                        buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    // set _id value
                    index = cur.getInt(index);
                }
                if (index == 0) {
                    // do nothing
                } else {
                    Uri uri_temp = Uri
                            .parse("content://media/external/images/media/"
                                    + index);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }
        return uri;
    }


    /**
     * 通过图片uri打开默认的第一个图片剪切
     */
    public static void toCrop(Uri imgUri, Activity activity) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        List<ResolveInfo> list = activity.getPackageManager().queryIntentActivities(
                intent, 0);
        int size = list.size();

        if (size == 0) {
            Toast.makeText(activity, "can't find crop app", Toast.LENGTH_SHORT)
                    .show();
            return;
        } else {
            intent.setData(imgUri);
            intent.putExtra("outputX", 300);
            intent.putExtra("outputY", 300);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);

            // only one
            Intent i = new Intent(intent);
            ResolveInfo res = list.get(0);
            i.setComponent(new ComponentName(res.activityInfo.packageName,
                    res.activityInfo.name));
            activity.startActivityForResult(i, CROP_FROM_CAMERA);
        }
    }
}
