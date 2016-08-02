package com.example.a328789.waterfull.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by 328789 on 2016/8/2.
 */
public class Utils {

    private static String path;
    private static Toast toast;

    /**
     * 获取本地缓存路径
     */
    public static File getDeskCacheFile(Context context, String fileName){
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            //android官方认定的外部SD卡缓存路径，应用卸载之后会随之清空
            path = context.getExternalCacheDir().getPath();
        }else {
            path = context.getCacheDir().getPath();
        }
        File file = new File(path + fileName);
        if(!file.exists()){
            file.mkdir();
        }
        return file;
    }
    // 拿到App版本号
    public static int getAppVersion(Context context) {

        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }
    /**
     * 对key进行md5加密
     */
    public static String getDiskUrl(String url){
        String key = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(url.getBytes());
            byte[] digest = md5.digest();
            // 将byte数组转换成16进制字符串
            key = bytesToHexString(digest);
        } catch (NoSuchAlgorithmException e) {
            key = String.valueOf(key.hashCode());
            e.printStackTrace();
        }
        return key;
    }

    private static String bytesToHexString(byte[] digest) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            // 将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
            String hexString = Integer.toHexString(0xff & digest[i]);
            // 每个字节8位，转为16进制标志，2个16进制位
            if (hexString.length() == 1) {
                sb.append(0);
            }
            sb.append(hexString);
        }

        return sb.toString();
    }

    /**
     *  建立HTTP请求，获取图片
     *
     */
    public static boolean downloadUrlToStream(String urlString,
                                              OutputStream outputStream) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            if(urlConnection.getResponseCode()==200){
                in = new BufferedInputStream(urlConnection.getInputStream(),
                        8 * 1024);
                out = new BufferedOutputStream(outputStream, 8 * 1024);
                int b;
                while ((b = in.read()) != -1) {
                    out.write(b);
                }
                return true;
            }
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    /**
     * 压缩图片
     *
     */
    public static Bitmap compressBitmap(FileDescriptor fileDescriptor,int width){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        if(options.outWidth>width){
            options.inSampleSize=options.outWidth / width;
        }else {
            options.inSampleSize=1;
        }
        options.inJustDecodeBounds=false;
        return BitmapFactory.decodeFileDescriptor(fileDescriptor,null,options);
    }
    /**
     * 封装的toast
     */
    public static void myToast(Context context,String s){
        if (toast==null) {
            toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
//            toast.show();
        }else {
            toast.setText(s);
        }
        toast.show();
    }
}
