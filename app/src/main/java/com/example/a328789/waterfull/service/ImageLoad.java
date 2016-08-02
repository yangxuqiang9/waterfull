package com.example.a328789.waterfull.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.LruCache;

import com.example.a328789.waterfull.utils.DiskLruCache;
import com.example.a328789.waterfull.utils.Utils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by 328789 on 2016/8/2.
 */
public class ImageLoad {
    private static ImageLoad imageLoad;
    private LruCache lruCache;
    private  DiskLruCache mDiskCache;
    private FileDescriptor fd;
    private Bitmap bitmap;

    private ImageLoad(Context context){
        int cacheMemory = (int) (Runtime.getRuntime().maxMemory() / 8);
        lruCache = new LruCache<String,Bitmap>(cacheMemory){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return super.sizeOf(key, value);
            }
        };

        try {
            //获取本地缓存路径
            File deskcache = Utils.getDeskCacheFile(context, "deskcache");
            //获取版本号
            int appVersion = Utils.getAppVersion(context);
            mDiskCache = DiskLruCache.open(deskcache, appVersion, 1, 20 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static ImageLoad getInstace(Context context){
        if(imageLoad==null){
            synchronized (ImageLoad.class){
                if(imageLoad==null){
                    imageLoad=new ImageLoad(context);
                }
            }
        }
        return imageLoad;
    }
    public Bitmap loadImage(String url,int columnWidth){
        //先从内存中获取图片
        Bitmap bitmap = getMemoryCache(url);
        if(bitmap==null){
            //从本地缓存中获取图片
            bitmap= getBitmapForDisk(url,columnWidth);
        }
        return bitmap;
    }
    /**
     *  从本地缓存获取图片
     * @return
     */
    private Bitmap getBitmapForDisk(String url, int columnWidth) {
        //对url进行md5加密,用作缓存文件的名称
        String diskUrl = Utils.getDiskUrl(url);
        try {
            DiskLruCache.Snapshot snapshot = mDiskCache.get(diskUrl);
            if(snapshot==null){
                DiskLruCache.Editor edit = mDiskCache.edit(diskUrl);
                OutputStream outputStream = edit.newOutputStream(0);
                boolean b = Utils.downloadUrlToStream(url, outputStream);
                if(b){
                    edit.commit();
                }else {
                    edit.abort();
                }
                snapshot=mDiskCache.get(diskUrl);
            }
            if(snapshot!=null){
                FileInputStream inputStream = (FileInputStream)snapshot.getInputStream(0);
                fd = inputStream.getFD();
            }
            //TODO 压缩bitmap
            if(fd!=null){
                bitmap = Utils.compressBitmap(fd, columnWidth);
            }
            if(bitmap!=null){
                //加入到内存缓存中
                addMemoryCache(url,bitmap);
            }
            return bitmap;

        } catch (IOException e) {
            e.printStackTrace();
        }finally {

        }
        return null;
    }

    private Bitmap getMemoryCache(String url) {
        if(url!=null){
            Bitmap bitmap = (Bitmap) lruCache.get(url);
            return bitmap;
        }
        return null;
    }
    private void addMemoryCache(String url,Bitmap b) {
        if(getMemoryCache(url)==null){
           lruCache.put(url,b);
        }
    }
}
