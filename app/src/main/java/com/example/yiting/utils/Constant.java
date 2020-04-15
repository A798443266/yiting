package com.example.yiting.utils;

public class Constant {
    public static String BASE = "http://192.168.1.4:8888";

    public static final String SP_NAME = "sp_yiting";//sp名称
    public static final String ISLOGIN = "islogin";//是否已经登录
    public static final String ISADMIN = "isAdmin";//当前登录的是否是车场管理员
    public static final String USERNAME = "username";//用户名
    public static final String USERID = "userid"; // 当前登录用户id
    public static final String PARINK_LOT_ID = "parkingLotId"; //车场id
    public static final String CUR_CITY = "curCity"; // 当前定位城市

    public static final String LOGIN = BASE + "/login";
    public static final String PARK_ADMIN_LOGIN = BASE + "/parkAdminLogin";
    public static final String ADD_SHARE = BASE + "/addShare";
    public static final String GETALLSHARE = BASE + "/getAllShareInfo";
    public static final String GET_USER_INFO = BASE + "/getUserInfo";
    public static final String GET_PARKADMIN = BASE + "/getParkAdminInfo";
    public static final String GET_PARKINKGLOT_INFO = BASE + "/getPakLotInfo";
    public static final String UPDATE_PARKINGLOT = BASE + "/updateParkingLot";
    public static final String ADD_SHARE_ORDER = BASE + "/addShareOrder";
    public static final String GET_SHARE_ORDER = BASE + "/getShareOrderById";
    public static final String GET_INFORMATION = BASE + "/getInformation";
    public static final String ADD_CAR = BASE + "/addCar";
    public static final String GET_CARS = BASE + "/getCars";
    public static final String  GET_USER_SHARE_PARK= BASE + "/getUserShareParks";
    //共享车位预定情况
    public static final String  GET_SHARE_BOOK_DETAIL= BASE + "/getShareOrderBookDetail";
    public static final String TUIDING = BASE + "/tuiding";
    public static final String DELETE_CAR = BASE + "/deleteCar";

}
