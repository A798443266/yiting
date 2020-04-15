package com.example.yiting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.yiting.R;
import com.example.yiting.adapter.MyShareAdapter;
import com.example.yiting.adapter.ShareOderAdapter;
import com.example.yiting.bean.ShareInfo;
import com.example.yiting.bean.ShareOrderInfo;
import com.example.yiting.bean.User;
import com.example.yiting.bean.UserSharePark;
import com.example.yiting.utils.Constant;
import com.example.yiting.utils.SpUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class MyShareActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.lv)
    ListView lv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.ll_no)
    LinearLayout llNo;
    @BindView(R.id.root)
    RelativeLayout root;

    private MyShareAdapter adapter;
    private List<UserSharePark> userShareParks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_share);
        ButterKnife.bind(this);

        tvTitle.setText("我共享的车位");
        adapter = new MyShareAdapter(this, null);
        lv.setAdapter(adapter);
        setClick();
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
    }

    public void getData(RefreshLayout refreshLayout) {
        int userId = SpUtils.getInt(this, Constant.USERID);
        OkHttpUtils.get().url(Constant.GET_USER_SHARE_PARK)
                .addParams("userId", userId + "")
                .addParams("pageNo", "1")
                .addParams("pageSize", "99")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if(refreshLayout != null) {
                            refreshLayout.finishRefresh(false);
                        }
                        Toast.makeText(MyShareActivity.this, "网络出错了", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if(refreshLayout != null) {
                            refreshLayout.finishRefresh();
                        }
                        JSONObject jsonObject = JSON.parseObject(response);
                        String str = jsonObject.getString("shareParks");
                        userShareParks = JSON.parseArray(str, UserSharePark.class);
                        if (userShareParks != null && userShareParks.size() > 0) {
                            adapter.setData(userShareParks);
                            llNo.setVisibility(View.GONE);
                        }
                    }
                });
    }

    public void setClick() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShareInfo shareInfo = new ShareInfo();
                shareInfo.setUserSharePark(userShareParks.get(position));
                User user = new User();
                user.setName("我");
                shareInfo.setUser(user);
                Intent intent = new Intent(MyShareActivity.this, PersonShareDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("info", shareInfo);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        adapter.setOnLookBookClickListener(new MyShareAdapter.OnLookBookClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(MyShareActivity.this, LookOrdersActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("info", userShareParks.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        adapter.setOnDeleteClickListenr(new MyShareAdapter.OnDeleteClickListener() {
            @Override
            public void onClick(View view, int position) {

            }
        });
    }

    @OnClick({R.id.rl_back, R.id.tv_right, R.id.btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
               finish();
                break;
            case R.id.btn:
                startActivity(new Intent(this, ReleaseParkActivity.class));
                break;
        }
    }
}
