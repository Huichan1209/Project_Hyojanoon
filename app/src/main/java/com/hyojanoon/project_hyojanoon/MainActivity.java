package com.hyojanoon.project_hyojanoon;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;


public class MainActivity extends AppCompatActivity {
    public static Activity mainActivity;
    LinearLayout mainLinear;
    TextToSpeech tts;
    static final int SEC = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity(new Intent(MainActivity.this, LoadingActivity.class));
        settingVars();
        //액티비티 전환 직후 tts를 호출하면 음성 출력이 안되는 버그가 있어서 객체 생성 후 0.5초간 기다림
        Handler dHandler = new Handler();
        dHandler.postDelayed(new Runnable() {
            @Override
                public void run() {
                SharedPreferences sf = getSharedPreferences("sFile", MODE_PRIVATE);
                if(sf.getBoolean("setting3", false))
                    startActivity(new Intent(getApplicationContext(), NDMainActivity.class));
                else
                    announce(TextToSpeech.QUEUE_FLUSH,
                        "안녕하세요 효자눈입니다. 효자눈은 스와이핑으로 조작할 수 있습니다. 학습모드는 왼쪽, 실습모드는 위쪽, 설정은 오른쪽입니다. "); //조작법 안내
            }
        },5*SEC);

    }

    private void settingVars(){
        mainActivity = MainActivity.this;

        initTTS();

        mainLinear = (LinearLayout) findViewById(R.id.mainLinear);
        mainLinear.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()){
            // 터치 시작점을 기준으로 끝점 좌표를 따짐
            // ex) 위 -> 아래로 스와이핑하면 Bottom이 호출됨

            @Override
            public void onSwipeTop() {
                super.onSwipeTop();
                announce(TextToSpeech.QUEUE_FLUSH, "");
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                announce(TextToSpeech.QUEUE_FLUSH, "");
                startActivity(new Intent(getApplicationContext(), settingActivity.class));
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                announce(TextToSpeech.QUEUE_FLUSH, "");
                startActivity(new Intent(getApplicationContext(), LearnActivity.class));
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
            }

            @Override
            public void onSwipeBottom() {
                super.onSwipeBottom();
                showMassage("시각장애인모드 실습모드는 업데이트 예정입니다. 비장애인모드에서 실행해주세요");
                /*
                announce(TextToSpeech.QUEUE_FLUSH, "");
                startActivity(new Intent(getApplicationContext(), TrainingActivity.class));
                overridePendingTransition(R.anim.anim_slide_in_top, R.anim.anim_slide_out_bottom);
                */
            }
        });
    }

    private void announce(int queueMode, String text){

        tts.speak(text, queueMode, null);
    }

    private void showMassage(String _text) {
        Toast.makeText(getApplicationContext(), _text, Toast.LENGTH_SHORT).show();
    }

    private void initTTS(){
        SharedPreferences sf = getSharedPreferences("sFile", MODE_PRIVATE);
        final Boolean[] settingBools = new Boolean[4];

        for(int i = 0; i < settingBools.length; i++){
            settingBools[i] = sf.getBoolean("setting" + (i+1), false);
        }

        //tts 객체 초기화
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR)
                    tts.setLanguage(Locale.KOREA);

                if(settingBools[0]) tts.setPitch(0.7f);
                else tts.setPitch(1);

                if(settingBools[1]) tts.setSpeechRate(0.7f);
                else tts.setSpeechRate(1);
            }
        });
    }

}
