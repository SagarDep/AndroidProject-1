package com.jrfunclibrary.model;

/**
 * Created by Administrator on 2017/4/30.
 */

public class CommCode {

    /**
     * codeString : null
     * value : 100003914
     * text : 通知
     * isUse : 1
     * codeValue : null
     */

    private Object codeString;
    private int value;
    private String text;
    private int isUse;
    private Object codeValue;

    public Object getCodeString() {
        return codeString;
    }

    public void setCodeString(Object codeString) {
        this.codeString = codeString;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getIsUse() {
        return isUse;
    }

    public void setIsUse(int isUse) {
        this.isUse = isUse;
    }

    public Object getCodeValue() {
        return codeValue;
    }

    public void setCodeValue(Object codeValue) {
        this.codeValue = codeValue;
    }
}
