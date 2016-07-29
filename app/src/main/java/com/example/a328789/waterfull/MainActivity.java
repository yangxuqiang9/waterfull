package com.example.a328789.waterfull;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private int currentPage;
    private int count;
    private List<String> imageFilenames;
    private int y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        int iamgeWhight = (getWindowManager().getDefaultDisplay().getWidth() - 4)/3;

        addImage(currentPage,count);
    }

    private void addImage(int curentPage,int count) {
        for (int x=currentPage*count;x<(curentPage+1)*count&&x<imageFilenames.size();x++){
            addBitMapToImage(imageFilenames.get(x),y,x);
        }
        y++;
        if(y>=3){
            y=0;
        }
    }

    private void addBitMapToImage(String imagePath, int y, int x) {

    }
}
