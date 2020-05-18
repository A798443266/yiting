package com.example.yiting.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import cn.smssdk.SMSSDK;
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
    @BindView(R.id.ll_login)
    LinearLayout llLogin;
    @BindView(R.id.et_nick)
    EditText etNick;
    @BindView(R.id.et_rg_password)
    EditText etRgPassword;
    @BindView(R.id.et_rg_password1)
    EditText etRgPassword1;
    @BindView(R.id.et_rg_phone)
    EditText etRgPhone;
    @BindView(R.id.et_checck_num)
    EditText etChecckNum;
    @BindView(R.id.btn_check)
    Button btnCheck;
    @BindView(R.id.ll_register)
    LinearLayout llRegister;
    @BindView(R.id.cb_gree)
    CheckBox cbGree;

    private int i = 30;
    private String nick;
    private String password;
    private String phone;
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

    @OnClick({R.id.ll, R.id.btn_login, R.id.tv_register, R.id.tv_login, R.id.btn_check, R.id.btn_register})
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
                llLogin.setVisibility(View.GONE);
                llRegister.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_login:
                if (isLogin) return;
                tvLogin.setBackgroundResource(R.drawable.button_bg);
                tvRegister.setBackgroundColor(Color.parseColor("#00000000"));
                tvRegister.setTextColor(Color.parseColor("#888888"));
                tvLogin.setTextColor(Color.parseColor("#ffffff"));
                isLogin = true;
                llRegister.setVisibility(View.GONE);
                llLogin.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_check:
                getSMS();
                break;
            case R.id.btn_register:
                registerUser();
                break;
            default:
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

    //获取验证码
    public void getSMS() {
        String phoneNum = etRgPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
            Toast.makeText(this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        SMSSDK.getVerificationCode("86", phoneNum);
        btnCheck.setClickable(false);
        //开始倒计时
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (; i > 0; i--) {
                    handler.sendEmptyMessage(-1);
                    if (i <= 0) {
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //倒计时结束执行
                handler.sendEmptyMessage(-2);
            }
        }).start();
    }

    private void registerUser() {

        nick = etNick.getText().toString();
        password = etRgPassword.getText().toString();
        String psw1 = etRgPassword1.getText().toString();
        phone = etRgPhone.getText().toString();
        String checknum = etChecckNum.getText().toString();

        if (TextUtils.isEmpty(nick) || TextUtils.isEmpty(password) || TextUtils.isEmpty(psw1)
                || TextUtils.isEmpty(phone) || TextUtils.isEmpty(checknum)) {
            Toast.makeText(this, "请输入完整信息", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cbGree.isChecked()) {
            Toast.makeText(this, "请先阅读并同意服务协议", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(psw1)) {
            Toast.makeText(this, "两次输入的密码不相等", Toast.LENGTH_SHORT).show();
            return;
        }

        //先验证手机验证码是否正确
        confirm();
    }

    private void confirm() {
        String phoneNum = etPhone.getText().toString().trim();
        String code = etChecckNum.getText().toString().trim();
        SMSSDK.submitVerificationCode("86", phoneNum, code);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == -1) {
                btnCheck.setText(i + " s");
            } else if (msg.what == -2) {
                btnCheck.setText("重新发送");
                btnCheck.setClickable(true);
                i = 30;
            } else {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        // TODO 处理成功得到验证码的结果
                        Toast.makeText(LoginActivity.this, "验证码已发送", Toast.LENGTH_SHORT).show();
                    } else {
                        // TODO 处理错误的结果
                        ((Throwable) data).printStackTrace();
                    }
                } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        // TODO 处理验证码验证通过的结果
                        // Toast.makeText(RegisterActivity.this, "验证码正确", Toast.LENGTH_SHORT).show();
                        //去自己的服务器中注册
                        JSONObject object = new JSONObject();
                        try {
                            object.put("phone", phone);
                            object.put("password", password);
                            object.put("name", nick);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String json = JSONObject.toJSONString(object);
                        llLoad.setVisibility(View.VISIBLE);
                        OkHttpUtils.postString().url(Constant.REGISTER)
                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                .content(json)
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        llLoad.setVisibility(View.GONE);
                                        Toast.makeText(LoginActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        llLoad.setVisibility(View.GONE);
                                        JSONObject jsonObject = JSON.parseObject(response);
                                        int code = jsonObject.getInteger("code");
                                        if (code == 200) {
                                            Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                            backLogin();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    } else {
                        // TODO 处理错误的结果
                        Toast.makeText(LoginActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                        ((Throwable) data).printStackTrace();
                    }
                }

            }
        }
    };

    public void backLogin() {
        tvLogin.setBackgroundResource(R.drawable.button_bg);
        tvRegister.setBackgroundColor(Color.parseColor("#00000000"));
        tvRegister.setTextColor(Color.parseColor("#888888"));
        tvLogin.setTextColor(Color.parseColor("#ffffff"));
        isLogin = true;
        llRegister.setVisibility(View.GONE);
        llLogin.setVisibility(View.VISIBLE);

        etRgPassword1.setText("");
        etRgPassword.setText("");
        etChecckNum.setText("");
        etNick.setText("");
        etPhone.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
        handler.removeCallbacksAndMessages(null);
    }
}
