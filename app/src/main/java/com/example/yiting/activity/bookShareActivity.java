package com.example.yiting.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.example.yiting.R;
import com.example.yiting.bean.Car;
import com.example.yiting.bean.ShareInfo;
import com.example.yiting.bean.ShareOrder;
import com.example.yiting.utils.Constant;
import com.example.yiting.utils.SpUtils;
import com.example.yiting.utils.UIUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zyyoona7.popup.EasyPopup;
import com.zyyoona7.popup.XGravity;
import com.zyyoona7.popup.YGravity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

public class bookShareActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_car)
    TextView tvCar;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_day)
    TextView tvDay;
    @BindView(R.id.et_description)
    EditText etDescription;
    @BindView(R.id.root)
    RelativeLayout root;
    @BindView(R.id.ll)
    LinearLayout ll;
    @BindView(R.id.ll_success)
    LinearLayout llSuccess;

    private ShareInfo shareInfo;
    private List<Car> cas;
    private String startTime;
    private String endTime;
    private OptionsPickerView<String> pvOptions;
    private OptionsPickerView<String> pvCar;
    private List<String> license_cars = new ArrayList<>();
    private String license;
    private List<String> startTimeArr = new ArrayList<>();
    private List<String> endTimeArr = new ArrayList<>();

    private EasyPopup popPay;
    private EasyPopup popPwd;
    private int payWay = 1;
    private float price;
    private String password;
    private String description;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_share);
        ButterKnife.bind(this);
        initView();
    }

    public void initView() {
        tvTitle.setText("预定车位");
        shareInfo = (ShareInfo) getIntent().getSerializableExtra("info");
        cas = (List<Car>) getIntent().getSerializableExtra("cars");
        for (int i = 0; i < cas.size(); i++) {
            license_cars.add(cas.get(i).getLicensePlate());
        }
        if (shareInfo == null) return;
        tvDay.setText(shareInfo.getUserSharePark().getDay());

        String[] arr1 = shareInfo.getUserSharePark().getStarttime().split(":");
        String[] arr2 = shareInfo.getUserSharePark().getEndtime().split(":");
        int startH = Integer.parseInt(arr1[0]);
        int startM = Integer.parseInt(arr1[1]);
        int endH = Integer.parseInt(arr2[0]);
        int endM = Integer.parseInt(arr2[1]);
        startTimeArr.add(shareInfo.getUserSharePark().getStarttime());
        if (startM == 0) { //表示允许的开始时间是整点
            String str = String.valueOf(startH).length() == 1 ? "0" + startH + ":30" : startH + ":30";
            startTimeArr.add(str);
            endTimeArr.add(str);
        }
        for (int i = startH + 1; i < endH; i++) {
            String half = String.valueOf(i).length() == 1 ? "0" + i + ":30" : i + ":30";
            String noHalf = String.valueOf(i).length() == 1 ? "0" + i + ":00" : i + ":00";
            startTimeArr.add(noHalf);
            startTimeArr.add(half);
            endTimeArr.add(noHalf);
            endTimeArr.add(half);
        }
        if (endM == 0) { //表示允许的结束时间是整点
            endTimeArr.add(endH + ":00");
        } else {
            endTimeArr.add(endH + ":00");
            endTimeArr.add(endH + ":30");
            startTimeArr.add(endH + ":00");
        }

        pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                startTime = startTimeArr.get(options1);
                endTime = endTimeArr.get(options2);
                tvTime.setText(startTime + " ~ " + endTime);
            }
        })
                .setContentTextSize(20)
                .setTitleText("选择停车时段")
                .setTitleColor(0xaa000000)//标题文字颜色
                .setSubmitColor(UIUtils.getColor(R.color.systemColor))//确定按钮文字颜色
                .setCancelColor(0x88000000)//取消按钮文字颜色
                .setSelectOptions(0, 0)
                .build();
        pvOptions.setNPicker(startTimeArr, endTimeArr, null);

        pvCar = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                license = license_cars.get(options1);
                tvCar.setText(license);
            }
        })
                .setContentTextSize(20)
                .setTitleText("选择车辆")
                .setTitleColor(0xaa000000)//标题文字颜色
                .setSubmitColor(UIUtils.getColor(R.color.systemColor))//确定按钮文字颜色
                .setCancelColor(0x88000000)//取消按钮文字颜色
                .setSelectOptions(0)
                .build();
        pvCar.setNPicker(license_cars, null, null);

    }

    public void submit() {
        userId = SpUtils.getInt(this, Constant.USERID);
        description = etDescription.getText().toString().trim();
        if (TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime)) {
            Toast.makeText(this, "输入的信息不完整", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userId == shareInfo.getUser().getId()) {
            Toast.makeText(this, "不能预定自己发布的共享车位！", Toast.LENGTH_SHORT).show();
            return;
        }
        //计算价格
        price = getPrice(shareInfo.getUserSharePark().getPrice(), startTime, endTime);
        //显示支付方式
        showPayWay();
    }

    public void showPayWay() {
        View view = View.inflate(this, R.layout.pop_select_pay_way, null);
        popPay = new EasyPopup(this)
                .setContentView(view)
                .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setBackgroundDimEnable(true)
                .setAnimationStyle(R.style.pop_park_info_animation)
                .setDimValue(0.2f)
                .setFocusAndOutsideEnable(true)
                .apply();
        TextView tv_price = popPay.findViewById(R.id.tv_price);
        RadioButton rb_ye = popPay.findViewById(R.id.rb_ye);
        RadioButton rb_zfb = popPay.findViewById(R.id.rb_zfb);
        RadioButton rb_wx = popPay.findViewById(R.id.rb_wx);
        tv_price.setText(price + "");


        rb_ye.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) return;
                rb_zfb.setChecked(false);
                rb_wx.setChecked(false);
                payWay = 1;
            }
        });
        rb_zfb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) return;
                rb_wx.setChecked(false);
                rb_ye.setChecked(false);
                payWay = 2;
            }
        });
        rb_wx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) return;
                rb_zfb.setChecked(false);
                rb_ye.setChecked(false);
                payWay = 3;
            }
        });

        popPay.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popPay.dismiss();
                showPwd();
            }
        });

        popPay.showAtAnchorView(root, YGravity.ALIGN_BOTTOM, XGravity.CENTER, 0, 0);

    }

    public void showPwd() {
        View view = View.inflate(this, R.layout.pop_input_password, null);
        popPwd = new EasyPopup(this)
                .setContentView(view)
                .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setBackgroundDimEnable(true)
                .setAnimationStyle(R.style.pop_park_info_animation)
                .setDimValue(0.2f)
                .setFocusAndOutsideEnable(true)
                .apply();

        EditText et_password = popPwd.findViewById(R.id.et_password);
        popPwd.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = et_password.getText().toString().trim();
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(bookShareActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                request();
            }
        });
        popPwd.showAtAnchorView(root, YGravity.ALIGN_BOTTOM, XGravity.CENTER, 0, 0);
    }

    public void request() {
        JSONObject object = new JSONObject();
        try {
            object.put("userId", userId + "");
            object.put("startTime", startTime);
            object.put("endTime", endTime);
            //对应的共享车位id
            object.put("shareId", shareInfo.getUserSharePark().getId());
            object.put("beizhu", description);
            object.put("price", price + "");
            object.put("password", password);
            //车牌
            object.put("car", license);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String json = JSONObject.toJSONString(object);
        OkHttpUtils.postString().url(Constant.ADD_SHARE_ORDER)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(json)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        popPwd.dismiss();
                        Toast.makeText(bookShareActivity.this, "网络出错了", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        parseJson(response);
                    }
                });
    }

    public void parseJson(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        int code = jsonObject.getInteger("code");
        if (code == 200) {
            popPwd.dismiss();
            ll.setVisibility(View.GONE);
            llSuccess.setVisibility(View.VISIBLE);
            //通知订单页刷新
            EventBus.getDefault().post(new ShareOrder());
        } else {
            Toast.makeText(this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
            if (code == 300) {
                popPwd.dismiss();
            }
        }
    }

    private float getPrice(Float price, String startTime, String endTime) {
        String[] arr1 = startTime.split(":");
        String[] arr2 = endTime.split(":");
        int startH = Integer.parseInt(arr1[0]);
        int startM = Integer.parseInt(arr1[1]);
        int endH = Integer.parseInt(arr2[0]);
        int endM = Integer.parseInt(arr2[1]);

        float time = endH - startH;
        if (startM != 0 && endM == 0) {
            time -= 0.5;
        } else if (startM == 0 && endM != 0) {
            time += 0.5;
        }
        return price * time;
    }

    @OnClick({R.id.rl_back, R.id.ll_time, R.id.btn, R.id.tv_car})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.ll_time:
                pvOptions.show();
                break;
            case R.id.tv_car:
                pvCar.show();
                break;
            case R.id.btn:
                submit();
                break;
        }
    }
}
