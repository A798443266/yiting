package com.example.yiting.fragment.order;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.yiting.R;
import com.example.yiting.activity.OrderDetailActivity;
import com.example.yiting.activity.PersonShareDetailActivity;
import com.example.yiting.activity.SearchActivity;
import com.example.yiting.adapter.ShareOderAdapter;
import com.example.yiting.bean.ShareInfo;
import com.example.yiting.bean.ShareOrder;
import com.example.yiting.bean.ShareOrderInfo;
import com.example.yiting.bean.UserSharePark;
import com.example.yiting.fragment.BaseFragment;
import com.example.yiting.model.Model;
import com.example.yiting.utils.Constant;
import com.example.yiting.utils.NavigationUtil;
import com.example.yiting.utils.SpUtils;
import com.example.yiting.utils.UIUtils;
import com.example.yiting.view.CommomDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zyyoona7.popup.EasyPopup;
import com.zyyoona7.popup.XGravity;
import com.zyyoona7.popup.YGravity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import okhttp3.Call;

public class OrderFragment1 extends BaseFragment {

    @BindView(R.id.lv)
    ListView lv;
    @BindView(R.id.ll_no)
    LinearLayout llNo;
    @BindView(R.id.root)
    RelativeLayout root;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private ShareOderAdapter adapter;
    private List<ShareOrderInfo> shareOrderInfos;
    private EasyPopup popNav;

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        adapter = new ShareOderAdapter(mContext, null);
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

    @Override
    public int getLayoutId() {
        return R.layout.fragment_order1;
    }

    //收到预定共享车位成功传递过来的消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ShareOrder s) {
        getData(null);
    }

    public void getData(RefreshLayout refreshLayout) {
        int userId = SpUtils.getInt(mContext, Constant.USERID);
        OkHttpUtils.get().url(Constant.GET_SHARE_ORDER)
                .addParams("userId", userId + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (refreshLayout != null) {
                            refreshLayout.finishRefresh(false);
                        }
                        Toast.makeText(mContext, "网络出错了", Toast.LENGTH_SHORT).show();
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

    public void setClick() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, OrderDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("info", shareOrderInfos.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        adapter.setOnDetailClickListener(new ShareOderAdapter.DetailClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(mContext, PersonShareDetailActivity.class);
                Bundle bundle = new Bundle();
                ShareInfo shareInfo = new ShareInfo();
                shareInfo.setUser(shareOrderInfos.get(position).getUser());
                shareInfo.setUserSharePark(shareOrderInfos.get(position).getUserSharePark());
                bundle.putSerializable("info", shareInfo);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        adapter.setOnNavClickListener(new ShareOderAdapter.NavClickListener() {
            @Override
            public void onClick(View view, int position) {
                initPop(shareOrderInfos.get(position).getUserSharePark());
            }
        });
        adapter.setOnTuiDingClickListener(new ShareOderAdapter.TuiDingClickListener() {
            @Override
            public void onClick(View view, int position) {
                new CommomDialog(mContext, R.style.dialog, "您确定要退订吗？退款将返回到您的余额中", new CommomDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        if (confirm) {
                            int userId = SpUtils.getInt(mContext, Constant.USERID);
                            OkHttpUtils.get().url(Constant.TUIDING)
                                    .addParams("orderId", shareOrderInfos.get(position).getShareOrder().getId() + "")
                                    .addParams("userId", userId + "")
                                    .build()
                                    .execute(new StringCallback() {
                                        @Override
                                        public void onError(Call call, Exception e, int id) {
                                            Toast.makeText(mContext, "网络出错了", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }

                                        @Override
                                        public void onResponse(String response, int id) {
                                            JSONObject jsonObject = JSON.parseObject(response);
                                            int code = jsonObject.getInteger("code");
                                            dialog.dismiss();
                                            if(code == 200) {
                                                Toast.makeText(mContext, "退订成功", Toast.LENGTH_SHORT).show();
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

    private void initPop(UserSharePark userSharePark) {
        View view = View.inflate(mContext, R.layout.pop_select_daohang_way, null);
        popNav = new EasyPopup(mContext)
                .setContentView(view)
                .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setHeight(UIUtils.dp2px(159))
                .setBackgroundDimEnable(true)
                .setAnimationStyle(R.style.pop_park_info_animation)
                .setDimValue(0.2f)
                .setFocusAndOutsideEnable(true)
                .apply();

        popNav.findViewById(R.id.ll1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double longitude = userSharePark.getLongitude();
                double latitude = userSharePark.getLatitude();
                NavigationUtil.navBaidu(mContext, longitude, latitude);
                popNav.dismiss();
            }
        });
        popNav.findViewById(R.id.ll2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double longitude = userSharePark.getLongitude();
                double latitude = userSharePark.getLatitude();
                NavigationUtil.navGaode(mContext, longitude, latitude);
                popNav.dismiss();
            }
        });

        popNav.findViewById(R.id.ll3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popNav.dismiss();
            }
        });
        popNav.showAtAnchorView(root, YGravity.ALIGN_BOTTOM, XGravity.CENTER, 0, UIUtils.dp2px(56));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
