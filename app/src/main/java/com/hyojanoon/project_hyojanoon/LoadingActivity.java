package com.hyojanoon.project_hyojanoon;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

public class LoadingActivity extends AppCompatActivity {
    static final int SEC = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        ImageView loadingGIF = (ImageView) findViewById(R.id.loadingGIF);
        GlideDrawableImageViewTarget hyojanoonLogo = new GlideDrawableImageViewTarget(loadingGIF);
        Glide.with(this).load(R.drawable.hyojanoon_logo).into(hyojanoonLogo);
        startLoading();
    }

    private void startLoading(){
        Handler myHandler = new Handler();
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                overridePendingTransition(0, 0);
            }
        }, 5*SEC);
    }

    @Override
    public void onBackPressed() {
    }
}
