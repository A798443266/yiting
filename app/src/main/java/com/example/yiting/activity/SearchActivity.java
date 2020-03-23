package com.example.yiting.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.example.yiting.R;
import com.example.yiting.adapter.SearchRecordAdapter;
import com.example.yiting.adapter.SearchSugAdapter;
import com.example.yiting.bean.RecordInfo;
import com.example.yiting.bean.SearchBean;
import com.example.yiting.model.Model;
import com.example.yiting.utils.Constant;
import com.example.yiting.utils.SpUtils;
import com.example.yiting.view.CommomDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.atv)
    AutoCompleteTextView atv;
    @BindView(R.id.lv)
    ListView lv;
    @BindView(R.id.lv_history)
    ListView lvHistory;
    @BindView(R.id.tv_clear)
    TextView tvClear;
    @BindView(R.id.ll_history)
    LinearLayout llHistory;

    private SuggestionSearch mSuggestionSearch = null;
    private SearchSugAdapter adapter;
    private SearchRecordAdapter searchdapter;
    private List<SearchBean> infos = new ArrayList<>();
    private String city;
    private List<RecordInfo> records;
    private Boolean isShowHistory = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        poiSugSearch();
        init();

    }

    private void poiSugSearch() {
        // 初始化建议搜索模块，注册建议搜索事件监听
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(sugListener);
    }

    public void init() {
        city = SpUtils.getString(this, Constant.CUR_CITY);
        city = TextUtils.isEmpty(city) ? "北京" : city;
        atv.setThreshold(1); //输入一个字就开始提示
        adapter = new SearchSugAdapter(this, infos);
        lv.setAdapter(adapter);
        getRecord();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventBus.getDefault().post(infos.get(position));
                //搜索记录存入本地数据库
                Model.getInstance().getDbDao().addRecord(SpUtils.getInt(SearchActivity.this, Constant.USERID),
                    infos.get(position).getKey());
                finish();
            }
        });

        atv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() <= 0) {
                    infos.clear();
                    adapter.notifyDataSetChanged();
                    lv.setVisibility(View.GONE);
                    llHistory.setVisibility(View.VISIBLE);
                    isShowHistory = true;
                    return;
                }


                // 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                    .keyword(s.toString()) // 关键字
                    .city(city)); // 城市

                if (isShowHistory) {
                    isShowHistory = false;
                    llHistory.setVisibility(View.GONE);
                    lv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    OnGetSuggestionResultListener sugListener = new OnGetSuggestionResultListener() {
        @Override
        public void onGetSuggestionResult(SuggestionResult suggestionResult) {
            if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
                return;
            }
            infos.clear();
            for (SuggestionResult.SuggestionInfo info : suggestionResult.getAllSuggestions()) {
                LatLng point = info.getPt();
                Double latitude = null;
                Double longitude = null;

                if (point != null) {
                    latitude = point.latitude;
                    longitude = point.longitude;
                }

                if (info.getKey() != null && info.getDistrict() != null && info.getCity() != null
                    && latitude != null && longitude != null) {

                    SearchBean sug = new SearchBean();
                    sug.setKey(info.getKey());
                    sug.setCity(info.getCity());
                    sug.setDis(info.getDistrict());
                    sug.setLatitude(latitude);
                    sug.setLongitude(longitude);
                    infos.add(sug);
                }
            }
            adapter.notifyDataSetChanged();

        }
    };

    private void getRecord() {
        int userId = SpUtils.getInt(this, Constant.USERID);
        //从数据库中获取用户的搜索记录
        records = Model.getInstance().getDbDao().queryByUserId(userId);
        if (records != null && records.size() > 0) {
            searchdapter = new SearchRecordAdapter(this, records);
            lvHistory.setAdapter(searchdapter);
            llHistory.setVisibility(View.VISIBLE);
            lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    atv.setText(records.get(position).getContent());
                    atv.setSelection(atv.length());//指针移动到文字末尾
                }
            });
        } else {
//            searchdapter = new SearchRecordAdapter(this, null);
//            lvHistory.setAdapter(adapter);
            llHistory.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.rl_back1, R.id.iv_yuyin, R.id.icon_search, R.id.tv_clear})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back1:
                finish();
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                break;
            case R.id.iv_yuyin:
                break;
            case R.id.icon_search:
                break;
            case R.id.tv_clear:
                new CommomDialog(this, R.style.dialog, "您确定要清空搜索记录吗？", new CommomDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        if (confirm) {
                            dialog.dismiss();
                            Model.getInstance().getDbDao().deleteAll(SpUtils.getInt(SearchActivity.this, Constant.USERID));
                            llHistory.setVisibility(View.GONE);
                        }
                    }
                }).setTitle("提示").show();

                break;
        }
    }

}
