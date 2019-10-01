package com.hyojanoon.project_hyojanoon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class NDSettingActivity extends AppCompatActivity {
    private LinearLayout study_Linear_setting, camera_Linear_setting, setting_Linear_setting;
    private ImageView study_icon_setting, camera_icon_setting, setting_icon_setting, setting_text;
    private ImageView tts_ton_setBtn_1, tts_ton_setBtn_2, tts_speed_setBtn_1, tts_speed_setBtn_2, mode_setBtn_1, mode_setBtn_2;
    private boolean currentSettings[];
    private ImageView btns[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ndsetting);
        settingVar();
        setting_icon_setting.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        setting_text.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
    }

    private void initSettings(){
        SharedPreferences sf = getSharedPreferences("sFile", MODE_PRIVATE);
        currentSettings[0] = sf.getBoolean("setting1", false);
        currentSettings[2] = sf.getBoolean("setting2", false);
        currentSettings[4] = sf.getBoolean("setting3", false);

        if(!currentSettings[0]) updateSetting(0);
        else updateSetting(1);
        if(!currentSettings[2]) updateSetting(2);
        else updateSetting(3);
        if(!currentSettings[4]) updateSetting(4);
        else updateSetting(5);

    }

    private void updateSetting(int index){
        Log.d("설정 ", index+"");
        switch (index){
            case 0:
                tts_ton_setBtn_1.setImageResource(R.drawable.shvrp_white);
                tts_ton_setBtn_2.setImageResource(R.drawable.skwrp_orange);
                currentSettings[index] = false; currentSettings[index+1] = true;
                break;
            case 1:
                tts_ton_setBtn_2.setImageResource(R.drawable.skwrp_white);
                tts_ton_setBtn_1.setImageResource(R.drawable.shvrp_orange);
                currentSettings[index] = false; currentSettings[index-1] = true;
                break;
            case 2:
                tts_speed_setBtn_1.setImageResource(R.drawable.qkfma_white);
                tts_speed_setBtn_2.setImageResource(R.drawable.smfla_orange);
                currentSettings[index] = false; currentSettings[index+1] = true;
                break;
            case 3:
                tts_speed_setBtn_2.setImageResource(R.drawable.smfla_white);
                tts_speed_setBtn_1.setImageResource(R.drawable.qkfma_orange);
                currentSettings[index] = false; currentSettings[index-1] = true;
                break;
            case 4:
                //장애인/비장애인모드는 메인에서 검사하는 설정 값이 반대임
                mode_setBtn_1.setImageResource(R.drawable.wkddodls_white);
                mode_setBtn_2.setImageResource(R.drawable.qlwkddodls_orange);
                currentSettings[index] = false; currentSettings[index+1] = true;
                break;
            case 5:
                mode_setBtn_2.setImageResource(R.drawable.qlwkddodls_white);
                mode_setBtn_1.setImageResource(R.drawable.wkddodls_orange);
                currentSettings[index] = false; currentSettings[index-1] = true;
                break;
        }
    }

    private void setBtns(){
        tts_ton_setBtn_1 = findViewById(R.id.tts_ton_setBtn_1);
        tts_ton_setBtn_2 = findViewById(R.id.tts_ton_setBtn_2);
        tts_speed_setBtn_1 = findViewById(R.id.tts_speed_setBtn_1);
        tts_speed_setBtn_2 = findViewById(R.id.tts_speed_setBtn_2);
        mode_setBtn_1 = findViewById(R.id.mode_setBtn_1);
        mode_setBtn_2 = findViewById(R.id.mode_setBtn_2);
    }

    private void setBtnsListenr(){
        tts_ton_setBtn_1.setOnClickListener(v -> updateSetting(0));

        tts_ton_setBtn_2.setOnClickListener(v -> updateSetting(1));

        tts_speed_setBtn_1.setOnClickListener(v -> updateSetting(2));

        tts_speed_setBtn_2.setOnClickListener(v -> updateSetting(3));

        mode_setBtn_1.setOnClickListener(v -> {
            updateSetting(4);
            saveSettings();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        });

        mode_setBtn_2.setOnClickListener(v -> updateSetting(5));
    }

    private void settingVar(){
        currentSettings = new boolean[6];
        for (int i=0; i<currentSettings.length; i++)
            currentSettings[i] = false;
        setBtns();
        initSettings();

        study_Linear_setting = findViewById(R.id.study_Linear_setting);
        camera_Linear_setting = findViewById(R.id.camera_Linear_setting);
        setting_Linear_setting = findViewById(R.id.setting_Linear_setting);

        study_icon_setting = findViewById(R.id.study_icon_setting);
        camera_icon_setting = findViewById(R.id.camera_icon_setting);
        setting_icon_setting = findViewById(R.id.setting_icon_setting);

        setting_text = findViewById(R.id.setting_text);
        setListener();
    }

    private void setListener(){
        setBtnsListenr();
        study_Linear_setting.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), NDStudyActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        camera_Linear_setting.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), NDCameraActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        setting_Linear_setting.setOnClickListener(v -> {

        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), NDMainActivity.class));
        overridePendingTransition(0,0);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveSettings();
    }

    private void saveSettings(){
        //SharedPreferences를 sFile이름, 기본모드로 설정
        SharedPreferences sharedPreferences = getSharedPreferences("sFile", MODE_PRIVATE);

        //저장을 하기 위해 에디터로 값을 저장
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //설정값을 setting + 번호 키로 저장
        editor.putBoolean("setting1", currentSettings[0]);
        editor.putBoolean("setting2", currentSettings[2]);
        editor.putBoolean("setting3", currentSettings[4]);
        editor.commit();
    }
}
