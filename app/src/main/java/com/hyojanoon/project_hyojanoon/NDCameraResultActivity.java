package com.hyojanoon.project_hyojanoon;

import android.content.Intent;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;

public class NDCameraResultActivity extends AppCompatActivity {
    private ImageView result_type_text1, result_type_text2;
    private TextView resultTv;
    private TextToSpeech tts;
    private final int SEC = 1000;
    private String detection_type;
    private static String subedResult = "";
    private static String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ndcamera_result);
        settingVars();

        Intent intent = getIntent();

        String result = intent.getExtras().getString("result");

        for(int i=0; i<result.length(); i++){
            if( Character.isUpperCase( result.charAt(i) ) )
                subedResult = result.substring(i);
        }
        Log.d("결과 ", result+subedResult);
        resultTv.setText(subedResult);
        detection_type = intent.getExtras().getString("detection_type");
        Log.d("인식 타입 ", detection_type);

        if(detection_type.equals("LABEL_DETECTION")) {result_type_text1.setImageResource(R.drawable.dlstlrehlstkanf_text); type = "사물은";}
        if(detection_type.equals("TEXT_DETECTION")) {result_type_text1.setImageResource(R.drawable.dlstlrehlsrmfwk_text); type = "글자는";}

        Handler dHandler = new Handler();
        dHandler.postDelayed( () -> {
            announce(type.substring(0, 2)+" 인식 결과, 이 "+ type + subedResult +"으로 보입니다");
        },1*SEC);
    }

    private void initTTS(){
        tts = new TextToSpeech(getApplicationContext(), status -> {
                if(status != ERROR)
                    tts.setLanguage(Locale.KOREA);
        });
    }

    private void settingVars(){
        initTTS();
        resultTv = findViewById(R.id.result_text);
        result_type_text1 = findViewById(R.id.result_type_text1);
        result_type_text2 = findViewById(R.id.result_type_text2);
        result_type_text2.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), NDMainActivity.class));
        overridePendingTransition(0,0);
        finish();
    }

    private void announce(String text){
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.stop();
    }
}
