package com.example.yiting.bean;

import java.io.Serializable;

public class ShareOrderInfo implements Serializable {
    private ShareOrder shareOrder;
    private User user;
    private UserSharePark userSharePark;

    public ShareOrderInfo() {
    }

    public ShareOrderInfo(ShareOrder shareOrder, User user, UserSharePark userSharePark) {
        this.shareOrder = shareOrder;
        this.user = user;
        this.userSharePark = userSharePark;
    }

    public ShareOrder getShareOrder() {
        return shareOrder;
    }

    public void setShareOrder(ShareOrder shareOrder) {
        this.shareOrder = shareOrder;
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
