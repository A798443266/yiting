package com.example.yiting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.yiting.R;
import com.example.yiting.adapter.InformationAdapter;
import com.example.yiting.bean.Information;
import com.example.yiting.utils.Constant;
import com.example.yiting.utils.SpUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class InformationAllActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.lv)
    ListView lv;
    @BindView(R.id.ll_load)
    LinearLayout llLoad;

    private InformationAdapter adapter;
    private List<Information> informations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_all);
        ButterKnife.bind(this);
        tvTitle.setText("资讯");

        adapter = new InformationAdapter(this, null);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(InformationAllActivity.this, InformationDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("info", informations.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        getInformation();
    }

    public void getInformation() {
        llLoad.setVisibility(View.VISIBLE);
        OkHttpUtils.get().url(Constant.GET_INFORMATION)
                .addParams("pageNo", "1")
                .addParams("pageSize", "10")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        llLoad.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        llLoad.setVisibility(View.GONE);
                        parseJson(response);
                    }
                });
    }

    public void parseJson(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        String str = jsonObject.getString("information");
        informations = JSON.parseArray(str, Information.class);
        adapter.setData(informations);
    }

    @OnClick(R.id.rl_back)
    public void onViewClicked() {
        finish();
    }
}
