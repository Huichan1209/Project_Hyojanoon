package com.hyojanoon.project_hyojanoon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

public class LearnActivity extends AppCompatActivity {
    LinearLayout learnLinear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        settingVars();
    }

    private void settingVars(){
        learnLinear = (LinearLayout) findViewById(R.id.learnLinear);
        learnLinear.setOnTouchListener(new OnSwipeTouchListener(LearnActivity.this){
            //터치 시작점과 끝점 좌표를 비교하기때문에 반대라서 편의상 상, 하, 좌, 우 순서로 오버라이드

            @Override
            public void onSwipeBottom() {
                super.onSwipeBottom();
                startActivity(new Intent(getApplicationContext(), FirstLearnActivity.class));
                overridePendingTransition(R.anim.anim_slide_in_top, R.anim.anim_slide_out_bottom);
            }

            @Override
            public void onSwipeTop() {
                super.onSwipeTop();
                startActivity(new Intent(getApplicationContext(), WordLearnActivity.class));
                overridePendingTransition(R.anim.anim_slide_in_bottom, R.anim.anim_slide_out_top);
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                startActivity(new Intent(getApplicationContext(), SecondLearnActivity.class));
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                startActivity(new Intent(getApplicationContext(), LastLearnActivity.class));
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            }
        });
    }

    private void showMassage(String _text)
    {
        Toast.makeText(getApplicationContext(), _text, Toast.LENGTH_SHORT).show();
    }

}
