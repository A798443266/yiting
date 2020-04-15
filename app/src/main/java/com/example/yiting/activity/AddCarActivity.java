package com.example.yiting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.yiting.R;
import com.example.yiting.utils.Constant;
import com.example.yiting.utils.SpUtils;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

public class AddCarActivity extends AppCompatActivity {

    @BindView(R.id.root)
    RelativeLayout root;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.license_container)
    RelativeLayout licenseContainer;
    @BindView(R.id.ct)
    CommonTabLayout ct;
    @BindView(R.id.btn)
    Button btn;
    @BindView(R.id.license)
    com.example.yiting.view.LicensePlateView license;

    private String[] types = {"五位牌照", "六位牌照"};
    private ArrayList<CustomTabEntity> list = new ArrayList<>();
    private String car_license;
    private boolean canClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);
        ButterKnife.bind(this);

        tvTitle.setText("添加车辆");
        initLienseType();
        license.setInputListener(new com.example.yiting.view.LicensePlateView.InputListener() {
            @Override
            public void inputComplete(String content) {
                car_license = content;
                btn.setClickable(true);
                canClick = true;
                btn.setBackgroundResource(R.drawable.button_bg);
            }

            @Override
            public void deleteContent() {
                car_license = "";
                if (canClick) {
                    btn.setBackgroundResource(R.drawable.button_enable_bg);
                    canClick = false;
                    btn.setClickable(false);
                }

            }
        });
        //设置父布局作为自定义键盘的容器
        license.setKeyboardContainerLayout(licenseContainer);
    }

    public void initLienseType() {
        for (int i = 0; i < types.length; i++) {
            int finalI = i;
            list.add(new CustomTabEntity() {
                @Override
                public String getTabTitle() {
                    return types[finalI];
                }

                @Override
                public int getTabSelectedIcon() {
                    return 0;
                }

                @Override
                public int getTabUnselectedIcon() {
                    return 0;
                }
            });
        }
        ct.setTabData(list);
        ct.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (position == 1) {
                    license.showLastView();
                    car_license = "";
                    btn.setBackgroundResource(R.drawable.button_enable_bg);
                    canClick = false;
                    btn.setClickable(false);
                } else {
                    license.hideLastView();
                }
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
    }

    @OnClick({R.id.rl_back, R.id.btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn:
                sumbit();
                break;
        }
    }

    public void sumbit() {
        if (TextUtils.isEmpty(car_license)) {
            Toast.makeText(AddCarActivity.this, "请先输入车牌！", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = SpUtils.getInt(this, Constant.USERID);
        JSONObject object = new JSONObject();
        try {
            object.put("userid", userId + "");
            object.put("licensePlate", car_license);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String json = JSONObject.toJSONString(object);
        btn.setClickable(false);
        OkHttpUtils.postString().url(Constant.ADD_CAR)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(json)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        btn.setClickable(true);
                        Toast.makeText(AddCarActivity.this, "网络出错了", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        btn.setClickable(true);
                        JSONObject jsonObject = JSON.parseObject(response);
                        int code = jsonObject.getInteger("code");
                        if (code == 200) {
                            Toast.makeText(AddCarActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                            Intent intent = getIntent();
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            Toast.makeText(AddCarActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}
