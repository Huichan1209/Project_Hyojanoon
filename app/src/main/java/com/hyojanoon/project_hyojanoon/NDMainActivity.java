package com.hyojanoon.project_hyojanoon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class NDMainActivity extends AppCompatActivity {
    private LinearLayout study_Linear_main, camera_Linear_main, setting_Linear_main;
    private ImageView study_icon_main, camera_icon_main, setting_icon_main;
    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ndmain);
        killMainActivity();
        settingVar();
    }

    private void killMainActivity(){
        MainActivity mainActivity = (MainActivity)MainActivity.mainActivity;
        mainActivity.finish();
    }

    private void settingVar(){
        backPressCloseHandler = new BackPressCloseHandler(this);

        study_Linear_main = findViewById(R.id.study_Linear_main);
        camera_Linear_main = findViewById(R.id.camera_Linear_main);
        setting_Linear_main = findViewById(R.id.setting_Linear_main);

        study_icon_main = findViewById(R.id.study_icon_main);
        camera_icon_main = findViewById(R.id.camera_icon_main);
        setting_icon_main = findViewById(R.id.setting_icon_main);

        setListener();
    }

    private void setListener(){
        study_Linear_main.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), NDStudyActivity.class));
            overridePendingTransition(0, 0);
        });
        camera_Linear_main.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), NDCameraActivity.class));
            overridePendingTransition(0, 0);
        });
        setting_Linear_main.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), NDSettingActivity.class));
            overridePendingTransition(0, 0);
        });
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }


}
