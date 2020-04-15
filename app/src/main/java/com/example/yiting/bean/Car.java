package com.example.yiting.bean;

import java.io.Serializable;

public class Car implements Serializable {
    private Integer id;

    private String licensePlate;

    private Integer status;

    private String frameNumber;

    private String engineNumber;

    private String pic;

    private Integer userid;

    private String createtime;

    private String passtime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate == null ? null : licensePlate.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFrameNumber() {
        return frameNumber;
    }

    public void setFrameNumber(String frameNumber) {
        this.frameNumber = frameNumber == null ? null : frameNumber.trim();
    }

    public String getEngineNumber() {
        return engineNumber;
    }

    public void setEngineNumber(String engineNumber) {
        this.engineNumber = engineNumber == null ? null : engineNumber.trim();
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic == null ? null : pic.trim();
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getPasstime() {
        return passtime;
    }

    public void setPasstime(String passtime) {
        this.passtime = passtime;
    }

}
