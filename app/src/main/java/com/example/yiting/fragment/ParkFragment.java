package com.example.yiting.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;
import androidx.cardview.widget.CardView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.yiting.R;
import com.example.yiting.activity.ParkInfoDetailActivity;
import com.example.yiting.activity.PersonShareDetailActivity;
import com.example.yiting.activity.SearchActivity;
import com.example.yiting.bean.ParkingLot;
import com.example.yiting.bean.SearchBean;
import com.example.yiting.bean.ShareInfo;
import com.example.yiting.bean.UserSharePark;
import com.example.yiting.utils.Constant;
import com.example.yiting.utils.UIUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zyyoona7.popup.EasyPopup;
import com.zyyoona7.popup.XGravity;
import com.zyyoona7.popup.YGravity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

public class ParkFragment extends BaseFragment {
    @BindView(R.id.bmapView)
    MapView mapView;
    @BindView(R.id.iv_lk)
    ImageView ivLk;
    @BindView(R.id.cv_lk)
    CardView cvLk;
    @BindView(R.id.root)
    RelativeLayout root;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.rg)
    RadioGroup rg;

    private EasyPopup popShare;
    private EasyPopup popPark;
    private boolean openLk = false;
    private BaiduMap map;
    private LocationClient mLocationClient;
    private BDLocation currentLocation = null; //存放当前定位的位置信息
    private LatLng currentCenter;//当前屏幕的中心点
    private Boolean isLoad = false;

    private Marker curClickMark = null; //当前点击的Marker
    private int showStatus = 1;
    List<Marker> parkMarkers = new ArrayList<>();
    List<Marker> shareMarkers = new ArrayList<>();

    @Override
    protected void initView() {

        EventBus.getDefault().register(this);
        map = mapView.getMap();
        // 设置初始缩放级别
        MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(15);
        map.animateMapStatus(u);
        // 不显示地图缩放控件（按钮控制栏）
        mapView.showZoomControls(false);
        // 隐藏百度的LOGO
        View child = mapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
        initLocation();
        setMarkClick();
        initPopView();
        setRgChangeListener();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_park;
    }

    //初始化定位
    public void initLocation() {
        map.setMyLocationEnabled(true);
        //定位初始化
        mLocationClient = new LocationClient(mContext);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        MyLocationListener myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);
        mLocationClient.start();
    }

    @OnClick({R.id.cv_lk, R.id.cv_position, R.id.cv_search, R.id.cv_load})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cv_lk:
                openLk = !openLk;
                map.setTrafficEnabled(openLk);
                if (openLk) {
                    ivLk.setBackgroundResource(R.drawable.icon_lukuang1);
                } else {
                    ivLk.setBackgroundResource(R.drawable.icon_lukuang);
                }
                break;
            case R.id.cv_position:
                navigateTo();
                break;

            case R.id.cv_search:
                startActivity(new Intent(mContext, SearchActivity.class));
                getActivity().overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                break;
            case R.id.cv_load:  //加载屏幕中心点附近的车位
                if (currentCenter == null) return;
                if (isLoad) return;  //防止短时间内重复点击
                isLoad = true;
                getAllShare(currentCenter.latitude, currentCenter.longitude);
                break;
        }
    }

    public void setMarkClick() {
        map.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                curClickMark = marker;
                marker.setScale(1.5f);
                Bundle bundle = marker.getExtraInfo();
                if (bundle == null) return false;
                Object obj = bundle.getSerializable("info");
                if (obj == null) return false;

                if (obj instanceof ShareInfo) {
                    showUserShareInfo((ShareInfo) obj);
                } else {
                    showParkInfo((ParkingLot) obj);
                }

                return false;
            }
        });
    }

    class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            currentLocation = location;
            //mapView 销毁后不在处理新接收的位置
            if (location == null || mapView == null) {
                return;
            }
