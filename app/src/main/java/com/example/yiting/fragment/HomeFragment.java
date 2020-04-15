package com.example.yiting.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;
import com.example.yiting.R;
import com.example.yiting.activity.AddCarActivity;
import com.example.yiting.activity.InformationAllActivity;
import com.example.yiting.activity.InformationDetailActivity;
import com.example.yiting.activity.ReleaseParkActivity;
import com.example.yiting.activity.SearchActivity;
import com.example.yiting.adapter.InformationAdapter;
import com.example.yiting.bean.Information;
import com.example.yiting.utils.Constant;
import com.example.yiting.utils.SpUtils;
import com.example.yiting.view.MyListView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoader;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

public class HomeFragment extends BaseFragment {

    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.tv_city)
    TextView tvCity;
    @BindView(R.id.lv)
    MyListView lv;

    public LocationClient mLocationClient = null;

    private MyLocationListener myListener = null;
    private InformationAdapter adapter;
    private List<Information> informations;

    @Override
    protected void initView() {
        initBanner();
        adapter = new InformationAdapter(mContext, null);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, InformationDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("info", informations.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        getCity();
        // 检查权限
//        checkPermission();
        getInformation();

    }

    public void getCity() {
        String city = SpUtils.getString(mContext, Constant.CUR_CITY);
        tvCity.setText(city);
        mLocationClient = new LocationClient(mContext);
        myListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myListener);

        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    public void getInformation() {
        OkHttpUtils.get().url(Constant.GET_INFORMATION)
                .addParams("pageNo", "1")
                .addParams("pageSize", "4")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        String str = SpUtils.getString(mContext, "home_information");
                        if(!TextUtils.isEmpty(str)) {
                            parseJson(str);
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        SpUtils.putString(mContext, "home_information", response);
                        parseJson(response);
                    }
                });
    }

    public void parseJson(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        String str = jsonObject.getString("information");
        informations = JSON.parseArray(str, Information.class);
        adapter.setData(informations);
    }

    public void checkPermission() {
        // GPS定位
        if (ContextCompat.checkSelfPermission(mContext,
            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

//        网络定位
        if (ContextCompat.checkSelfPermission(mContext,
            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 102);
        }
    }

    // 申请权限的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e("TAG","asd");
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            getActivity().finish();
        } else {
            Log.e("TAG","laile");
            getCity();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }

    private void initBanner() {
        List<Integer> imagesUrl = new ArrayList<>();
        imagesUrl.add(R.drawable.banner1);
        imagesUrl.add(R.drawable.banner2);
        banner.setImages(imagesUrl);
        //设置循环指示点
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        //设置轮播时间
        banner.setDelayTime(3500);
        //设置翻转动画效果
//        banner.setBannerAnimation(Transformer.ZoomOutSlide);
        //设置图片加载器
        banner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                Glide.with(mContext).load(path).into(imageView);
            }
        });
        //设置item的点击事件
//        banner.setOnBannerListener(new OnBannerListener() {
//            @Override
//            public void OnBannerClick(int position) {
//                Toast.makeText(mContext, "position==" + position, Toast.LENGTH_SHORT).show();
//            }
//        });
        banner.start();
    }

    @OnClick({R.id.ll1, R.id.ll2, R.id.ll3, R.id.ll4, R.id.rl_title, R.id.ll_more, R.id.iv_msg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll1:
            case R.id.rl_title:
                startActivity(new Intent(mContext, SearchActivity.class));
                EventBus.getDefault().post(2);
                break;
            case R.id.ll2:
                startActivity(new Intent(mContext, ReleaseParkActivity.class));
                break;
            case R.id.ll3:
                EventBus.getDefault().post(3);
                break;
            case R.id.ll4:
            case R.id.ll_more:
                startActivity(new Intent(mContext, InformationAllActivity.class));
                break;
            case R.id.iv_msg:
                break;
        }
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            String addr = location.getAddrStr();    //获取详细地址信息
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            String city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            String street = location.getStreet();    //获取街道信息

            if (city != null) {
                tvCity.setText(city);
                SpUtils.putString(mContext, Constant.CUR_CITY, city);
            }

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mLocationClient != null)
            mLocationClient.stop();
    }

}
