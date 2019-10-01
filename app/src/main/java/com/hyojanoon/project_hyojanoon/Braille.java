package com.hyojanoon.project_hyojanoon;

import android.util.Log;

public class Braille {
static final int FIRST_SOUND = 0, SECOND_SOUND = 1, LAST_SOUND = 2, WORD = 3;

    //시각장애인모드에서 호출
    public char getBrailleCode(int type, char _char){
        char result = 0b000000;
        //type 값을 검사해서 초성인지 중성인지 종성인지 분류
        switch (type){
            case FIRST_SOUND :
                result = getBrailleCode_FirstSound(_char);
                break;

            case SECOND_SOUND :
                result = getBrailleCode_SecondSound(_char);
                break;

            case LAST_SOUND :
                result = getBrailleCode_LastSound(_char);
                break;
        }
        return result;
    }

    //비장애인모드에서 호출
    public BrailleCode getBrailleCode(char cho, char jung, char jong)
    {
        int a = 0b000000;
        int b = a;
        int c = b;
        if(jung == 'ㅏ') {
            //약자 (모음 ㅏ 인 경우) 경우 a(초성)는 0b000000
            b = getBrailleCode_Abbreviation(cho, jung);
            c = (int) getBrailleCode_LastSound(jong);
        }
        else {
            //일반경우
            a = (int) getBrailleCode_FirstSound(cho);
            b = (int) getBrailleCode_SecondSound(jung);
            c = (int) getBrailleCode_LastSound(jong);
        }
        int bLength = 6; int cLength = 6; // 종성이 없을 경우 마지막 자리는 0b000000 으로 채워짐
        if(b > 0b111111) bLength = 12;
        if(c > 0b111111) cLength = 12;
        if(c == 0) cLength = 0;
        Log.d("[Braille]", "초성 : " + cho + "중성 : " + jung + "종성 : " + jong);
        Log.d("[코드 길이]","bLength : " + bLength + " cLength : " + cLength);
        //각 음운의 코드 길이만큼 옆으로 밀고
        long result = (a << (bLength+cLength)) | b << (cLength) | c;
        BrailleCode brailleCode = new BrailleCode(result, bLength, cLength);

        return brailleCode;
    }

    public BrailleCode getBrailleCode(char cho){
        int result = getBrailleCode_FirstSound(cho);
        BrailleCode brailleCode = new BrailleCode(result, 0, 0);
        return brailleCode;
    }

    public BrailleCode getBrailleCode(char jung, boolean isJung){
        return getBrailleCode('ㅇ', jung, ' ');
    }

    private char getBrailleCode_FirstSound(char _char){
        char result = 0b000000;
        switch (_char){
            case 'ㄱ': result = 0b000100; break;
            case 'ㄴ': result = 0b100100; break;
            case 'ㄷ': result = 0b010100; break;
            case 'ㄹ': result = 0b000010; break;
            case 'ㅁ': result = 0b100010; break;
            case 'ㅂ': result = 0b000110; break;
            case 'ㅅ': result = 0b000001; break;
            case 'ㅇ': result = 0b000000; break;
            case 'ㅈ': result = 0b000101; break;
            case 'ㅊ': result = 0b000011; break;
            case 'ㅋ': result = 0b110100; break;
            case 'ㅌ': result = 0b110010; break;
            case 'ㅍ': result = 0b100110; break;
            case 'ㅎ': result = 0b010110; break;
            //된소리들
            case 'ㄲ':
            case 'ㄸ':
            case 'ㅃ':
            case 'ㅆ':
            case 'ㅉ': result = 0b000001; break;
            default: result = 0b000000;
        }
        return result;
    }

    private char getBrailleCode_SecondSound(char _char){
        char result = 0b000000;
        switch (_char){
            case 'ㅏ': result = 0b110001; break;
            case 'ㅑ': result = 0b001110; break;
            case 'ㅓ': result = 0b011100; break;
            case 'ㅕ': result = 0b100011; break;
            case 'ㅗ': result = 0b101001; break;
            case 'ㅛ': result = 0b001101; break;
            case 'ㅜ': result = 0b101100; break;
            case 'ㅠ': result = 0b100101; break;
            case 'ㅡ': result = 0b010101; break;
            case 'ㅣ': result = 0b101010; break;
            case 'ㅐ': result = 0b111010; break;
            case 'ㅔ': result = 0b101110; break;
            case 'ㅚ': result = 0b101111; break;
            case 'ㅘ': result = 0b111001; break;
            case 'ㅝ': result = 0b111100; break;
            case 'ㅢ': result = 0b010111; break;
            case 'ㅖ': result = 0b001100; break;
            case 'ㅟ': result = 0b101100111010; break;
            case 'ㅒ': result = 0b001110111010; break;
            case 'ㅙ': result = 0b111001111010; break;
            case 'ㅞ': result = 0b111100111010; break;
            default: result = 0b000000;
        }
        return result;
    }

