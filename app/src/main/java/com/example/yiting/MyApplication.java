package com.example.yiting;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.example.yiting.model.Model;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;
import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;

public class MyApplication extends Application {
    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        init();
    }

    private void init() {
        Model.getInstance().init(context);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //初始化科大讯飞
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=5ca5b466");
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);

        //okhttpUtils配置
        CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(getApplicationContext()));

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
            .cookieJar(cookieJar)
            .connectTimeout(5000L, TimeUnit.MILLISECONDS)
            .readTimeout(5000L, TimeUnit.MILLISECONDS)
            //其他配置
            .build();
        OkHttpUtils.initClient(okHttpClient);
    }
}
