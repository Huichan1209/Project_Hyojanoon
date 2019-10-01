package com.hyojanoon.project_hyojanoon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;

public class settingActivity extends AppCompatActivity {
    Button[] settingBtns;
    boolean[] isClickeds;
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        settingVars();
    }

    private void settingVars(){
        settingBtns = new Button[4];
        isClickeds = new boolean[settingBtns.length];
        settingBtns[0] = findViewById(R.id.settingBtn1);
        settingBtns[1] = findViewById(R.id.settingBtn2);
        settingBtns[2] = findViewById(R.id.settingBtn3);
        settingBtns[3] = findViewById(R.id.settingBtn4);

        for (int i=0; i<isClickeds.length; i++) isClickeds[i] = false;

        initTTS();

        settingBtns[0].setOnLongClickListener(v -> {
                isClickeds[0] = !isClickeds[0];
                changeVoice(isClickeds[0]);
                if(isClickeds[0]) settingBtns[0].setBackgroundColor(Color.YELLOW);
                else settingBtns[0].setBackgroundColor(Color.WHITE);
                return true;
        });

        settingBtns[1].setOnLongClickListener( v -> {
                isClickeds[1] = !isClickeds[1];
                slowVoice(isClickeds[1]);
                if(isClickeds[1]) settingBtns[1].setBackgroundColor(Color.YELLOW);
                else settingBtns[1].setBackgroundColor(Color.WHITE);
                return true;
        });

        settingBtns[2].setOnLongClickListener( v -> {
                isClickeds[2] = !isClickeds[2];
                if(isClickeds[2]){
                    settingBtns[2].setBackgroundColor(Color.YELLOW);
                    killMainActivity();
                    saveSettings();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
                else settingBtns[2].setBackgroundColor(Color.WHITE);
                return true;
        });

        settingBtns[3].setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isClickeds[3] = !isClickeds[3];
                if(isClickeds[3]) settingBtns[3].setBackgroundColor(Color.YELLOW);
                else settingBtns[3].setBackgroundColor(Color.WHITE);
                return true;
            }
        });

        settingBtns[0].setOnClickListener(v -> tts.speak("음성 톤 낮추기 입니다. 길게 눌러주세요", TextToSpeech.QUEUE_FLUSH, null));
        settingBtns[1].setOnClickListener(v -> tts.speak("음성 속도 낮추기 입니다. 길게 눌러주세요", TextToSpeech.QUEUE_FLUSH, null));
        settingBtns[2].setOnClickListener(v -> tts.speak("비장애인 모드 켜기입니다. 길게 누르시면 앱을 재시작합니다.", TextToSpeech.QUEUE_FLUSH, null));
        settingBtns[3].setOnClickListener(v -> tts.speak("미정입니다. 길게 눌러주세요", TextToSpeech.QUEUE_FLUSH, null));
    }

    private void initTTS(){
        SharedPreferences sf = getSharedPreferences("sFile", MODE_PRIVATE);

        for(int i = 0; i < isClickeds.length; i++){
            isClickeds[i] = sf.getBoolean("setting" + (i+1), false);
        }

        //tts 객체 초기화
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR)
                    tts.setLanguage(Locale.KOREA);

                if(isClickeds[0]) tts.setPitch(0.7f);
                else tts.setPitch(1);

                if(isClickeds[1]) tts.setSpeechRate(0.7f);
                else tts.setSpeechRate(1);
            }
        });


        for(int i = 0; i < isClickeds.length; i++){
            if(isClickeds[i]) settingBtns[i].setBackgroundColor(Color.YELLOW);
            else settingBtns[i].setBackgroundColor(Color.WHITE);
        }



    }

    private void changeVoice(boolean isActive){
        if(isActive) tts.setPitch(0.7f);
        else tts.setPitch(1);

        tts.speak("변경되었습니다.", TextToSpeech.QUEUE_FLUSH, null);
    }

    private void slowVoice(boolean isActive){
        if(isActive) tts.setSpeechRate(0.7f);
        else tts.setSpeechRate(1);

        tts.speak("변경되었습니다.", TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveSettings();
    }

    private void saveSettings()
    {
        //Activity가 종료되기 전에 설정값 저장
        //SharedPreferences를 sFile이름, 기본모드로 설정
        SharedPreferences sharedPreferences = getSharedPreferences("sFile", MODE_PRIVATE);

        //저장을 하기 위해 에디터로 값을 저장
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //설정값을 setting + 번호 키로 저장
        for(int i=0; i<isClickeds.length; i++) {
            editor.putBoolean("setting" + (i + 1), isClickeds[i]);
        }
        editor.commit();
    }

    private void killMainActivity(){
        MainActivity mainActivity = (MainActivity)MainActivity.mainActivity;
        mainActivity.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.stop();
    }
}
