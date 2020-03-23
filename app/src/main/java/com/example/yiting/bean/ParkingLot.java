package com.example.yiting.bean;

import java.io.Serializable;

public class ParkingLot implements Serializable {
    private Integer id;

    private String number;

    private String name;

    private Integer total;

    private Integer current;

    private String address;

    private Float price;

    private String city;

    private Double longitude;

    private Double latitude;

    private String payWay;

    private String province;

    private Float freeDuration;

    private Float cappedPrice;

    private String pic;

    private String description;

    public ParkingLot() {
    }

    public ParkingLot(Integer id, String number, String name, Integer total, Integer current, String address, Float price, String city, Double longitude, Double latitude, String payWay, String province, Float freeDuration, Float cappedPrice, String pic, String description) {
        this.id = id;
        this.number = number;
        this.name = name;
        this.total = total;
        this.current = current;
        this.address = address;
        this.price = price;
        this.city = city;
        this.longitude = longitude;
        this.latitude = latitude;
        this.payWay = payWay;
        this.province = province;
        this.freeDuration = freeDuration;
        this.cappedPrice = cappedPrice;
        this.pic = pic;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number == null ? null : number.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
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

    public String getPayWay() {
        return payWay;
    }

    public void setPayWay(String payWay) {
        this.payWay = payWay == null ? null : payWay.trim();
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province == null ? null : province.trim();
    }

    public Float getFreeDuration() {
        return freeDuration;
    }

    public void setFreeDuration(Float freeDuration) {
        this.freeDuration = freeDuration;
    }

    public Float getCappedPrice() {
        return cappedPrice;
    }

    public void setCappedPrice(Float cappedPrice) {
        this.cappedPrice = cappedPrice;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic == null ? null : pic.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }
}
