package com.example.administrator.zhixueproject.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by lyn on 2017/5/20.
 */

public class FileUtils {

    /**
     * 获取sd卡路径
     */
    public static String getSdcardPath() {
        String path = Environment.getExternalStorageDirectory() + File.separator + "zhixue" + File.separator;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static void mkdirsPath(String path) {
        File file = new File(path);
        if (file == null) {
            return;
        }
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static boolean fileExists(String path) {
        File file = new File(path);
        if (file == null) {
            return false;
        }
        return file.exists();
    }

    /**
     * 截取url的文件名作为本地存储的文件名
     */
    public static String getFileName(String path) {
        String fileName = "";
        if (TextUtils.isEmpty(path) || !path.contains("/")) {
            return fileName;
        }
        try {
            fileName = path.substring(path.lastIndexOf("/") + 1);
        } catch (Exception e) {
            fileName = "";
        }
        return fileName;
    }


    public static long getFileSizes(File f) throws Exception {
        long s = 0;
        if (f.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(f);
            s = fis.available();
        } else {
            f.createNewFile();
        }
        return s;
    }


    /**
     * 将图片的长和宽缩小味原来的1/2
     * @param imgPath
     * @return
     */
    public static Bitmap getBitMapBy2(String imgPath,int potions){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = potions;
        return BitmapFactory.decodeFile(imgPath,options);
    }

    /**
     * 对图片进行压缩
     * @param file
     * @return
     */
    public static String  compressBitMap(File file){
        //将图片缩小为原来的一半
        Bitmap bitmap=FileUtils.getBitMapBy2(file.getPath(), 2);
        //对图片进行压缩
        bitmap = FileUtils.compressImage(bitmap);
        String newPath=getSdcardPath()+System.currentTimeMillis()+"_"+(Math.random()*9+1)*1000+".jpg";
        try {
            file = new File(newPath);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();

            File file1=new File(newPath);
            LogUtils.e(FileUtils.getFileSize(file1.length())+"______");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(null!=bitmap){
            bitmap.recycle();
            bitmap=null;
        }
        return newPath;
    }


    public static File getFileByUri(Uri uri,Context context) {
        String path = null;
        if ("file".equals(uri.getScheme())) {
            path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = context.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA }, buff.toString(), null, null);
                int index = 0;
                int dataIdx = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    index = cur.getInt(index);
                    dataIdx = cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    path = cur.getString(dataIdx);
                }
                cur.close();
                if (index == 0) {
                } else {
                    Uri u = Uri.parse("content://media/external/images/media/" + index);
                    System.out.println("temp uri is :" + u);
                }
            }
            if (path != null) {
                return new File(path);
            }
        } else if ("content".equals(uri.getScheme())) {
            // 4.2.2以后
            String[] proj = { MediaStore.Images.Media.DATA };
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
            }
            cursor.close();

            return new File(path);
        } else {
            //Log.i(TAG, "Uri Scheme:" + uri.getScheme());
        }
        return null;
    }


    /**
     * 质量压缩方法
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 60;
        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于1000kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            options -= 10;// 每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }


    /**
     * 如果图片是content开头，就获取该图片地址
     * @param mContext
     * @param uri
     * @return
     */
    public static Uri getFileUri(Context mContext,Uri uri){
        try {
            if (uri.getScheme().equals("file")) {
                String path = uri.getEncodedPath();
                if (path != null) {
                    path = Uri.decode(path);
                    ContentResolver cr = mContext.getContentResolver();
                    StringBuffer buff = new StringBuffer();
                    buff.append("(")
                            .append(MediaStore.Images.ImageColumns.DATA)
                            .append("=")
                            .append("'" + path + "'")
                            .append(")");
                    Cursor cur = cr.query(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            new String[] { MediaStore.Images.ImageColumns._ID },
                            buff.toString(), null, null);
                    int index = 0;
                    for (cur.moveToFirst(); !cur.isAfterLast(); cur
                            .moveToNext()) {
                        index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                        index = cur.getInt(index);
                    }
                    if (index == 0) {
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
        }catch (Exception e){

        }
        return uri;
    }


    /**
     * 获取文件几M
     * @param fileS
     * @return
     */
    public static double getFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = df.format((double) fileS / 1048576);
        return Double.parseDouble(fileSizeString);
    }


    /**
     * 获取文件大小
     * @param fileS
     * @return
     */
    public static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }


    /**
     * 处理旋转后的图片
     * @param context 上下文
     * @return 返回修复完毕后的图片路径
     */
    public static String amendRotatePhoto(File file, Context context) {
        // 取得图片旋转角度
        int angle = readPictureDegree(file.getPath());
        // 把原图压缩后得到Bitmap对象
        Bitmap bmp = getCompressPhoto(file);
        // 修复图片被旋转的角度
        Bitmap bitmap = rotaingImageView(angle, bmp);
        String newPath=getSdcardPath()+System.currentTimeMillis()+"_"+(Math.random()*9+1)*1000+".jpg";
        try {
            file = new File(newPath);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();

            File file1=new File(newPath);
            LogUtils.e(FileUtils.getFileSize(file1.length())+"______");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(null!=bitmap){
            bitmap.recycle();
            bitmap=null;
        }
        return newPath;
    }


    //获取图片的旋转角度
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }


    /**
     * 对图片进行压缩
     * @param file
     * @return
     */
    public static Bitmap  getCompressPhoto(File file){
        //将图片缩小为原来的一半
        Bitmap bitmap=FileUtils.getBitMapBy2(file.getPath(), 2);
        //对图片进行压缩
        bitmap = FileUtils.compressImage(bitmap);

        return bitmap;
    }




    /**
     * 旋转图片
     * @param angle 被旋转角度
     * @param bitmap 图片对象
     * @return 旋转后的图片
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle();
        }
        return returnBm;
    }


    /**
     * 将图片的旋转角度置为0  ，此方法可以解决某些机型拍照后图像，出现了旋转情况
     *
     * @Title: setPictureDegreeZero
     * @param path
     * @return void
     * @date 2012-12-10 上午10:54:46
     */
    public static void setPictureDegreeZero(String path) {
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            // 修正图片的旋转角度，设置其不旋转。这里也可以设置其旋转的角度，可以传值过去，
            // 例如旋转90度，传值ExifInterface.ORIENTATION_ROTATE_90，需要将这个值转换为String类型的
            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, "no");
            exifInterface.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
