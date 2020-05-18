package com.example.yiting.activity;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.yiting.adapter.LookOderAdapter;
import com.example.yiting.adapter.ShareOderAdapter;
import com.example.yiting.bean.ShareOrderInfo;
import com.example.yiting.bean.UserSharePark;
import com.example.yiting.utils.Constant;
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

public class LookOrdersActivity extends AppCompatActivity {

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

    private UserSharePark userSharePark;
    private List<ShareOrderInfo> shareOrderInfos;
    private LookOderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_orders);
        ButterKnife.bind(this);

        tvTitle.setText("共享车位预定情况");
        userSharePark = (UserSharePark) getIntent().getSerializableExtra("info");
        adapter = new LookOderAdapter(this, null);
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

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(LookOrdersActivity.this, BookDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("info", shareOrderInfos.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    public void getData(RefreshLayout refreshLayout) {
        if (userSharePark == null) return;
        OkHttpUtils.get().url(Constant.GET_SHARE_BOOK_DETAIL)
                .addParams("id", userSharePark.getId() + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (refreshLayout != null) {
                            refreshLayout.finishRefresh(false);
                        }
                        Toast.makeText(LookOrdersActivity.this, "网络出错了", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (refreshLayout != null) {
                            refreshLayout.finishRefresh();
                        }
                        JSONObject jsonObject = JSON.parseObject(response);
                        String str = jsonObject.getString("shareOrderInfos");
                        shareOrderInfos = JSON.parseArray(str, ShareOrderInfo.class);
                        if (shareOrderInfos != null && shareOrderInfos.size() > 0) {
                            adapter.setData(shareOrderInfos);
                            llNo.setVisibility(View.GONE);
                        }
                    }
                });
    }

    @OnClick({R.id.rl_back, R.id.tv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_right:
                break;
        }
    }
}
