package com.hyojanoon.project_hyojanoon;

import android.content.SharedPreferences;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;

public class WordLearnActivity extends AppCompatActivity {
    ImageView[] brailles;
    TextView tvChar;
    TextToSpeech tts;
    LinearLayout wordLearnLinear;
    Braille braille;
    int currentChar = 0;
    static final int SEC = 1000;
    boolean isFirst = true;
    String[] chars =
            {"가", "나", "다", "마",
             "바", "사", "아", "자",
             "카", "타", "파", "하", "것",
             "ㅆ 받침", "억", "언", "얼", "열",
             "영", "옥", "온", "옹", "운",
             "울", "은", "을", "인", "그래서",
             "그러나", "그러면", "그러므로",
             "그런데", "그리고", "그리하여"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_learn);
        settingVars();

        //액티비티 전환 직후 tts를 호출하면 음성 출력이 안되는 버그가 있어서 객체 생성 후 0.5초간 기다림
        Handler dHandler = new Handler();
        dHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateChange();
            }
        },SEC/2);
    }

    private void settingVars(){
        //점자 버튼6개 등록
        brailles = new ImageView[12];
        brailles[0] = (ImageView) findViewById(R.id.word_learn_braille1);
        brailles[1] = (ImageView) findViewById(R.id.word_learn_braille2);
        brailles[2] = (ImageView) findViewById(R.id.word_learn_braille3);
        brailles[3] = (ImageView) findViewById(R.id.word_learn_braille4);
        brailles[4] = (ImageView) findViewById(R.id.word_learn_braille5);
        brailles[5] = (ImageView) findViewById(R.id.word_learn_braille6);
        brailles[6] = (ImageView) findViewById(R.id.word_learn_braille7);
        brailles[7] = (ImageView) findViewById(R.id.word_learn_braille8);
        brailles[8] = (ImageView) findViewById(R.id.word_learn_braille9);
        brailles[9] = (ImageView) findViewById(R.id.word_learn_braille10);
        brailles[10] = (ImageView) findViewById(R.id.word_learn_braille11);
        brailles[11] = (ImageView) findViewById(R.id.word_learn_braille12);

        //tts 객체 초기화
        initTTS();

        braille = new Braille();

        wordLearnLinear = (LinearLayout) findViewById(R.id.wordLearnLinear);
        wordLearnLinear.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()){

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                if(currentChar>0){
                    currentChar--;
                    updateChange();
                }
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                if(currentChar<chars.length-1){
                    currentChar++;
                    updateChange();
                }
            }
        });

        tvChar = (TextView) findViewById(R.id.tvChar_word);
    }

    private void updateChange(){

        //UI상의 점자 핀 초기화
        for(int i=0; i<brailles.length; i++)
            brailles[i].setImageResource(R.drawable.empty_braille);

        //자작 점자 클래스로 현재 문자(초성)을 보내서 점자 코드로 변환
        char brailleCode = braille.getBrailleCode(Braille.WORD, chars[currentChar]);
        //반복문을 위한 기준값

        char benchmark;
        if(chars[currentChar] == "것" || chars[currentChar] == "그래서" || chars[currentChar] == "그러나" || chars[currentChar] == "그러면" || chars[currentChar] == "그러므로" || chars[currentChar] == "그런데" || chars[currentChar] == "그리고" || chars[currentChar] == "그리하여")
            benchmark = 0b100000000000;
        else
            benchmark = 0b100000;

        //비트 연산을 통해 UI상의 점자핀에 점자 코드를 반영
        for(int i=0;i<brailles.length; i++)
            if((brailleCode & benchmark>>i) != 0)
                brailles[i].setImageResource(R.drawable.filled_braille);

        /*
            위의 for문과 같음.
            if((brailleCode & 0b100000) != 0) brailles[0].setImageResource(R.drawable.filled_braille);
            if((brailleCode & 0b010000) != 0) brailles[1].setImageResource(R.drawable.filled_braille);
            if((brailleCode & 0b001000) != 0) brailles[2].setImageResource(R.drawable.filled_braille);
            if((brailleCode & 0b000100) != 0) brailles[3].setImageResource(R.drawable.filled_braille);
            if((brailleCode & 0b000010) != 0) brailles[4].setImageResource(R.drawable.filled_braille);
            if((brailleCode & 0b000001) != 0) brailles[5].setImageResource(R.drawable.filled_braille);
        */


        announce(TextToSpeech.QUEUE_FLUSH, "이 점자는 " + (chars[currentChar])
                + ". " + (chars[currentChar]) + "입니다.");

        if(isFirst) {
            announce(TextToSpeech.QUEUE_ADD, "다음 점자는 오른쪽으로, 이전 점자는 왼쪽으로 스와이핑 해주세요");
            isFirst = false;
        }

        tvChar.setText((chars[currentChar]));
    }

    private void announce(int queueMode, String text){
        tts.speak(text, queueMode, null);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tts != null){
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }
}
