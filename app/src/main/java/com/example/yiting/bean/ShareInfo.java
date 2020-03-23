package com.example.yiting.bean;

import java.io.Serializable;

/**
 * 用户车位共享信息，包括车位信息和用户信息
 */
public class ShareInfo implements Serializable {
    private User user;
    private UserSharePark userSharePark;

    public ShareInfo() {
    }

    public ShareInfo(User user, UserSharePark userSharePark) {
        this.user = user;
        this.userSharePark = userSharePark;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserSharePark getUserSharePark() {
        return userSharePark;
    }

    public void setUserSharePark(UserSharePark userSharePark) {
        this.userSharePark = userSharePark;
    }
}
