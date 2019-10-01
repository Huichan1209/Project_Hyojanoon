package com.hyojanoon.project_hyojanoon;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

public class NDStudyActivity extends AppCompatActivity {
    private ImageView study_icon_study, camera_icon_study, setting_icon_study, study_text;
    private LinearLayout study_Linear_study, camera_Linear_study, setting_Linear_study;
    private ImageView[][] dots;
    private ImageView bar1, bar2;
    private EditText edt;
    public NDBraille ndBraille;
    final int TOO_LARGE = 9;
    private static final int REQUEST_ENABLE_BT = 10; // 블루투스 활성화 상태
    private BluetoothAdapter bluetoothAdapter; // 블루투스 어댑터
    private Set<BluetoothDevice> devices; // 블루투스 디바이스 데이터 셋
    private BluetoothDevice bluetoothDevice; // 블루투스 디바이스
    private BluetoothSocket bluetoothSocket = null; // 블루투스 소켓
    private OutputStream outputStream = null; // 블루투스에 데이터를 출력하기 위한 출력 스트림
    int pariedDeviceCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ndstudy);

        settingVar(); //변수 설정

        study_icon_study.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        study_text.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
    }



    private void settingVar(){

        setBlueTooth();

        //하단바에서 icon과 텍스트를 감싸고 있는 실제 터치를 감지할 Paddinged LinearLayout
        study_Linear_study = findViewById(R.id.study_Linear_study);
        camera_Linear_study = findViewById(R.id.camera_Linear_study);
        setting_Linear_study = findViewById(R.id.setting_Linear_study);

        //하단바 icon들
        study_icon_study = findViewById(R.id.study_icon_study);
        camera_icon_study = findViewById(R.id.camera_icon_study);
        setting_icon_study = findViewById(R.id.setting_icon_study);

        //아이콘 하단의 텍스트
        study_text = findViewById(R.id.study_text);

        //점자 셀 사이 칸막이
        bar1 = findViewById(R.id.study_bar1);
        bar2 = findViewById(R.id.study_bar2);

        //입력창
        edt = findViewById(R.id.nd_study_editText);

        //비장애인용 점자 변환 클래스 객체화
        ndBraille = new NDBraille();

        //실제 UI상의 점자들
        dots = new ImageView[3][6];
        dots[0][0] = (ImageView) findViewById(R.id.nd_dot1_1);
        dots[0][1] = (ImageView) findViewById(R.id.nd_dot1_2);
        dots[0][2] = (ImageView) findViewById(R.id.nd_dot1_3);
        dots[0][3] = (ImageView) findViewById(R.id.nd_dot1_4);
        dots[0][4] = (ImageView) findViewById(R.id.nd_dot1_5);
        dots[0][5] = (ImageView) findViewById(R.id.nd_dot1_6);

        dots[1][0] = (ImageView) findViewById(R.id.nd_dot2_1);
        dots[1][1] = (ImageView) findViewById(R.id.nd_dot2_2);
        dots[1][2] = (ImageView) findViewById(R.id.nd_dot2_3);
        dots[1][3] = (ImageView) findViewById(R.id.nd_dot2_4);
        dots[1][4] = (ImageView) findViewById(R.id.nd_dot2_5);
        dots[1][5] = (ImageView) findViewById(R.id.nd_dot2_6);

        dots[2][0] = (ImageView) findViewById(R.id.nd_dot3_1);
        dots[2][1] = (ImageView) findViewById(R.id.nd_dot3_2);
        dots[2][2] = (ImageView) findViewById(R.id.nd_dot3_3);
        dots[2][3] = (ImageView) findViewById(R.id.nd_dot3_4);
        dots[2][4] = (ImageView) findViewById(R.id.nd_dot3_5);
        dots[2][5] = (ImageView) findViewById(R.id.nd_dot3_6);

        setListener();
    }

    private void setListener()
    {
        //입력창 이벤트
        edt.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s)
            {
                Log.d("내용 : ", s.toString());
                if(s.toString().length()>=1 && getType(s.toString()).equals("한글"))
                    updateDot(s);
                else if (getType(s.toString()).equals("숫자") || getType(s.toString()).equals("영문"))
                    Toast.makeText(getApplicationContext(), getType(s.toString())+"번역은 업데이트 예정 기능입니다 !", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }


        });


        study_Linear_study.setOnClickListener(v -> {

        });
        camera_Linear_study.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), NDCameraActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        setting_Linear_study.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), NDSettingActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
    }

    private void updateDot(Editable s){
        char c = s.toString().charAt(0);
        if(c == 'ㅇ') Toast.makeText(getApplicationContext(), "초성 ㅇ은 점자가 없습니다. :)", Toast.LENGTH_SHORT).show();

        BrailleCode brailleCode = ndBraille.getBrailleCode(c);
        Log.d("리턴된 코드 : ", Long.toBinaryString(brailleCode.getBrailleCode()));
        printDot(brailleCode);
    }

    private void printDot(BrailleCode brailleCode){
        long code = brailleCode.getBrailleCode();
        int jungLength = brailleCode.getbLength();
        int jongLength = brailleCode.getcLength();

        initDots(); //셀 전부 보이게 설정, 모든 점자 빈 상태로 초기화

        switch (brailleCode.getCodeLength()){
            case 1:
                when_braille_cells_length_is_1(code);
                break;
            case 2:
                when_braille_cells_length_is_2(code);
                break;
            case 3:
                when_braille_cells_length_is_3(code);
            break;
            case TOO_LARGE:
                when_braille_cells_length_TOO_LARGE(code);
                break;
        }
    }

    private void when_braille_cells_length_is_1(long code) {
        int benchMark = 0b100000;
        //비트 연산으로 UI상에 변경된 점자를 반영
        for(int i = 0; i < 6; i++)
            if((code & benchMark>>i) != 0) dots[0][i].setImageResource(R.drawable.nd_filleddot);

        send_BrailleCode_To_Arduino(code);
    }

    private void when_braille_cells_length_is_2(long code) {
        int benchMark = 0b100000000000;
        for(int i = 0; i < 2; i++){
            for (int j = 0; j < 6; j++){
                if((code & benchMark>>(6*i)+j) != 0) dots[i][j].setImageResource(R.drawable.nd_filleddot);
            }
        }

        send_BrailleCode_To_Arduino(code);
    }

    private void when_braille_cells_length_is_3(long code) {
        int benchMark = 0b100000000000000000;
        for(int i = 0; i < 3; i++){
            for (int j = 0; j < 6; j++){
                if((code & benchMark>>(6*i)+j) != 0) dots[i][j].setImageResource(R.drawable.nd_filleddot);
            }
        }

        send_BrailleCode_To_Arduino(code);
    }

    private void when_braille_cells_length_TOO_LARGE(long code){
        Toast.makeText(NDStudyActivity.this, "점자 길이가 셀 3개를 초과합니다. 점자 디스플레이에는 출력됩니다.", Toast.LENGTH_LONG).show();
        sendData(code);
    }

    private void send_BrailleCode_To_Arduino(long brailleCode)
    {
        sendData(brailleCode);
        Toast.makeText(getApplicationContext(), "보내는중...", Toast.LENGTH_LONG);
    }

    private void sendData(long brailleCode) {
        try{
            // 데이터 송신
            String result = Long.toBinaryString(brailleCode); //개행문자 추가

            //점자 코드 길이를 6의 배수에 맞춰 앞에 0추가. ex) 101 -> 000101
            String bubble = "";
            if(!(result.length()%6 == 0))
                for(int i = 0; i < 6-(result.length()%6); i++)
                    bubble += "0";
            result = bubble + result;

            //임베디드에서 점자 셀 6개가 한 세트로 취급하기 때문에 36자리에 맞춰 뒤에 0을 추가해줌
            for(int i = 0; i < 36 - result.length(); i++)
                result += "0";

            outputStream.write(result.getBytes());

        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void initDots(){
        setDotsVisiblity();
        setDotsEmpty();
    }

    private void setDotsEmpty(){
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 6; j++){
                dots[i][j].setImageResource(R.drawable.nd_emptydot);
            }
        }
    }

    private void setDotsVisiblity()
    {
        //점자들 전부 보이게
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 6; j ++){
                dots[i][j].setVisibility(View.VISIBLE);
            }
        }

        //칸막이(?)들 전부 보이게
        bar1.setVisibility(View.VISIBLE);
        bar2.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), NDMainActivity.class));
        overridePendingTransition(0,0);
        finish();
    }

    private void setBlueTooth(){
        // 블루투스 활성화하기
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // 블루투스 어댑터를 디폴트 어댑터로 설정
        if(bluetoothAdapter == null) { // 디바이스가 블루투스를 지원하지 않을 때
            // 여기에 처리 할 코드를 작성하세요.
            finish();
        }
        else { // 디바이스가 블루투스를 지원 할 때

            if(bluetoothAdapter.isEnabled()) { // 블루투스가 활성화 상태 (기기에 블루투스가 켜져있음)
                selectBluetoothDevice(); // 블루투스 디바이스 선택 함수 호출
            }
            else { // 블루투스가 비 활성화 상태 (기기에 블루투스가 꺼져있음)
                // 블루투스를 활성화 하기 위한 다이얼로그 출력
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                // 선택한 값이 onActivityResult 함수에서 콜백된다.
                startActivityForResult(intent, REQUEST_ENABLE_BT);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT :
                if(requestCode == RESULT_OK) { // '사용'을 눌렀을 때
                    selectBluetoothDevice(); // 블루투스 디바이스 선택 함수 호출
                }
                else { // '취소'를 눌렀을 때
                    // 여기에 처리 할 코드를 작성하세요.
                    startActivity(new Intent(getApplicationContext(), NDMainActivity.class));
                    overridePendingTransition(0,0);
                    finish();
                }
                break;
        }
    }

    public void selectBluetoothDevice() {
        // 이미 페어링 되어있는 블루투스 기기를 찾습니다.
        devices = bluetoothAdapter.getBondedDevices();
        // 페어링 된 디바이스의 크기를 저장
        pariedDeviceCount = devices.size();
        // 페어링 되어있는 장치가 없는 경우
        if(pariedDeviceCount == 0) {
            // 페어링을 하기위한 함수 호출
        }
        // 페어링 되어있는 장치가 있는 경우
        else {
            // 디바이스를 선택하기 위한 다이얼로그 생성
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("페어링 되어있는 블루투스 디바이스 목록");
            // 페어링 된 각각의 디바이스의 이름과 주소를 저장
            List<String> list = new ArrayList<>();
            // 모든 디바이스의 이름을 리스트에 추가
            for(BluetoothDevice bluetoothDevice : devices) {
                list.add(bluetoothDevice.getName());
            }
            // List를 CharSequence 배열로 변경
            final CharSequence[] charSequences = list.toArray(new CharSequence[list.size()]);
            list.toArray(new CharSequence[list.size()]);
            // 해당 아이템을 눌렀을 때 호출 되는 이벤트 리스너
            builder.setItems(charSequences, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 해당 디바이스와 연결하는 함수 호출
                    connectDevice(charSequences[which].toString());
                }
            });
            // 뒤로가기 버튼 누를 때 창이 안닫히도록 설정
            builder.setCancelable(false);
            // 다이얼로그 생성
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public void connectDevice(String deviceName) {
        // 페어링 된 디바이스들을 모두 탐색
        for(BluetoothDevice tempDevice : devices) {
            // 사용자가 선택한 이름과 같은 디바이스로 설정하고 반복문 종료
            if(deviceName.equals(tempDevice.getName())) {
                bluetoothDevice = tempDevice;
                break;
            }
        }
        // UUID 생성
        UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        // Rfcomm 채널을 통해 블루투스 디바이스와 통신하는 소켓 생성
        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            // 데이터 송신 스트림을 얻어옵니다.
            outputStream = bluetoothSocket.getOutputStream();
            // 데이터 수신 함수 호출
            //receiveData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getType(String word) {
        boolean numeric = false;
        boolean alpha = false;
        boolean korean = false;
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < word.length(); i++) {
            int index = word.charAt(i);

            if(index >= 48 && index <= 57) {
                numeric = true;
            } else if(index >= 65 && index <= 122) {
                alpha = true;
            } else {
                korean = true;
            }
        }

        if(numeric)
            sb.append("숫자");

        if(alpha) {
            if(sb.length() > 0)
                sb.append(",");
            sb.append("영문");
        }

        if(korean) {
            if(sb.length() > 0)
                sb.append(",");
            sb.append("한글");
        }

        return sb.toString();
    }
}
