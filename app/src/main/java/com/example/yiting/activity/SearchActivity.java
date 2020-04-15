package com.example.yiting.activity;

import android.app.Dialog;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.carlos.voiceline.mylibrary.VoiceLineView;
import com.example.yiting.R;
import com.example.yiting.adapter.SearchRecordAdapter;
import com.example.yiting.adapter.SearchSugAdapter;
import com.example.yiting.bean.RecordInfo;
import com.example.yiting.bean.SearchBean;
import com.example.yiting.model.Model;
import com.example.yiting.utils.Constant;
import com.example.yiting.utils.JsonParser;
import com.example.yiting.utils.SpUtils;
import com.example.yiting.utils.UIUtils;
import com.example.yiting.view.CommomDialog;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.zyyoona7.popup.EasyPopup;
import com.zyyoona7.popup.XGravity;
import com.zyyoona7.popup.YGravity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    @BindView(R.id.rl)
    RelativeLayout rl;

    private SuggestionSearch mSuggestionSearch = null;
    private SearchSugAdapter adapter;
    private SearchRecordAdapter searchdapter;
    private List<SearchBean> infos = new ArrayList<>();
    private String city;
    private List<RecordInfo> records;
    private Boolean isShowHistory = true;

    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private SpeechRecognizer speechRecognizer;
    VoiceLineView voicLine;
    EasyPopup mCirclePop;
    TextView tv;
    TextView tv1;
    ImageView yuyin;

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
                showPop();
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


    private void showPop() {
        View view = View.inflate(this, R.layout.pop_yuyin, null);
        voicLine = view.findViewById(R.id.voicLine);
        tv = view.findViewById(R.id.tv);
        tv1 = view.findViewById(R.id.tv1);
        yuyin = view.findViewById(R.id.iv_yuyin);
        mCirclePop = new EasyPopup(this)
                .setContentView(view)
                .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setHeight(UIUtils.dp2px(300))
                .setBackgroundDimEnable(true)
                .setAnimationStyle(R.style.popyuyin_animation)
                .setDimValue(0.4f)
                .setFocusAndOutsideEnable(true)
                .apply();
        mCirclePop.showAtAnchorView(rl, YGravity.ALIGN_BOTTOM, XGravity.CENTER, 0, 0);
        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (speechRecognizer.isListening())
                    speechRecognizer.stopListening();
                if (mCirclePop != null && mCirclePop.isShowing())
                    mCirclePop.dismiss();
            }
        });
        speechRecognizer = SpeechRecognizer.createRecognizer(this, null);
        mIatResults.clear();
        setSpeechParam();
        int ret = speechRecognizer.startListening(mRecoListener);
        if (ret != ErrorCode.SUCCESS) {
            //此时听写失败
            Toast.makeText(SearchActivity.this, "听写失败", Toast.LENGTH_SHORT).show();
            mCirclePop.dismiss();
        }
    }

    private void setSpeechParam() {
        speechRecognizer.setParameter(SpeechConstant.DOMAIN, "iat");
        speechRecognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        speechRecognizer.setParameter(SpeechConstant.ACCENT, "mandarin ");
    }

    private MediaRecorder mediaRecorder = new MediaRecorder();
    private RecognizerListener mRecoListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
            voicLine.setVolume(i);
        }

        @Override
        public void onBeginOfSpeech() {
            try {
                if (mediaRecorder == null) mediaRecorder = new MediaRecorder();
                mediaRecorder.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            voicLine.setVolume(getDB());
            voicLine.run();
        }

        @Override
        public void onEndOfSpeech() {
            if (mediaRecorder != null) {
                try {
                    mediaRecorder.stop();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    mediaRecorder = null;
                    mediaRecorder = new MediaRecorder();
                }
                mediaRecorder.release();
                mediaRecorder = null;
            }

        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            String results = recognizerResult.getResultString();//语音解析的结果（json数据）
            String text = JsonParser.parseIatResult(results);//解析json数据
            String sn = null;
            // 读取json结果中的sn字段
            try {
                JSONObject resultJson = new JSONObject(results);
                sn = resultJson.optString("sn");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mIatResults.put(sn, text);
            StringBuffer resultBuffer = new StringBuffer();
            for (String key : mIatResults.keySet()) {
                resultBuffer.append(mIatResults.get(key));
            }
            String s = resultBuffer.toString();
            if (TextUtils.isEmpty(s))
                return;
            String s1 = s.substring(0, s.length() - 1);
            atv.setText(s1);
            atv.setSelection(atv.length());
            if (speechRecognizer.isListening())
                speechRecognizer.stopListening();
            mCirclePop.dismiss();
        }

        @Override
        public void onError(final SpeechError speechError) {
            if (speechRecognizer.isListening())
                speechRecognizer.stopListening();
            yuyin.setVisibility(View.VISIBLE);
            tv.setText("我没有听清哦");
            tv1.setVisibility(View.GONE);
            voicLine.setVisibility(View.GONE);
            yuyin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv.setText("正在倾听");
                    tv1.setVisibility(View.VISIBLE);
                    voicLine.setVisibility(View.VISIBLE);
                    yuyin.setVisibility(View.GONE);
                    speechRecognizer.startListening(mRecoListener);
                }
            });
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };


    public int getDB() {
        if (mediaRecorder == null) return 0;
        double ratio = (double) mediaRecorder.getMaxAmplitude();
        double db = 0;
        if (ratio > 1) db = 20 * Math.log10(ratio);
        return (int) db;
    }


}
