package com.example.yiting.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;
import com.example.yiting.R;
import com.example.yiting.activity.ReleaseParkActivity;
import com.example.yiting.activity.SearchActivity;
import com.example.yiting.adapter.InformationAdapter;
import com.example.yiting.bean.InformationBean;
import com.example.yiting.utils.Constant;
import com.example.yiting.utils.SpUtils;
import com.example.yiting.view.MyListView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

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
    private List<InformationBean> infos = new ArrayList<>();

    @Override
    protected void initView() {
        initBanner();

        for (int i = 0; i < 4; i++) {
            infos.add(new InformationBean());
        }

        adapter = new InformationAdapter(mContext, infos);
        lv.setAdapter(adapter);
        getCity();
        // 检查权限
//        checkPermission();

    }

    public void getCity() {
        mLocationClient = new LocationClient(mContext);
        myListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myListener);

        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
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

    @OnClick({R.id.ll1, R.id.ll2, R.id.ll3, R.id.ll4, R.id.rl_title})
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