    private char getBrailleCode_LastSound(char _char){
        char result = 0b000000;
        switch (_char){
            case 0x0000: result = 0; break;
            case 'ㄱ': result = 0b100000; break;
            case 'ㄴ': result = 0b010010; break;
            case 'ㄷ': result = 0b001010; break;
            case 'ㄹ': result = 0b010000; break;
            case 'ㅁ': result = 0b010001; break;
            case 'ㅂ': result = 0b110000; break;
            case 'ㅅ': result = 0b001000; break;
            case 'ㅇ': result = 0b011011; break;
            case 'ㅈ': result = 0b101000; break;
            case 'ㅊ': result = 0b011000; break;
            case 'ㅋ': result = 0b011010; break;
            case 'ㅌ': result = 0b011001; break;
            case 'ㅍ': result = 0b001101; break;
            case 'ㅎ': result = 0b001011; break;
            case 'ㅆ': result = 0b001100; break;
            case 'ㄲ': result = 0b100000100000; break;
            case 'ㄳ': result = 0b100000001000; break;
            case 'ㄵ': result = 0b010010101000; break;
            case 'ㄶ': result = 0b010010001011; break;
            case 'ㄺ': result = 0b010000100000; break;
            case 'ㄻ': result = 0b010000010001; break;
            case 'ㄼ': result = 0b010000110000; break;
            case 'ㄽ': result = 0b010000001000; break;
            case 'ㄾ': result = 0b010000011001; break;
            case 'ㄿ': result = 0b010000010011; break;
            case 'ㅀ': result = 0b010000001011; break;
            default: result = 0b000000;
        }
        return result;
    }

    private char getBrailleCode_Word(String _char){
        char result = 0b000000;
        switch (_char){
            case "가": result = 0b110101; break;
            case "나": result = 0b100100; break;
            case "다": result = 0b010100; break;
            case "마": result = 0b100010; break;
            case "바": result = 0b000110; break;
            case "사": result = 0b111000; break;
            case "아": result = 0b110001; break;
            case "자": result = 0b000101; break;
            case "카": result = 0b110100; break;
            case "타": result = 0b110010; break;
            case "파": result = 0b100110; break;
            case "하": result = 0b010110; break;
            case "것": result = 0b000111011100; break;
            case "ㅆ 받침": result = 0b001100; break;
            case "억": result = 0b100111; break;
            case "언": result = 0b011111; break;
            case "얼": result = 0b011110; break;
            case "연": result = 0b100001; break;
            case "열": result = 0b110011; break;
            case "영": result = 0b110111; break;
            case "옥": result = 0b101101; break;
            case "온": result = 0b111011; break;
            case "옹": result = 0b111111; break;
            case "운": result = 0b110110; break;
            case "울": result = 0b111101; break;
            case "은": result = 0b101011; break;
            case "을": result = 0b011101; break;
            case "인": result = 0b111110; break;
            case "그래서": result = 0b100000011100; break;
            case "그러나": result = 0b100000100100; break;
            case "그러면": result = 0b100000010010; break;
            case "그러므로": result = 0b100000010001; break;
            case "그런데": result = 0b100000101110; break;
            case "그리고": result = 0b100000101001; break;
            case "그리하여": result = 0b100000100011; break;
        }
        return result;
    }

    private char getBrailleCode_Abbreviation(char cho, char jung){
        char result = 0b000000;

        if(jung == 'ㅏ') {
            switch (cho) {
                case 'ㄱ': result = 0b110101; break;
                case 'ㄴ' : result = 0b100100 ; break;
                case 'ㄷ' : result = 0b010100 ; break;
                case 'ㄹ' : result = 0b000010110001; break;
                case 'ㅁ': result = 0b100010; break;
                case 'ㅂ': result = 0b000110; break;
                case 'ㅅ': result = 0b111000; break;
                case 'ㅇ': result = 0b110001; break;
                case 'ㅈ': result = 0b000101; break;
                case 'ㅊ': result = 0b000011110001; break;
                case 'ㅋ': result = 0b110100; break;
                case 'ㅌ': result = 0b110010; break;
                case 'ㅍ': result = 0b100110; break;
                case 'ㅎ': result = 0b010110; break;
            }
        }
        return result;
    }

    public char getBrailleCode(int type, String _char){
        char result = 0b000000;

        //type 값을 검사해서 약자인지 확인
        if (type == WORD) result = getBrailleCode_Word(_char);

        return result;
    }
}
