package com.hyojanoon.project_hyojanoon;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import static android.speech.tts.TextToSpeech.ERROR;

public class LastLearnActivity extends AppCompatActivity {
    ImageView[] brailles;
    TextView tvChar;
    TextToSpeech tts;
    LinearLayout lastLearnLinear;
    Braille braille;
    char[] chars = {'ㄱ','ㄴ', 'ㄷ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅅ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ', 'ㅆ'};
    int currentChar = 0;
    static final int SEC = 1000;
    boolean isFirst = true;

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
        setContentView(R.layout.activity_last_learn);
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
        brailles = new ImageView[6];
        brailles[0] = (ImageView) findViewById(R.id.last_learn_braille1);
        brailles[1] = (ImageView) findViewById(R.id.last_learn_braille2);
        brailles[2] = (ImageView) findViewById(R.id.last_learn_braille3);
        brailles[3] = (ImageView) findViewById(R.id.last_learn_braille4);
        brailles[4] = (ImageView) findViewById(R.id.last_learn_braille5);
        brailles[5] = (ImageView) findViewById(R.id.last_learn_braille6);

        //tts 객체 초기화
        initTTS();
//        setBlueTooth();

        braille = new Braille();

        lastLearnLinear = (LinearLayout) findViewById(R.id.lastLearnLinear);
        lastLearnLinear.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()){

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

        tvChar = (TextView) findViewById(R.id.tvChar_las);
    }

    private void updateChange(){

        //UI상의 점자 핀 6개 초기화
        for(int i=0; i<brailles.length; i++)
            brailles[i].setImageResource(R.drawable.empty_braille);

        //자작 점자 클래스로 현재 문자(초성)을 보내서 점자 코드로 변환
        char brailleCode = braille.getBrailleCode(Braille.LAST_SOUND, chars[currentChar]);

        char benchmark = 0b100000; //반복문을 위한 기준값

        //비트 연산을 통해 UI상의 점자핀에 점자 코드를 반영
        for(int i=0;i<brailles.length; i++)
            if((brailleCode & benchmark>>i) != 0)
                brailles[i].setImageResource(R.drawable.filled_braille);

        long code = brailleCode;
        sendData(code);

        /*
            위의 for문과 같음.
            if((brailleCode & 0b100000) != 0) brailles[0].setImageResource(R.drawable.filled_braille);
            if((brailleCode & 0b010000) != 0) brailles[1].setImageResource(R.drawable.filled_braille);
            if((brailleCode & 0b001000) != 0) brailles[2].setImageResource(R.drawable.filled_braille);
            if((brailleCode & 0b000100) != 0) brailles[3].setImageResource(R.drawable.filled_braille);
            if((brailleCode & 0b000010) != 0) brailles[4].setImageResource(R.drawable.filled_braille);
            if((brailleCode & 0b000001) != 0) brailles[5].setImageResource(R.drawable.filled_braille);
        */

        announce(TextToSpeech.QUEUE_FLUSH, "이 점자는 " + Character.toString(chars[currentChar])
                + ". " + Character.toString(chars[currentChar]) + "입니다.");

        if(isFirst) {
            announce(TextToSpeech.QUEUE_ADD, "다음 점자는 오른쪽으로, 이전 점자는 왼쪽으로 스와이핑 해주세요");
            isFirst = false;
        }

        tvChar.setText(Character.toString(chars[currentChar]));
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

    private void sendData(long brailleCode) {
        try{
            // 데이터 송신
            String result = Long.toBinaryString(brailleCode)+"\n"; //개행문자 추가
            outputStream.write(result.getBytes());
        }catch(Exception e) {
            e.printStackTrace();
        }
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
