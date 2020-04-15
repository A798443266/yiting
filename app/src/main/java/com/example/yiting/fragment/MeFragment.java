package com.example.yiting.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.example.yiting.R;
import com.example.yiting.activity.LoginActivity;
import com.example.yiting.activity.MyCarsActivity;
import com.example.yiting.activity.MyShareActivity;
import com.example.yiting.bean.ShareOrder;
import com.example.yiting.bean.User;
import com.example.yiting.utils.Constant;
import com.example.yiting.utils.SpUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

public class MeFragment extends BaseFragment {
    @BindView(R.id.cv_user_pic)
    CircleImageView cvUserPic;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.tv_balance)
    TextView tvBalance;
    @BindView(R.id.tv_integral)
    TextView tvIntegral;

    private User user;

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_me;
    }

    @Override
    public void initData() {
        super.initData();
        getUserInfo();
    }

    //收到预定共享车位成功传递过来的消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ShareOrder s) {
        getUserInfo();
    }

    public void getUserInfo() {
        int userId = SpUtils.getInt(mContext, Constant.USERID);
        OkHttpUtils.get().url(Constant.GET_USER_INFO)
                .addParams("userId", userId + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(mContext, "获取用户信息失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JSONObject jsonObject = JSON.parseObject(response);
                        String userStr = jsonObject.getString("user");
                        user = JSON.parseObject(userStr, User.class);
                        setView();
                    }
                });
    }

    public void setView() {
        if (user == null) return;
        Glide.with(mContext).load(Constant.BASE + user.getAvatar())
                .error(R.drawable.touxiang)
                .into(cvUserPic);
        tvUsername.setText(user.getName());
        tvBalance.setText(user.getBalance() + "");
        tvIntegral.setText(user.getIntegral() + "");
    }

    @OnClick({R.id.rl1, R.id.rl2, R.id.rl3, R.id.rl4, R.id.rl5, R.id.btn_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl1:
                break;
            case R.id.rl2:
                startActivity(new Intent(mContext, MyCarsActivity.class));
                break;
            case R.id.rl3:
                startActivity(new Intent(mContext, MyShareActivity.class));
                break;
            case R.id.rl4:
                break;
            case R.id.rl5:
                break;
            case R.id.btn_logout:
                SpUtils.clear(mContext);
                Intent intent = new Intent(mContext, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
