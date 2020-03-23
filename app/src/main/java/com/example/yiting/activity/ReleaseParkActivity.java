package com.example.yiting.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.alibaba.fastjson.JSON;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.yiting.R;
import com.example.yiting.adapter.ParkPhotoAdapter;
import com.example.yiting.bean.CityBean;
import com.example.yiting.bean.TimeBean;
import com.example.yiting.utils.Constant;
import com.example.yiting.utils.SpUtils;
import com.example.yiting.utils.UIUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zyyoona7.popup.EasyPopup;
import com.zyyoona7.popup.XGravity;
import com.zyyoona7.popup.YGravity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class ReleaseParkActivity extends AppCompatActivity {

    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.gv)
    GridView gv;
    @BindView(R.id.root)
    RelativeLayout root;
    @BindView(R.id.et_addr)
    EditText etAddr;
    @BindView(R.id.et_number)
    EditText etNumber;
    @BindView(R.id.et_price)
    EditText etPrice;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_description)
    EditText etDescription;
    @BindView(R.id.rg)
    RadioGroup rg;
    @BindView(R.id.ll_load)
    LinearLayout llLoad;
    @BindView(R.id.tv_city)
    TextView tvCity;
    @BindView(R.id.ll_success)
    LinearLayout llSuccess;
    @BindView(R.id.sv)
    ScrollView sv;

    private GeoCoder mCoder = GeoCoder.newInstance();

    private TimePickerView pvTime;
    private OptionsPickerView<String> pvOptions;
    private OptionsPickerView pvCity;
    Calendar startDate = Calendar.getInstance();
    Calendar endDate = Calendar.getInstance();

    private List<String> startTimeArr = new ArrayList<>();
    private List<String> endTimeArr = new ArrayList<>();
    private TimeBean timeBean = new TimeBean();
    private ParkPhotoAdapter adapter;
    private String day;
    private String startTime = "";
    private String endTime = "";
    private String address;
    private String name;
    private String phone;
    private String description;
    private String number;
    private String price;
    private String province;
    private String city;
    private int type = 0;

    private EasyPopup pop;
    private Uri mOutPutFileUri;
    private String picturePath;
    private String path = "";
    private List<Bitmap> bmp = new ArrayList<>();//图片
    public static List<String> add = new ArrayList<>();//图片地址
    private static final int TAKE_PICTURE = 0;
    private static final int RESULT_LOAD_IMAGE = 1;


    private ArrayList<CityBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private Thread thread;
    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;
    private boolean isLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_park);
        ButterKnife.bind(this);
        mHandler.sendEmptyMessage(MSG_LOAD_DATA);
        init();
    }

    public void init() {
        endDate.set(2021, 11, 31);
        for (int i = 0; i < timeBean.getTimes().length; i++) {
            startTimeArr.add(timeBean.getTimes()[i]);
            endTimeArr.add(timeBean.getTimes()[i]);
        }

        adapter = new ParkPhotoAdapter(this, bmp);
        adapter.setOnCloseClickListener(new ParkPhotoAdapter.OnCloseClickListener() {
            @Override
            public void closeClick(View view, int position) {
                bmp.remove(position);
                adapter.notifyDataSetChanged();
            }
        });
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (bmp.size() == position) {
                    showPopSelectPhotoWay();
                } else {

                }
            }
        });


        View view = View.inflate(this, R.layout.pop_select_photo_way, null);

        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                day = UIUtils.formatDate(date);
                tvDate.setText(day);
            }
        })
            .setContentTextSize(20)
            .setTitleText("选择日期")
            .setTitleColor(0xaa000000)//标题文字颜色
            .setSubmitColor(UIUtils.getColor(R.color.systemColor))//确定按钮文字颜色
            .setCancelColor(0x88000000)//取消按钮文字颜色
            .setRangDate(startDate, endDate)//起始终止年月日设定
            .build();

        pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                startTime = startTimeArr.get(options1);
                endTime = endTimeArr.get(options2);
                tvTime.setText(startTime + " ~ " + endTime);
            }
        })
            .setContentTextSize(20)
            .setTitleText("选择共享时段")
            .setTitleColor(0xaa000000)//标题文字颜色
            .setSubmitColor(UIUtils.getColor(R.color.systemColor))//确定按钮文字颜色
            .setCancelColor(0x88000000)//取消按钮文字颜色
            .setSelectOptions(16, 24)
            .build();
        pvOptions.setNPicker(startTimeArr, endTimeArr, null);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb1) {
                    type = 0;
                } else if (checkedId == R.id.rb2) {
                    type = 1;
                }
            }
        });


        pop = new EasyPopup(this)
            .setContentView(view)
            .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
            .setHeight(UIUtils.dp2px(159))
            .setBackgroundDimEnable(true)
            .setAnimationStyle(R.style.pop_park_info_animation)
            .setDimValue(0.2f)
            .setFocusAndOutsideEnable(true)
            .apply();

        //拍照
        pop.findViewById(R.id.ll1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ReleaseParkActivity.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ReleaseParkActivity.this, new String[]{Manifest.permission.CAMERA}, 102);
                } else {
                    takePhoto();
                }

            }
        });

        //从相册选
        pop.findViewById(R.id.ll2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //动态申请获取访问 读写磁盘的权限
                if (ContextCompat.checkSelfPermission(ReleaseParkActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ReleaseParkActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
                } else {
                    takeAlbum();
                }
            }
        });
        //取消
        pop.findViewById(R.id.ll3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });

        //地理编码监听
        mCoder.setOnGetGeoCodeResultListener(listener);
    }

    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(getExternalCacheDir(), "test.jpg");
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mOutPutFileUri = getUriForFile(ReleaseParkActivity.this, file);
        picturePath = file.getAbsolutePath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutPutFileUri);
        startActivityForResult(intent, TAKE_PICTURE);
        pop.dismiss();
    }

    public void takeAlbum() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
        pop.dismiss();
    }

    // 申请权限的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101) { // 存储
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takeAlbum();
            } else {
                pop.dismiss();
            }
        } else if (requestCode == 102) { //相机
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                pop.dismiss();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Android7.0之后Android N对访问文件权限
    private static Uri getUriForFile(Context context, File file) {
        if (context == null || file == null) {
            throw new NullPointerException();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(context.getApplicationContext(), context.getPackageName() + ".fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mOutPutFileUri));
                        bitmap = UIUtils.compressImage(bitmap);
                        bmp.add(bitmap);
                        add.add(picturePath);
                        adapter.notifyDataSetChanged();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
                break;
            case RESULT_LOAD_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String[] files = {MediaStore.Images.Media.DATA};
                    Cursor c = this.getContentResolver().query(uri, files, null, null, null);
                    c.moveToFirst();
                    int i = c.getColumnIndex(files[0]);
                    path = c.getString(i);
                    c.close();

                    try {
                        Bitmap bitmap = UIUtils.getBitmapFormUri(this, uri);
                        add.add(path);
                        bmp.add(bitmap);
                        adapter.notifyDataSetChanged();
                    } catch (IOException e) {
                        Log.e("TAG", e.getMessage());
                        e.printStackTrace();
                    }

                }
                break;
        }
    }


    @OnClick({R.id.rl_back, R.id.ll_date, R.id.ll_time, R.id.btn_submit, R.id.tv_city})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.ll_date:
                pvTime.show();
                break;
            case R.id.ll_time:
                pvOptions.show();
                break;
            case R.id.tv_city:
                showOptionCity();
                break;
            case R.id.btn_submit:
                check();
                break;
        }
    }

    public void showOptionCity() {
        if (!isLoaded) return;
        pvCity = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                province = options1Items.get(options1).getPickerViewText();
                city = options2Items.get(options1).get(options2);
                tvCity.setText(province + " / " + city);
            }
        })
            .setContentTextSize(20)
            .setTitleText("选择省份和城市")
            .setTitleColor(0xaa000000)
            .setSubmitColor(UIUtils.getColor(R.color.systemColor))
            .setCancelColor(0x88000000)
            .build();
        pvCity.setPicker(options1Items, options2Items);
        pvCity.show();
    }

    public void showPopSelectPhotoWay() {
        pop.showAtAnchorView(root, YGravity.ALIGN_BOTTOM, XGravity.CENTER, 0, 0);
    }

    public void check() {
        address = etAddr.getText().toString().trim();
        number = etNumber.getText().toString().trim();
        name = etName.getText().toString().trim();
        phone = etPhone.getText().toString().trim();
        description = etDescription.getText().toString().trim();
        price = etPrice.getText().toString().trim();


        if (TextUtils.isEmpty(address) || TextUtils.isEmpty(number)
            || TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)
            || TextUtils.isEmpty(price) || TextUtils.isEmpty(province)
            || TextUtils.isEmpty(city) || TextUtils.isEmpty(day)
            || TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime)) {

            Toast.makeText(this, "输入信息不完整", Toast.LENGTH_SHORT).show();
            return;
        }

        mCoder.geocode(new GeoCodeOption()
            .city(city)
            .address(address));

    }

    public void submit(double latitude, double longitude) {
        Log.e("TAG", latitude + ", " + longitude);
        Map<String, String> headers = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        Map<String, File> files = new HashMap<>();
        if (add.size() > 0) {
            Log.e("TAG",add.size()+"");
            headers.put("Content-Type", "mutipart/form-data");
            for (int i = 0; i < add.size(); i++) {
                File file = new File(add.get(i));
                String suffix = add.get(i).substring(add.get(i).lastIndexOf("."));
                files.put("pics" + i + suffix, file);
            }
        }

        int userId = SpUtils.getInt(this, Constant.USERID);

        params.put("address", address);
        params.put("number", number);
        params.put("name", name);
        params.put("phone", phone);
        params.put("province", province);
        params.put("city", city);
        params.put("type", type + "");
        params.put("price", price);
        params.put("latitude", latitude + "");
        params.put("longitude", longitude + "");
        params.put("userId", userId + "");
        params.put("day", day);
        params.put("startTime",startTime);
        params.put("endTime",endTime);

        if (!TextUtils.isEmpty(description)) {
            params.put("description", description);
        }

        llLoad.setVisibility(View.VISIBLE);

        OkHttpUtils.post().url(Constant.ADD_SHARE)
            .headers(headers)
            .params(params)
            .files("pics", files)
            .build()
            .execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    llLoad.setVisibility(View.GONE);
                    Toast.makeText(ReleaseParkActivity.this, "网络出错啦~", Toast.LENGTH_SHORT).show();
                    Log.e("TAG", e.getMessage());
                }

                @Override
                public void onResponse(String response, int id) {
//                    llLoad.setVisibility(View.GONE);
//                    sv.setVisibility(View.GONE);
//                    llSuccess.setVisibility(View.VISIBLE);
                    Log.e("TAG", response);
                }
            });
    }

    OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
        //地理编码
        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
            if (geoCodeResult != null && geoCodeResult.getLocation() != null) {
                if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(ReleaseParkActivity.this, "地址解析错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                double latitude = geoCodeResult.getLocation().latitude;
                double longitude = geoCodeResult.getLocation().longitude;
                submit(latitude, longitude);
            } else {
                Toast.makeText(ReleaseParkActivity.this, "地址解析错误", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

        }
    };

    //解析城市数据
    private void initJsonData() {//解析数据

        String CityData = UIUtils.getJson(this, "city.json");//获取assets目录下的json文件数据
        ArrayList<CityBean> jsonBean = parseData(CityData);

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;
        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            for (int c = 0; c < jsonBean.get(i).getCity_list().size(); c++) {//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCity_list().get(c);
                CityList.add(CityName);//添加城市
            }

            options2Items.add(CityList);
        }

        mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);

    }

    public ArrayList<CityBean> parseData(String result) {//Gson 解析
        ArrayList<CityBean> detail = null;
        try {
            detail = (ArrayList<CityBean>) JSON.parseArray(result, CityBean.class);
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }

    //判断地址数据是否获取成功
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (thread == null) {//如果已创建就不再重新创建子线程了
                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 写子线程中的操作,解析省市区数据
                                initJsonData();
                            }
                        });
                        thread.start();
                    }
                    break;
                case MSG_LOAD_SUCCESS:
                    isLoaded = true;
                    break;
                case MSG_LOAD_FAILED:
                    break;

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bmp.clear();
        add.clear();
        mCoder.destroy();
    }
}