//            MyLocationData locData = new MyLocationData.Builder()
//                .accuracy(location.getRadius())
//                // 此处设置开发者获取到的方向信息，顺时针0-360
//                .direction(location.getDirection())
//                .latitude(location.getLatitude())
//                .longitude(location.getLongitude())
//                .build();
//            map.setMyLocationData(locData);

            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(15.0f);
            map.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

        }
    }

    /*定位到当前位置*/
    private void navigateTo() {
        if (currentLocation == null)
            return;
        LatLng ll = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
        map.animateMapStatus(update);
        update = MapStatusUpdateFactory.zoomTo(15f);
        map.animateMapStatus(update);
        // 显示当前的定位点图标
//        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
//        locationBuilder.latitude(currentLocation.getLatitude());
//        locationBuilder.longitude(currentLocation.getLongitude());
//        MyLocationData locationData = locationBuilder.build();
//        map.setMyLocationData(locationData);
    }

    public void showUserShareInfo(ShareInfo shareInfo) {
        popShare.showAtAnchorView(root, YGravity.ALIGN_BOTTOM, XGravity.CENTER, 0, UIUtils.dp2px(35));
        List<View> pop1Views = new ArrayList<>();
        pop1Views.add(popShare.findViewById(R.id.btn1));
        pop1Views.add(popShare.findViewById(R.id.btn2));
        pop1Views.add(popShare.findViewById(R.id.ll_detail));
        pop1Views.add(popShare.findViewById(R.id.root));

        TextView tv_name = popShare.findViewById(R.id.tv_name);
        TextView tv_address = popShare.findViewById(R.id.tv_address);
        TextView tv_day = popShare.findViewById(R.id.tv_day);
        TextView tv_startTime = popShare.findViewById(R.id.tv_startTime);
        TextView tv_endtime = popShare.findViewById(R.id.tv_endtime);
        TextView tv_price = popShare.findViewById(R.id.tv_price);

        UserSharePark userSharePark = shareInfo.getUserSharePark();
        tv_name.setText(shareInfo.getUser().getName());
        tv_address.setText(userSharePark.getProvince() + userSharePark.getCity() + "市" + userSharePark.getAddress());
        tv_day.setText(userSharePark.getDay());
        tv_startTime.setText(userSharePark.getStarttime());
        tv_endtime.setText(userSharePark.getEndtime());
        tv_price.setText(userSharePark.getPrice() + "");

        for (int i = 0; i < pop1Views.size(); i++) {
            pop1Views.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PersonShareDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("info", shareInfo);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    popShare.dismiss();
                }
            });
        }
    }

    public void showParkInfo(ParkingLot parkingLot) {
        popPark.showAtAnchorView(root, YGravity.ALIGN_BOTTOM, XGravity.CENTER, 0, UIUtils.dp2px(35));
        List<View> pop1Views = new ArrayList<>();
        pop1Views.add(popPark.findViewById(R.id.btn1));
        pop1Views.add(popPark.findViewById(R.id.btn2));
        pop1Views.add(popPark.findViewById(R.id.ll_detail));
        pop1Views.add(popPark.findViewById(R.id.root));

        TextView tv_name = popPark.findViewById(R.id.tv_name);
        TextView tv_address = popPark.findViewById(R.id.tv_address);
        TextView tv_cur = popPark.findViewById(R.id.tv_cur);

        tv_name.setText(parkingLot.getName());
        tv_cur.setText(parkingLot.getCurrent() + "");
        tv_address.setText(parkingLot.getProvince() + parkingLot.getCity() + "市" + parkingLot.getAddress());

        for (int i = 0; i < pop1Views.size(); i++) {
            pop1Views.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ParkInfoDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("info", parkingLot);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    popPark.dismiss();
                }
            });
        }
    }

    public void initPopView() {

        View view = View.inflate(mContext, R.layout.pop_personal_share, null);
        popShare = new EasyPopup(mContext)
                .setContentView(view)
                .setWidth(UIUtils.dp2px(335))
                .setBackgroundDimEnable(true)
                .setAnimationStyle(R.style.pop_park_info_animation)
                .setDimValue(0.2f)
                .setFocusAndOutsideEnable(true)
                .apply();
        popShare.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popPark.dismiss();
            }
        });
        popShare.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (curClickMark != null) {
                    curClickMark.setScale(1.0f);
                }
            }
        });


        View view1 = View.inflate(mContext, R.layout.pop_park_info, null);
        popPark = new EasyPopup(mContext)
                .setContentView(view1)
                .setWidth(UIUtils.dp2px(335))
                .setBackgroundDimEnable(true)
                .setAnimationStyle(R.style.pop_park_info_animation)
                .setDimValue(0.2f)
                .setFocusAndOutsideEnable(true)
                .apply();

        popPark.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popPark.dismiss();
            }
        });
        popPark.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (curClickMark != null) {
                    curClickMark.setScale(1.0f);
                }
            }
        });

    }

    // 获取搜索页面传递过来的信息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SearchBean info) {
        tvAddress.setText(info.getKey());
        LatLng point = new LatLng(info.getLatitude(), info.getLongitude());
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(point);
        map.animateMapStatus(update);

        getAllShare(info.getLatitude(), info.getLongitude());
    }

    public void getAllShare(double latitude, double longitude) {
//        Log.e("TAG", latitude + ", " + longitude);
        OkHttpUtils.get().url(Constant.GETALLSHARE)
                .addParams("latitude", latitude + "")
                .addParams("longitude", longitude + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        isLoad = false;
                        Toast.makeText(mContext, "网络出错了", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JSONObject jsonObject = JSON.parseObject(response);
                        String shareStr = jsonObject.getString("userShare");
                        String parkStr = jsonObject.getString("parkingLot");
                        shareMarkers.clear();
                        parkMarkers.clear();
                        if (TextUtils.isEmpty(shareStr) && TextUtils.isEmpty(parkStr)) {
                            Toast.makeText(mContext, "当前附近没有找到任何停车位", Toast.LENGTH_SHORT).show();
                            isLoad = false;
                            return;
                        }
                        //清除上一次的marker
                        map.clear();
                        //用户共享车位
                        if (!TextUtils.isEmpty(shareStr)) {
                            List<ShareInfo> shareInfos = JSON.parseArray(shareStr, ShareInfo.class);
                            addShareMarker(shareInfos);
                        }
                        //停车场
                        if (!TextUtils.isEmpty(parkStr)) {
                            List<ParkingLot> parkingLots = JSON.parseArray(parkStr, ParkingLot.class);
                            addParkMarker(parkingLots);
                        }
                        isLoad = false;

                    }
                });
    }

    public void addShareMarker(List<ShareInfo> shareInfos) {
        if (shareInfos == null || shareInfos.size() == 0) return;
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromView(getActivity().getLayoutInflater().inflate(R.layout.icon_share, null));
        for (int i = 0; i < shareInfos.size(); i++) {
            ShareInfo shareInfo = shareInfos.get(i);
            LatLng point = new LatLng(shareInfo.getUserSharePark().getLatitude(),
                    shareInfo.getUserSharePark().getLongitude());

            Bundle bundle = new Bundle();
            bundle.putSerializable("info", shareInfo);
            OverlayOptions option = new MarkerOptions()
                    .position(point)
                    .extraInfo(bundle)
                    .animateType(MarkerOptions.MarkerAnimateType.jump)
                    .icon(bitmap);

            Marker marker = (Marker) map.addOverlay(option);
            shareMarkers.add(marker);
        }

    }

    public void addParkMarker(List<ParkingLot> parkingLots) {
        if (parkingLots == null || parkingLots.size() == 0) return;
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromView(getActivity().getLayoutInflater().inflate(R.layout.icon_park, null));
        for (int i = 0; i < parkingLots.size(); i++) {
            ParkingLot parkingLot = parkingLots.get(i);
            LatLng point = new LatLng(parkingLot.getLatitude(), parkingLot.getLongitude());

            Bundle bundle = new Bundle();
            bundle.putSerializable("info", parkingLot);
            OverlayOptions option = new MarkerOptions()
                    .position(point)
                    .extraInfo(bundle)
                    .animateType(MarkerOptions.MarkerAnimateType.jump)
                    .icon(bitmap);

            Marker marker = (Marker) map.addOverlay(option);
            parkMarkers.add(marker);
        }

    }

    // 设置RadioButton改变监听，筛选显示
    public void setRgChangeListener() {
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb1:
                        if (showStatus == 1) return;
                        if (showStatus == 2) {
                            for (int i = 0; i < parkMarkers.size(); i++) {
                                parkMarkers.get(i).setVisible(true);
                                parkMarkers.get(i).setClickable(true);
                            }
                        } else {
                            for (int i = 0; i < shareMarkers.size(); i++) {
                                shareMarkers.get(i).setVisible(true);
                                shareMarkers.get(i).setClickable(true);
                            }
                        }
                        showStatus = 1;
                        break;
                    case R.id.rb2:
                        if (showStatus == 2) return;
                        if (showStatus == 1) {
                            for (int i = 0; i < parkMarkers.size(); i++) {
                                parkMarkers.get(i).setVisible(false);
                                parkMarkers.get(i).setClickable(false);
                            }
                        } else {
                            for (int i = 0; i < parkMarkers.size(); i++) {
                                parkMarkers.get(i).setVisible(false);
                                parkMarkers.get(i).setClickable(false);
                            }
                            for (int i = 0; i < shareMarkers.size(); i++) {
                                shareMarkers.get(i).setVisible(true);
                                shareMarkers.get(i).setClickable(true);
                            }
                        }
                        showStatus = 2;
                        break;
                    case R.id.rb3:
                        if (showStatus == 3) return;
                        if (showStatus == 1) {
                            for (int i = 0; i < shareMarkers.size(); i++) {
                                shareMarkers.get(i).setVisible(false);
                                shareMarkers.get(i).setClickable(false);
                            }
                        } else {
                            for (int i = 0; i < shareMarkers.size(); i++) {
                                shareMarkers.get(i).setVisible(false);
                                shareMarkers.get(i).setClickable(false);
                            }
                            for (int i = 0; i < parkMarkers.size(); i++) {
                                parkMarkers.get(i).setVisible(true);
                                parkMarkers.get(i).setClickable(true);
                            }
                        }
                        showStatus = 3;
                        break;
                }
            }
        });

        map.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                currentCenter = mapStatus.target;
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
//        mLocationClient.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        mLocationClient.stop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        mLocationClient.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null)
            mapView.onDestroy();
        map.setMyLocationEnabled(false);
        mLocationClient.stop();
        EventBus.getDefault().unregister(this);
    }
}
