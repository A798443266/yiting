package com.example.yiting.bean;

public class ParkAdmin {
    private Integer id;

    private String phone;

    private String password;

    private String name;

    private String pic;

    private Integer parkinglotid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic == null ? null : pic.trim();
    }

    public Integer getParkinglotid() {
        return parkinglotid;
    }

    public void setParkinglotid(Integer parkinglotid) {
        this.parkinglotid = parkinglotid;
    }
}
