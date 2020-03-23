package com.example.yiting.bean;

/**
 * 搜索记录
 */
public class RecordInfo {
    private String id;
    private String time;
    private String content;

    public RecordInfo() {
    }

    public RecordInfo(String id, String time, String content) {
        this.id = id;
        this.time = time;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
