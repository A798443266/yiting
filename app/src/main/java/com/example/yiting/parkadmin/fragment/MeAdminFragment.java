package com.example.yiting.parkadmin.fragment;

import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.example.yiting.R;
import com.example.yiting.activity.LoginActivity;
import com.example.yiting.bean.ParkAdmin;
import com.example.yiting.fragment.BaseFragment;
import com.example.yiting.utils.Constant;
import com.example.yiting.utils.SpUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

public class MeAdminFragment extends BaseFragment {

    @BindView(R.id.cv_user_pic)
    CircleImageView cvUserPic;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    private ParkAdmin parkAdmin;

    @Override
    protected void initView() {
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_admin_me;
    }

    @Override
    public void initData() {
        super.initData();
        getUserInfo();
    }

    public void getUserInfo() {
        int userId = SpUtils.getInt(mContext, Constant.USERID);
        OkHttpUtils.get().url(Constant.GET_PARKADMIN)
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
                        String userStr = jsonObject.getString("parkAdmin");
                        parkAdmin = JSON.parseObject(userStr, ParkAdmin.class);
                        setView();
                    }
                });
    }


    public void setView() {
        if (parkAdmin == null) return;
        Glide.with(mContext).load(Constant.BASE + parkAdmin.getPic())
                .error(R.drawable.touxiang)
                .into(cvUserPic);
        tvUsername.setText(parkAdmin.getName());

    }

    @OnClick(R.id.btn_logout)
    public void onViewClicked() {
        SpUtils.clear(mContext);
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
