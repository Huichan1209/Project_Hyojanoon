package com.hyojanoon.project_hyojanoon;

public class BrailleCode{
    private long brailleCode;
    private int bLength;
    private int cLength;
    private int codeLength;
    public final int TOO_LARGE = 9;

    public BrailleCode(long _brailleCode, int _bLength, int _cLength){
        this.brailleCode = _brailleCode;
        this.bLength = _bLength;
        this.cLength = _cLength;
    }

    public long getBrailleCode() {
        return brailleCode;
    }

    public int getbLength() {
        return bLength;
    }

    public int getcLength() {
        return cLength;
    }

    public int getCodeLength(){
        if(brailleCode > 0b111111111111111111) //점자 길이가 셀 3개 이상을 초과
            codeLength = TOO_LARGE;
        else if (brailleCode > 0b111111111111) //점자 길이가 셀 2개 보다 길때 (셀 3개)
            codeLength = 3;
        else if (brailleCode > 0b111111) //점자 길이가 셀 1개보다 클때 (셀 2개)
            codeLength = 2;
        else                            //나머지 (셀 1개)
            codeLength = 1;
        return codeLength;
    }
}