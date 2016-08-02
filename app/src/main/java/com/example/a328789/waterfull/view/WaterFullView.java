package com.example.a328789.waterfull.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.a328789.waterfull.R;
import com.example.a328789.waterfull.bean.ImageBean;
import com.example.a328789.waterfull.service.ImageLoad;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by 328789 on 2016/7/29.
 */
public class WaterFullView extends ScrollView{

    private Set<LoadImageTask> taskSet;
    private int columnWidth;
    private boolean isOneLoad;
    private int pager;
    private int pagerSize=20;
    private String[] imageUrls;
    private ImageLoad imageLoad;
    private List<ImageView> iamgeViewList=new ArrayList<>();
    private int fristColumnHeight;
    private int secondColumnHerght;
    private LinearLayout fristColumn;
    private LinearLayout secondColumn;

    public WaterFullView(Context context) {
        super(context);
        init(context);
    }

    public WaterFullView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WaterFullView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化操作
     * @param context
     */
    private void init(Context context) {
        imageLoad = ImageLoad.getInstace(context);
        taskSet = new HashSet<>();
        imageUrls = ImageBean.images;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(!isOneLoad){
            isOneLoad=true;
            fristColumn = (LinearLayout) findViewById(R.id.frist_column);
            secondColumn = (LinearLayout) findViewById(R.id.second_column);
            //获取单列宽
            columnWidth = fristColumn.getWidth();

            //加载图片
            loadImage();
        }
    }

    /**
     * 加载图片
     */
    private void loadImage() {
        int startIndex=pager*pagerSize;
        int endIndex=(pager+1)*pagerSize-1;
        if(imageUrls.length>=startIndex){
            if(endIndex>imageUrls.length){
                endIndex=imageUrls.length;
            }
            for (int i=startIndex;i<=endIndex;i++){
                //开启异步线程加载图片
                LoadImageTask loadImageTask = new LoadImageTask();
                taskSet.add(loadImageTask);
                loadImageTask.execute(imageUrls[i]);
            }
            pager++;
        }else {
            Toast.makeText(getContext(),"图片加载完了",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 加载图片的异步线程
     */
    class LoadImageTask extends AsyncTask<String,Void,Bitmap>{

        private String url;

        @Override
        protected Bitmap doInBackground(String... params) {
            //先从缓存中获取图片,获取不到是从网络加载
            url = params[0];
            return imageLoad.loadImage(url,columnWidth);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap!=null){
                double scale = bitmap.getWidth() / (columnWidth * 1.0);
                int scaleHeight= (int) (bitmap.getHeight()/scale);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(columnWidth, scaleHeight);
                ImageView imageView = new ImageView(getContext());
                imageView.setLayoutParams(layoutParams);
                imageView.setImageBitmap(bitmap);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setPadding(5,5,5,5);
//                imageView.setTag(0,url);
                //放倒最短的一列中
                findShortColumn(scaleHeight,imageView).addView(imageView);
                iamgeViewList.add(imageView);
            }
            taskSet.remove(this);
        }

        /**
         * 找到最短的那一列
         * @param scaleHeight
         * @param imageView
         * @return
         */
        private LinearLayout findShortColumn(int scaleHeight, ImageView imageView) {
            if(fristColumnHeight<=secondColumnHerght){
                fristColumnHeight+=scaleHeight;
                return fristColumn;
            }else {
                secondColumnHerght+=scaleHeight;
                return secondColumn;
            }
        }
    }
}
