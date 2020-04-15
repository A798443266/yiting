package com.example.yiting.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.example.yiting.R;

import java.io.File;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

public class NavigationUtil {

    //使用的是高德坐标，调用百度导航
    public static void navBaiduBy(final Context context, final double longitude, final double latitude) {
        //是否安装百度地图
        if (isInstallBaiduMap(context)) {
            /*高德坐标转百度坐标*/
            double[] endLatLng = gcjToBd(longitude, latitude);
            openBaiduMap(context, endLatLng[0], endLatLng[1]);
        }
    }

    //使用的是高德坐标，调用高德导航
    public static void navGaodeBy(final Context context, final double longitude, final double latitude) {
        //是否安装高德地图
        if (isInstallGaodeMap(context)) {
            openGaodeMap(context, longitude, latitude);

        }
    }

    //使用的是百度坐标，调用百度导航
    public static void navBaidu(final Context context, final double longitude, final double latitude) {
        //是否安装百度地图
        if (isInstallBaiduMap(context)) {
            openBaiduMap(context, longitude, latitude);
        }
    }

    //使用的是百度坐标，调用高德导航
    public static void navGaode(final Context context, final double longitude, final double latitude) {
        //是否安装高德地图
        if (isInstallGaodeMap(context)) {
            /*百度坐标转高德坐标+bd_decrypt+bdToGcj*/
            double[] endLatLng = bdToGcj(longitude, latitude);
            openGaodeMap(context, endLatLng[0], endLatLng[1]);
        }
    }


    private static void openBaiduMap(final Context context, double longitude, double latitude) {
        try {
            /*调用导航*/
            StringBuffer loc = new StringBuffer("baidumap://map/navi?");
            loc.append("&src=").append(R.string.app_name + "|" + R.string.app_name);
            loc.append("&location=").append(latitude + "," + longitude);
            Intent intent = Intent.getIntent(loc.toString());
            context.startActivity(intent); //启动调用
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private static void openGaodeMap(final Context context, double longitude, double latitude) {
        try {
            /*调用导航*/
            StringBuilder loc = new StringBuilder();
            loc.append("androidamap://navi?sourceApplication=");
            loc.append(R.string.app_name);
            loc.append("&lat=");
            loc.append(latitude);
            loc.append("&lon=");
            loc.append(longitude);
            loc.append("&dev=");//起终 点是否偏移(0:lat和lon是已经加密后的,不需要国测加密: 1:需要国测加密)
            loc.append(0);//百度坐标调用高德的时候设置为0，因为百度地图坐标已经加密了
//            loc.append("&dname=").append(endDescrible);
            loc.append("&style=");
            loc.append(2);
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(loc.toString()));
            intent.setPackage("com.autonavi.minimap");
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isInstallBaiduMap(Context context) {
        // 判断是否安装百度地图
        if (new File("/data/data/" + "com.baidu.BaiduMap").exists()) {
            return true;
        } else {
            // 未安装
            // 显示手机上所有的market商店
            try {
                Toast.makeText(context, "未安装百度地图", Toast.LENGTH_SHORT).show();
                Uri uri = Uri.parse("market://details?id=com.baidu.BaiduMap");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "请先安装百度地图", Toast.LENGTH_SHORT).show();
                return false;
            }
            return false;
        }
    }

    // 检查是否安装高德地图
    private static boolean isInstallGaodeMap(Context context) {
        if (new File("/data/data/" + "com.autonavi.minimap").exists()) {
            return true;
        } else {
            try {
                Toast.makeText(context, "未安装高德地图", Toast.LENGTH_SHORT).show();
                Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "请先安装高德地图", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return false;
    }

    //百度转高德-直接用Math.PI
    public static double[] bd_decrypt(double bd_lon, double bd_lat) {
        double[] gd_lat_lon = new double[2];
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = sqrt(x * x + y * y) - 0.00002 * sin(y * Math.PI);
        double theta = atan2(y, x) - 0.000003 * cos(x * Math.PI);
        gd_lat_lon[0] = z * cos(theta);
        gd_lat_lon[1] = z * sin(theta);
        return gd_lat_lon;
    }

    /**
     *     * 百度 => 高德
     *     * BD-09 => GCJ-02
     *     * @parambdLon 经度
     *     * @parambdLat 纬度
     *    
     */

    public static double[] bdToGcj(double bdLon, double bdLat) {
        double[] gd_lat_lon = new double[2];
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = bdLon - 0.0065, y = bdLat - 0.006;
        double z = sqrt(x * x + y * y) - 0.00002 * sin(y * PI);
        double theta = atan2(y, x) - 0.000003 * cos(x * PI);
        gd_lat_lon[0] = z * cos(theta);
        gd_lat_lon[1] = z * sin(theta);
        return gd_lat_lon;
    }

    /**
     *     * 高德 => 百度
     *     * @paramgdLon 经度
     *     * @paramgdLat 纬度
     */

    public static double[] gcjToBd(double gdLon, double gdLat) {
        double[] bd_lat_lon = new double[2];
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = gdLon, y = gdLat;
        double z = sqrt(x * x + y * y) + 0.00002 * sin(y * PI);
        double theta = atan2(y, x) + 0.000003 * cos(x * PI);
        bd_lat_lon[0] = z * cos(theta) + 0.0065;
        bd_lat_lon[1] = z * sin(theta) + 0.006;
        return bd_lat_lon;
    }

}
