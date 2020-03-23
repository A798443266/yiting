package com.example.yiting.bean;

// 搜索提示 sug 的bean类
public class SearchBean {

    private double latitude; // 纬度
    private double longitude; //经度

    private String city; //城市
    private String key; //关键词
    private String dis; // 区县

    public SearchBean() {
    }

    public SearchBean(double latitude, double longitude, String city, String key, String dis) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.key = key;
        this.dis = dis;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDis() {
        return dis;
    }

    public void setDis(String dis) {
        this.dis = dis;
    }
}
