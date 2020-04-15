package com.example.yiting.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.example.yiting.R;
import com.example.yiting.bean.Car;
import com.example.yiting.bean.ShareInfo;
import com.example.yiting.bean.User;
import com.example.yiting.bean.UserSharePark;
import com.example.yiting.model.Model;
import com.example.yiting.utils.Constant;
import com.example.yiting.utils.NavigationUtil;
import com.example.yiting.utils.SpUtils;
import com.example.yiting.utils.StatusBarUtil;
import com.example.yiting.utils.UIUtils;
import com.example.yiting.view.CommomDialog;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoader;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zyyoona7.popup.EasyPopup;
import com.zyyoona7.popup.XGravity;
import com.zyyoona7.popup.YGravity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class PersonShareDetailActivity extends AppCompatActivity {
    @BindView(R.id.root)
    RelativeLayout root;
    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_day)
    TextView tvDay;
    @BindView(R.id.tv_startTime)
    TextView tvStartTime;
    @BindView(R.id.tv_endTime)
    TextView tvEndTime;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_contact)
    TextView tvContact;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_beizhu)
    TextView tvBeizhu;

    private ShareInfo shareInfo;
    private EasyPopup popNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_share_detail);
        ButterKnife.bind(this);
        StatusBarUtil.fullScreen(this, true);

        initView();
    }

    public void initView() {
        shareInfo = (ShareInfo) getIntent().getSerializableExtra("info");
        if (shareInfo == null) return;

        UserSharePark park = shareInfo.getUserSharePark();
        User user = shareInfo.getUser();

        tvUsername.setText(user.getName());
        tvAddress.setText(park.getProvince() + "省" + park.getCity() + "市" + park.getAddress());
        tvDay.setText(park.getDay());
        tvStartTime.setText(park.getStarttime());
        tvEndTime.setText(park.getEndtime());
        tvPrice.setText(park.getPrice() + "");
        tvContact.setText(park.getName());
        tvPhone.setText(park.getPhone());
        tvBeizhu.setText(park.getDescription());
        tvNumber.setText(park.getNumber());
        tvType.setText(park.getType() == 0 ? "室内" : "室外");

        initBanner();
        initPop();
    }

    private void initBanner() {
        String pics = shareInfo.getUserSharePark().getPic();
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
                Glide.with(PersonShareDetailActivity.this).load(path).error(R.drawable.no_pic).into(imageView);
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
                double longitude = shareInfo.getUserSharePark().getLongitude();
                double latitude = shareInfo.getUserSharePark().getLatitude();
                NavigationUtil.navBaidu(PersonShareDetailActivity.this, longitude, latitude);
                popNav.dismiss();
            }
        });
        popNav.findViewById(R.id.ll2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double longitude = shareInfo.getUserSharePark().getLongitude();
                double latitude = shareInfo.getUserSharePark().getLatitude();
                NavigationUtil.navGaode(PersonShareDetailActivity.this, longitude, latitude);
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
                checkCars();
                break;
        }
    }

    public void checkCars() {
        int userId = SpUtils.getInt(this, Constant.USERID);
        if (userId == shareInfo.getUserSharePark().getUserid()) {
            Toast.makeText(this, "您不能预定自己发布的共享车位", Toast.LENGTH_SHORT).show();
            return;
        }
        //获取用户添加的车辆
        OkHttpUtils.get().url(Constant.GET_CARS)
                .addParams("userId", userId+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(PersonShareDetailActivity.this, "网络出错了", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JSONObject jsonObject = JSON.parseObject(response);
                        String str = jsonObject.getString("cars");
                        List<Car> cars = JSON.parseArray(str, Car.class);
                        if (cars != null && cars.size() > 0) {
                            Intent intent = new Intent(PersonShareDetailActivity.this, bookShareActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("info", shareInfo);
                            bundle.putSerializable("cars", (Serializable) cars);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            new CommomDialog(PersonShareDetailActivity.this, R.style.dialog, "您还没有添加车辆，添加车辆后才可以预定车位，现在就去添加？", new CommomDialog.OnCloseListener() {
                                @Override
                                public void onClick(Dialog dialog, boolean confirm) {
                                    if (confirm) {
                                        dialog.dismiss();
                                        startActivity(new Intent(PersonShareDetailActivity.this, AddCarActivity.class));
                                    }
                                }
                            }).setTitle("提示").show();
                        }
                    }
                });

    }
}
