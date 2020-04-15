package com.example.yiting.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.yiting.R;
import com.example.yiting.adapter.MyCarAdapter;
import com.example.yiting.bean.Car;
import com.example.yiting.bean.ShareOrderInfo;
import com.example.yiting.utils.Constant;
import com.example.yiting.utils.SpUtils;
import com.example.yiting.view.CommomDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class MyCarsActivity extends AppCompatActivity {

    @BindView(R.id.lv)
    ListView lv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.ll_no)
    LinearLayout llNo;

    private MyCarAdapter adapter;
    private List<Car> cars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cars);
        ButterKnife.bind(this);

        adapter = new MyCarAdapter(this, null);
        lv.setAdapter(adapter);

        getData(null);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData(refreshlayout);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(false);//传入false表示加载失败
            }
        });
        setClick();
    }

    public void getData(RefreshLayout refreshLayout) {
        int userId = SpUtils.getInt(this, Constant.USERID);
        OkHttpUtils.get().url(Constant.GET_CARS)
                .addParams("userId", userId + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (refreshLayout != null) {
                            refreshLayout.finishRefresh(false);
                        }
                        Toast.makeText(MyCarsActivity.this, "网络出错了", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (refreshLayout != null) {
                            refreshLayout.finishRefresh();
                        }
                        JSONObject jsonObject = JSON.parseObject(response);
                        String str = jsonObject.getString("cars");
                        cars = JSON.parseArray(str, Car.class);
                        if (cars != null && cars.size() > 0) {
                            adapter.setData(cars);
                            llNo.setVisibility(View.GONE);
                        }
                    }
                });
    }

    @OnClick({R.id.rl_back, R.id.iv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.iv_add:
                Intent intent = new Intent(MyCarsActivity.this, AddCarActivity.class);
                startActivityForResult(intent, 200);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            if (resultCode == RESULT_OK) {
                getData(null);
            }
        }
    }

    public void setClick() {
        adapter.setOnDeleteClickListener(new MyCarAdapter.OnDeleteClickListener() {
            @Override
            public void onClick(View view, int position) {
                new CommomDialog(MyCarsActivity.this, R.style.dialog, "您确定删除该车辆吗？", new CommomDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        if (confirm) {
                            OkHttpUtils.get().url(Constant.DELETE_CAR)
                                    .addParams("carId", cars.get(position).getId() + "")
                                    .build()
                                    .execute(new StringCallback() {
                                        @Override
                                        public void onError(Call call, Exception e, int id) {
                                            Toast.makeText(MyCarsActivity.this, "网络出错了", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }

                                        @Override
                                        public void onResponse(String response, int id) {
                                            JSONObject jsonObject = JSON.parseObject(response);
                                            int code = jsonObject.getInteger("code");
                                            dialog.dismiss();
                                            if(code == 200) {
                                                Toast.makeText(MyCarsActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                                getData(null);
                                            }
                                        }
                                    });
                        }
                    }
                }).setTitle("提示").show();

            }
        });
    }
}
