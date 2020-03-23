package com.example.yiting.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.yiting.R;
import com.example.yiting.fragment.HomeFragment;
import com.example.yiting.fragment.MeFragment;
import com.example.yiting.fragment.OrderFragment;
import com.example.yiting.fragment.ParkFragment;
import com.example.yiting.utils.StatusBarUtil;
import com.example.yiting.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends FragmentActivity {

    @BindView(R.id.fl_main)
    FrameLayout flMain;
    @BindView(R.id.iv_home)
    ImageView ivHome;
    @BindView(R.id.tv_home)
    TextView tvHome;
    @BindView(R.id.iv_order)
    ImageView ivOrder;
    @BindView(R.id.tv_order)
    TextView tvOrder;
    @BindView(R.id.iv_me)
    ImageView ivMe;
    @BindView(R.id.tv_me)
    TextView tvMe;
    @BindView(R.id.ll_me)
    LinearLayout llMe;
    @BindView(R.id.iv_park)
    ImageView ivPark;
    @BindView(R.id.tv_park)
    TextView tvPark;


    private FragmentTransaction transaction;
    private HomeFragment homeFragment;
    private ParkFragment parkFragment;
    private OrderFragment orderFragment;
    private MeFragment meFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        StatusBarUtil.fullScreen(this, true);
        EventBus.getDefault().register(this);
        setSelect(1);
    }

    @OnClick({R.id.ll_home, R.id.ll_park, R.id.ll_order, R.id.ll_me})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_home:
                setSelect(1);
                break;
            case R.id.ll_park:
                setSelect(2);
//                StatusBarUtil.addStatusViewWithColor(this, "#fb3b5e");
                break;
            case R.id.ll_order:
                setSelect(3);
                break;
            case R.id.ll_me:
                setSelect(4);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Integer event) {
        setSelect(event);
    }

    public void setSelect(int select) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        //隐藏fragment
        hideFragment();
        //重置颜色
        reSetResourse();
        switch (select) {
            case 1:
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                    transaction.add(R.id.fl_main, homeFragment);
                }
                ivHome.setBackgroundResource(R.drawable.icon_nav_home1);
                tvHome.setTextColor(UIUtils.getColor(R.color.systemColor));
                transaction.show(homeFragment);
                break;
            case 2:
                if (parkFragment == null) {
                    parkFragment = new ParkFragment();
                    transaction.add(R.id.fl_main, parkFragment);
                }
                ivPark.setBackgroundResource(R.drawable.icon_nav_park1);
                tvPark.setTextColor(UIUtils.getColor(R.color.systemColor));
                transaction.show(parkFragment);
                break;
            case 3:
                if (orderFragment == null) {
                    orderFragment = new OrderFragment();
                    transaction.add(R.id.fl_main, orderFragment);
                }
                ivOrder.setBackgroundResource(R.drawable.icon_nav_order1);
                tvOrder.setTextColor(UIUtils.getColor(R.color.systemColor));
                transaction.show(orderFragment);
                break;
            case 4:
                if (meFragment == null) {
                    meFragment = new MeFragment();
                    transaction.add(R.id.fl_main, meFragment);
                }
                ivMe.setBackgroundResource(R.drawable.icon_nav_me1);
                tvMe.setTextColor(UIUtils.getColor(R.color.systemColor));
                transaction.show(meFragment);
                break;
        }

        transaction.commit();
    }

    //隐藏fragment
    private void hideFragment() {
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (parkFragment != null) {
            transaction.hide(parkFragment);
        }
        if (orderFragment != null) {
            transaction.hide(orderFragment);
        }
        if (meFragment != null) {
            transaction.hide(meFragment);
        }
    }

    //重置颜色图标
    private void reSetResourse() {
        ivHome.setBackgroundResource(R.drawable.icon_nav_home);
        ivPark.setBackgroundResource(R.drawable.icon_nav_park);
        ivOrder.setBackgroundResource(R.drawable.icon_nav_order);
        ivMe.setBackgroundResource(R.drawable.icon_nav_me);

        tvHome.setTextColor(UIUtils.getColor(R.color.navGray));
        tvPark.setTextColor(UIUtils.getColor(R.color.navGray));
        tvOrder.setTextColor(UIUtils.getColor(R.color.navGray));
        tvMe.setTextColor(UIUtils.getColor(R.color.navGray));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
