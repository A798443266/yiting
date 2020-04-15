package com.example.yiting.parkadmin.fragment;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.yiting.R;
import com.example.yiting.bean.ParkingLot;
import com.example.yiting.fragment.BaseFragment;
import com.example.yiting.utils.Constant;
import com.example.yiting.utils.SpUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

public class HomeAdminFragment extends BaseFragment {

    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_total)
    TextView tvTotal;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tv_current)
    TextView tvCurrent;
    @BindView(R.id.tv_free)
    TextView tvFree;
    @BindView(R.id.tv_cappedPrice)
    TextView tvCappedPrice;
    @BindView(R.id.et_total)
    EditText etTotal;
    @BindView(R.id.et_current)
    EditText etCurrent;
    @BindView(R.id.et_price)
    EditText etPrice;
    @BindView(R.id.et_free)
    EditText etFree;
    @BindView(R.id.et_cap_price)
    EditText etCapPrice;
    @BindView(R.id.et_description)
    EditText etDescription;

    private ParkingLot parkingLot;

    @Override
    protected void initView() {
        getInfo();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_admin_home;
    }

    public void getInfo() {
        int parkLotId = SpUtils.getInt(mContext, Constant.PARINK_LOT_ID);
        OkHttpUtils.get().url(Constant.GET_PARKINKGLOT_INFO)
                .addParams("parkLotId", parkLotId + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(mContext, "获取停车场信息失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JSONObject jsonObject = JSON.parseObject(response);
                        String str = jsonObject.getString("parkingLot");
                        parkingLot = JSON.parseObject(str, ParkingLot.class);
                        setView();
                    }
                });
    }

    public void setView() {
        if (parkingLot == null) return;
        tvName.setText(parkingLot.getName());
        tvAddress.setText(parkingLot.getAddress());
        tvTotal.setText(parkingLot.getTotal() + "");
        tvCurrent.setText(parkingLot.getCurrent() + "");
        tvFree.setText(parkingLot.getFreeDuration() == null ? "未知" : parkingLot.getFreeDuration() + "");
        tvCappedPrice.setText(parkingLot.getCappedPrice() == null ? "未知" : parkingLot.getCappedPrice() + "");
        tvPrice.setText(parkingLot.getPrice() + "");

        etTotal.setText(parkingLot.getTotal() + "");
        etCurrent.setText(parkingLot.getCurrent() + "");
        etFree.setText(parkingLot.getFreeDuration() == null ? "" : parkingLot.getFreeDuration() + "");
        etCapPrice.setText(parkingLot.getCappedPrice() == null ? "" : parkingLot.getCappedPrice() + "");
        etPrice.setText(parkingLot.getPrice() + "");
        etDescription.setText(parkingLot.getDescription() == null ? "" : parkingLot.getDescription() + "");
    }

    @OnClick(R.id.btn_submit)
    public void onViewClicked() {
        String total = etTotal.getText().toString().trim();
        String current = etCurrent.getText().toString().trim();
        String capPrice = etCapPrice.getText().toString().trim();
        String free = etFree.getText().toString().trim();
        String price = etPrice.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (TextUtils.isEmpty(total) || TextUtils.isEmpty(current) || TextUtils.isEmpty(capPrice)
                || TextUtils.isEmpty(free) || TextUtils.isEmpty(price)) {
            Toast.makeText(mContext, "输入的信息不完整", Toast.LENGTH_SHORT).show();
            return;
        }
        int parkLotId = SpUtils.getInt(mContext, Constant.PARINK_LOT_ID);

        JSONObject object = new JSONObject();
        try {
            object.put("id", parkLotId);
            object.put("current", current);
            object.put("total", total);
            object.put("cappedPrice", capPrice);
            object.put("freeDuration", free);
            object.put("price", price);
            object.put("description", description);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = JSONObject.toJSONString(object);

        OkHttpUtils.postString().url(Constant.UPDATE_PARKINGLOT)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(json)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(mContext, "网络出错了", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JSONObject jsonObject = JSON.parseObject(response);
                        int code = jsonObject.getInteger("code");
                        if (code == 200) {
                            Toast.makeText(mContext, "修改成功", Toast.LENGTH_SHORT).show();
                            getInfo();
                        } else {
                            Toast.makeText(mContext, "修改失败", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }
}
