package com.callingapp.avijitsamanta.call.Helper;


import java.io.Serializable;

public class Modal implements Serializable {
    private String name, mobile, img;
    private String duration, date, time;
    private int callType;
    private boolean isChecked;

    public Modal() {
    }

    // For Contact
    public Modal(String name, String mobile, String img) {
        this.name = name;
        this.mobile = mobile;
        this.img = img;
    }

    // For Call
    public Modal(String name, String mobile, String date, String time, int callType, String uri) {
        this.name = name;
        this.mobile = mobile;
        this.callType = callType;
        this.date = date;
        this.time = time;
        this.img = uri;
    }

    // Call log details
    public Modal(String duration, String time, int callType,boolean bbb) {
        this.duration = duration;
        this.time = time;
        this.callType = callType;
        this.isChecked = bbb;
    }

    public Modal(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getCallType() {
        return callType;
    }

    public void setCallType(int callType) {
        this.callType = callType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    private String key,value;

    public Modal(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

