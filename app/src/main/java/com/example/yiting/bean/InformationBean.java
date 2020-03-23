package com.example.yiting.bean;

import java.io.Serializable;

public class InformationBean implements Serializable {
    private String title;
    private String desc;
    private String[] pics;
    private String time;
    private String publish;
    private int readCount;

    public InformationBean() {
    }

    public InformationBean(String title, String desc, String[] pics, String time, String publish, int readCount) {
        this.title = title;
        this.desc = desc;
        this.pics = pics;
        this.time = time;
        this.publish = publish;
        this.readCount = readCount;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String[] getPics() {
        return pics;
    }

    public String getTime() {
        return time;
    }

    public String getPublish() {
        return publish;
    }

    public int getReadCount() {
        return readCount;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setPics(String[] pics) {
        this.pics = pics;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setPublish(String publish) {
        this.publish = publish;
    }

    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }
}
