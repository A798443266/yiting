package com.example.yiting.parkadmin.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.yiting.R;
import com.example.yiting.fragment.HomeFragment;
import com.example.yiting.fragment.MeFragment;
import com.example.yiting.fragment.OrderFragment;
import com.example.yiting.fragment.ParkFragment;
import com.example.yiting.parkadmin.fragment.HomeAdminFragment;
import com.example.yiting.parkadmin.fragment.MeAdminFragment;
import com.example.yiting.utils.StatusBarUtil;
import com.example.yiting.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainAdminActivity extends AppCompatActivity {

    @BindView(R.id.fl_main)
    FrameLayout flMain;
    @BindView(R.id.iv_home)
    ImageView ivHome;
    @BindView(R.id.tv_home)
    TextView tvHome;
    @BindView(R.id.iv_me)
    ImageView ivMe;
    @BindView(R.id.tv_me)
    TextView tvMe;

    private HomeAdminFragment homeFragment;
    private MeAdminFragment meFragment;
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);
        ButterKnife.bind(this);
        StatusBarUtil.fullScreen(this, true);
        setSelect(1);
    }

    @OnClick({R.id.ll_home, R.id.ll_me})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_home:
                setSelect(1);
                break;
            case R.id.ll_me:
                setSelect(2);
                break;
        }
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
                    homeFragment = new HomeAdminFragment();
                    transaction.add(R.id.fl_main, homeFragment);
                }
                ivHome.setBackgroundResource(R.drawable.icon_nav_home1);
                tvHome.setTextColor(UIUtils.getColor(R.color.systemColor));
                transaction.show(homeFragment);
                break;
            case 2:
                if (meFragment == null) {
                    meFragment = new MeAdminFragment();
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
        if (meFragment != null) {
            transaction.hide(meFragment);
        }
    }

    //重置颜色图标
    private void reSetResourse() {
        ivHome.setBackgroundResource(R.drawable.icon_nav_home);
        ivMe.setBackgroundResource(R.drawable.icon_nav_me);

        tvHome.setTextColor(UIUtils.getColor(R.color.navGray));
        tvMe.setTextColor(UIUtils.getColor(R.color.navGray));
    }




    //连按两次退出
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WHAT_BACK) {
                flag = true;
            }
        }
    };
    private boolean flag = true;
    private static final int WHAT_BACK = 0;

    //连续点击两次返回键退出
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && flag) {
            Toast.makeText(MainAdminActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            flag = false;
            handler.sendEmptyMessageDelayed(WHAT_BACK, 2000);
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
