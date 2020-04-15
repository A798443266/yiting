package com.example.yiting.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.yiting.R;
import com.example.yiting.parkadmin.activity.MainAdminActivity;
import com.example.yiting.utils.Constant;
import com.example.yiting.utils.SpUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.tv_login)
    TextView tvLogin;
    @BindView(R.id.tv_register)
    TextView tvRegister;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.ll_load)
    LinearLayout llLoad;
    @BindView(R.id.rg)
    RadioGroup rg;

    private Boolean isLogin = true;
    private Boolean isUser = true;  // 普通用户登录

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb1) {
                    isUser = true;
                } else {
                    isUser = false;
                }
            }
        });
    }

    @OnClick({R.id.ll, R.id.btn_login, R.id.tv_register, R.id.tv_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll:
                break;
            case R.id.tv_register:
                if (!isLogin) return;
                tvRegister.setBackgroundResource(R.drawable.button_bg);
                tvLogin.setBackgroundColor(Color.parseColor("#00000000"));
                tvLogin.setTextColor(Color.parseColor("#888888"));
                tvRegister.setTextColor(Color.parseColor("#ffffff"));
                isLogin = false;
                break;
            case R.id.tv_login:
                if (isLogin) return;
                tvLogin.setBackgroundResource(R.drawable.button_bg);
                tvRegister.setBackgroundColor(Color.parseColor("#00000000"));
                tvRegister.setTextColor(Color.parseColor("#888888"));
                tvLogin.setTextColor(Color.parseColor("#ffffff"));
                isLogin = true;
                break;
            case R.id.btn_login:
                login();
                break;
        }
    }

    public void login() {
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入账号密码", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject object = new JSONObject();
        try {
            object.put("phone", phone);
            object.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String json = JSONObject.toJSONString(object);
        String url = isUser ? Constant.LOGIN : Constant.PARK_ADMIN_LOGIN;

        llLoad.setVisibility(View.VISIBLE);
        OkHttpUtils.postString().url(url)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(json)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        llLoad.setVisibility(View.GONE);
                        Log.e("TAG", e.getMessage());
                        Toast.makeText(LoginActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        llLoad.setVisibility(View.GONE);
                        JSONObject jsonObject = JSON.parseObject(response);
                        int code = jsonObject.getInteger("code");
                        if (code == 200) {
                            int userId = jsonObject.getInteger("userId");
                            String name = jsonObject.getString("name");
                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

                            SpUtils.putBoolean(LoginActivity.this, Constant.ISLOGIN, true);
                            SpUtils.putString(LoginActivity.this, Constant.USERNAME, name);
                            SpUtils.putInt(LoginActivity.this, Constant.USERID, userId);
                            llLoad.setVisibility(View.GONE);
                            if (isUser) {
                                SpUtils.putBoolean(LoginActivity.this, Constant.ISADMIN, false);
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            } else {
                                int parkingLotId = jsonObject.getInteger("parkingLotId");
                                SpUtils.putBoolean(LoginActivity.this, Constant.ISADMIN, true);
                                SpUtils.putInt(LoginActivity.this, Constant.PARINK_LOT_ID, parkingLotId);
                                startActivity(new Intent(LoginActivity.this, MainAdminActivity.class));
                            }
                            finish();

                        } else {
                            Toast.makeText(LoginActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
