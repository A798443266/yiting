package com.example.yiting.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.yiting.R;
import com.example.yiting.bean.ParkingLot;
import com.example.yiting.utils.Constant;
import com.example.yiting.utils.NavigationUtil;
import com.example.yiting.utils.StatusBarUtil;
import com.example.yiting.utils.UIUtils;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoader;
import com.zyyoona7.popup.EasyPopup;
import com.zyyoona7.popup.XGravity;
import com.zyyoona7.popup.YGravity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ParkInfoDetailActivity extends AppCompatActivity {
    @BindView(R.id.root)
    RelativeLayout root;
    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tv_cur)
    TextView tvCur;
    @BindView(R.id.tv_free)
    TextView tvFree;
    @BindView(R.id.tv_cappedPrice)
    TextView tvCappedPrice;
    @BindView(R.id.tv_description)
    TextView tvDescription;

    private ParkingLot parkingLot;
    private EasyPopup popNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_info_detail);
        ButterKnife.bind(this);
        StatusBarUtil.fullScreen(this, true);

        parkingLot = (ParkingLot) getIntent().getSerializableExtra("info");
        initView();
        initPop();
    }

    public void initView() {
        if (parkingLot == null) return;
        tvName.setText(parkingLot.getName());
        tvAddress.setText(parkingLot.getProvince() + "省" + parkingLot.getCity() + "市" + parkingLot.getAddress());
        tvFree.setText(parkingLot.getFreeDuration() == null ? "未知" : parkingLot.getFreeDuration() + "");
        tvCur.setText(parkingLot.getCurrent() + "");
        tvPrice.setText(parkingLot.getPrice() + "");
        tvCappedPrice.setText(parkingLot.getCappedPrice() == null ? "未知" : parkingLot.getCappedPrice() + "");
        tvDescription.setText(parkingLot.getDescription() == null ? "暂无描述" : parkingLot.getDescription() + "");
        initBanner();
    }

    private void initBanner() {

        String pics = parkingLot.getPic();
        if (TextUtils.isEmpty(pics)) return;

        String[] paths = pics.split(",");

        List<String> imagesUrl = new ArrayList<>();
        for (String path : paths) {
            imagesUrl.add(Constant.BASE + path);
        }
        banner.setImages(imagesUrl);
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        banner.setIndicatorGravity(BannerConfig.CENTER);
        banner.setDelayTime(3500);
        //设置图片加载器
        banner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                Glide.with(ParkInfoDetailActivity.this).load(path).error(R.drawable.no_pic).into(imageView);
            }
        });
        banner.start();
    }

    private void initPop() {
        View view = View.inflate(this, R.layout.pop_select_daohang_way, null);
        popNav = new EasyPopup(this)
                .setContentView(view)
                .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setHeight(UIUtils.dp2px(159))
                .setBackgroundDimEnable(true)
                .setAnimationStyle(R.style.pop_park_info_animation)
                .setDimValue(0.2f)
                .setFocusAndOutsideEnable(true)
                .apply();

        popNav.findViewById(R.id.ll1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double longitude = parkingLot.getLongitude();
                double latitude = parkingLot.getLatitude();
                NavigationUtil.navBaidu(ParkInfoDetailActivity.this, longitude, latitude);
                popNav.dismiss();
            }
        });
        popNav.findViewById(R.id.ll2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double longitude = parkingLot.getLongitude();
                double latitude = parkingLot.getLatitude();
                NavigationUtil.navGaode(ParkInfoDetailActivity.this, longitude, latitude);
                popNav.dismiss();
            }
        });

        popNav.findViewById(R.id.ll3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popNav.dismiss();
            }
        });
    }

    @OnClick({R.id.rl_back, R.id.rl_collect, R.id.ll_daohang, R.id.ll_yuding})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_collect:
                break;
            case R.id.ll_daohang:
                popNav.showAtAnchorView(root, YGravity.ALIGN_BOTTOM, XGravity.CENTER, 0, 0);
                break;
            case R.id.ll_yuding:
                break;
        }
    }
}
