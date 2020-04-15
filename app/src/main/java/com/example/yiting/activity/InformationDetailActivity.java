package com.example.yiting.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.yiting.R;
import com.example.yiting.bean.Information;
import com.example.yiting.utils.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InformationDetailActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_sub_title)
    TextView tvSubTitle;
    @BindView(R.id.tv_source)
    TextView tvSource;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.tv_content)
    TextView tvContent;

    private Information information;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_detail);
        ButterKnife.bind(this);

        information = (Information) getIntent().getSerializableExtra("info");
        if (information == null) return;
        tvTitle.setText(information.getTitle());
        tvSubTitle.setText(information.getSubtitle());
        tvSource.setText(information.getSource());
        tvTime.setText(information.getCreatetime());
        tvContent.setText("\t\t\t" + information.getDescription());
        Glide.with(this)
                .load(Constant.BASE + information.getPic())
                .error(R.drawable.no_pic)
                .into(iv);
    }

    @OnClick({R.id.rl_back, R.id.iv_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.iv_share:
                break;
        }
    }
}
