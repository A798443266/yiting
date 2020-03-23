package com.example.yiting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yiting.R;
import com.example.yiting.utils.Constant;
import com.example.yiting.utils.SpUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeActivity extends AppCompatActivity {

    @BindView(R.id.ll_logo)
    LinearLayout llLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        showAnimation();
        handler.sendMessageDelayed(Message.obtain(), 1000);
    }

    private void showAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(500);
        alphaAnimation.setInterpolator(new AccelerateInterpolator());//设置动画的变化率
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(alphaAnimation);
        llLogo.startAnimation(animationSet);
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            // 如果当前activity已经退出，那么我就不处理handler中的消息
            if (isFinishing()) {
                return;
            }
            toMainOrLogin();
        }
    };

    private void toMainOrLogin() {
        boolean isLogin = SpUtils.getBoolean(this, Constant.ISLOGIN);
        if (isLogin) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
