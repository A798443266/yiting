package com.example.yiting.bean;

import java.io.Serializable;

/**
 * 共享车位信息
 */
public class UserSharePark implements Serializable {

    private Integer id;

    private Integer userid;

    private Double longitude;

    private Double latitude;

    private String address;

    private String pic;

    private String description;

    private String plot;

    private String parklotid;

    private String number;

    private Integer size;

    private String phone;

    private String name;

    private String passtime;

    private Float price;

    private String day;

    private String starttime;

    private String submittime;

    private String endtime;

    private String province;

    private String city;

    private Integer review;

    private Integer type;

    public UserSharePark() {
    }

    public UserSharePark(Integer id, Integer userid, Double longitude, Double latitude, String address, String pic, String description, String plot, String parklotid, String number, Integer size, String phone, String name, String submittime, String passtime, Float price, String day, String starttime, String endtime, String province, String city, Integer review, Integer type) {
        this.id = id;
        this.userid = userid;
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
        this.pic = pic;
        this.description = description;
        this.plot = plot;
        this.parklotid = parklotid;
        this.number = number;
        this.size = size;
        this.phone = phone;
        this.name = name;
        this.submittime = submittime;
        this.passtime = passtime;
        this.price = price;
        this.day = day;
        this.starttime = starttime;
        this.endtime = endtime;
        this.province = province;
        this.city = city;
        this.review = review;
        this.type = type;
    }

    public String getSubmittime() {
        return submittime;
    }

    public void setSubmittime(String submittime) {
        this.submittime = submittime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getParklotid() {
        return parklotid;
    }

    public void setParklotid(String parklotid) {
        this.parklotid = parklotid;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasstime() {
        return passtime;
    }

    public void setPasstime(String passtime) {
        this.passtime = passtime;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getReview() {
        return review;
    }

    public void setReview(Integer review) {
        this.review = review;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
