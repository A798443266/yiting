package com.example.yiting.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.yiting.R;
import com.example.yiting.bean.ShareInfo;
import com.example.yiting.bean.User;
import com.example.yiting.bean.UserSharePark;
import com.example.yiting.utils.Constant;
import com.example.yiting.utils.StatusBarUtil;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PersonShareDetailActivity extends AppCompatActivity {


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
        tvEndTime.setText(park.getStarttime());
        tvStartTime.setText(park.getEndtime());
        tvPrice.setText(park.getPrice() + "");
        tvContact.setText(park.getName());
        tvPhone.setText(park.getPhone());
        tvBeizhu.setText(park.getDescription());
        tvNumber.setText(park.getNumber());
        tvType.setText(park.getType() == 0 ? "室内" : "室外");

        initBanner();
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


    @OnClick({R.id.rl_back, R.id.rl_collect, R.id.ll_daohang, R.id.ll_yuding})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_collect:
                break;
            case R.id.ll_daohang:
                break;
            case R.id.ll_yuding:
                break;
        }
    }
}
