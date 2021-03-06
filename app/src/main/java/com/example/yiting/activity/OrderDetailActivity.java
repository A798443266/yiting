package com.example.yiting.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.example.yiting.R;
import com.example.yiting.bean.ShareOrder;
import com.example.yiting.bean.ShareOrderInfo;
import com.example.yiting.bean.UserSharePark;
import com.example.yiting.utils.Constant;
import com.example.yiting.utils.UIUtils;
import com.example.yiting.view.CommomDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zyyoona7.popup.EasyPopup;
import com.zyyoona7.popup.XGravity;
import com.zyyoona7.popup.YGravity;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.OkHttpClient;

public class OrderDetailActivity extends AppCompatActivity {

    @BindView(R.id.root)
    RelativeLayout root;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.cv)
    CircleImageView cv;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_day)
    TextView tvDay;
    @BindView(R.id.tv_startTime)
    TextView tvStartTime;
    @BindView(R.id.tv_endTime)
    TextView tvEndTime;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tv_createTime)
    TextView tvCreateTime;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.btn_start)
    Button btnStart;
    @BindView(R.id.btn_end)
    Button btnEnd;
    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.ll_picNo)
    LinearLayout llPicNo;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;
    @BindView(R.id.ll_load)
    LinearLayout llLoad;

    private ShareOrderInfo shareOrderInfo;
    private EasyPopup pop;
    private int statu = 0;
    private static final int TAKE_PICTURE = 0;
    private static final int RESULT_LOAD_IMAGE = 1;
    private Uri mOutPutFileUri;
    private String picturePath;
    private Bitmap bitmap;
    private String[] status = {"未开始", "进行中", "停车结束", "已退订"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        tvTitle.setText("订单详情");
        shareOrderInfo = (ShareOrderInfo) getIntent().getSerializableExtra("info");
        initView();
    }

    public void initView() {
        UserSharePark userSharePark = shareOrderInfo.getUserSharePark();
        ShareOrder shareOrder = shareOrderInfo.getShareOrder();
        statu = shareOrder.getStatus();
        if (statu == 1 || statu == 2) {
            llPicNo.setVisibility(View.VISIBLE);
        }
        if (statu == 0) {
            llPicNo.setVisibility(View.GONE);
        }
        Glide.with(this).load(Constant.BASE + shareOrderInfo.getUser().getAvatar())
                .error(R.drawable.touxiang)
                .into(cv);
        tvName.setText(shareOrderInfo.getUser().getName());
        tvAddress.setText(userSharePark.getProvince() + userSharePark.getCity() + userSharePark.getAddress());
        tvDay.setText(userSharePark.getDay());
        tvStartTime.setText(shareOrder.getStarttime());
        tvEndTime.setText(shareOrder.getEndtime());
        tvStatus.setText(status[statu]);
        tvCreateTime.setText(shareOrder.getCreatetime());

        View view = View.inflate(this, R.layout.pop_select_photo_way, null);
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
                if (ContextCompat.checkSelfPermission(OrderDetailActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(OrderDetailActivity.this, new String[]{Manifest.permission.CAMERA}, 102);
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
                if (ContextCompat.checkSelfPermission(OrderDetailActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(OrderDetailActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
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

        checkPic();
    }

    public void checkPic() {
        if (statu == 3) {
            llBottom.setVisibility(View.GONE);
            return;
        }
        if (statu == 2) {
            llBottom.setVisibility(View.GONE);
        }
        if (statu == 1 || statu == 2) {
            btnEnd.setClickable(true);
            btnStart.setClickable(false);
            btnStart.setBackgroundResource(R.drawable.button_enable_bg);
            btnEnd.setBackgroundResource(R.drawable.button_bg);
            OkHttpUtils.get().url(Constant.GET_UpPicByOrderId)
                    .addParams("orderId", shareOrderInfo.getShareOrder().getId() + "")
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Toast.makeText(OrderDetailActivity.this, "网络出错了", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            JSONObject jsonObject = JSON.parseObject(response);
                            if (jsonObject.getInteger("code") == 200) {
                                String picUrl = jsonObject.getString("bookerUpPic");
                                if (TextUtils.isEmpty(picUrl)) {
                                    llPicNo.setVisibility(View.GONE);
                                    return;
                                }
                                llPicNo.setVisibility(View.VISIBLE);
                                Glide.with(OrderDetailActivity.this)
                                        .load(Constant.BASE + picUrl)
                                        .error(R.drawable.no_pic)
                                        .into(iv);
                            } else {

                            }
                        }
                    });
        }
    }

    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mOutPutFileUri = getUriForFile(OrderDetailActivity.this, file);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == RESULT_OK) {
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mOutPutFileUri));
                        bitmap = UIUtils.compressImage(bitmap);
                        changeStatusToStart();
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
                    picturePath = c.getString(i);
                    c.close();
                    try {
                        bitmap = UIUtils.getBitmapFormUri(this, uri);
                        changeStatusToStart();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }


    @OnClick({R.id.rl_back, R.id.btn_start, R.id.btn_end})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_start:
                start();
                break;
            case R.id.btn_end:
                end();
                break;
        }
    }

    public void changeStatusToStart() {
        llLoad.setVisibility(View.VISIBLE);
        PostFormBuilder client = OkHttpUtils.post().url(Constant.UPDATE_ORDER_START);
        if (!TextUtils.isEmpty(picturePath)) {
            client.addFile("pic", picturePath, new File(picturePath));
        }
        client.addParams("orderId", shareOrderInfo.getShareOrder().getId() + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        llLoad.setVisibility(View.GONE);
                        Toast.makeText(OrderDetailActivity.this, "网络出错了", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        llLoad.setVisibility(View.GONE);
                        JSONObject jsonObject = JSON.parseObject(response);
                        if (jsonObject.getInteger("code") == 200) {
                            if (bitmap != null) {
                                llPicNo.setVisibility(View.VISIBLE);
                                iv.setImageBitmap(bitmap);
                            }
                            tvStatus.setText(status[1]);
                            btnEnd.setClickable(true);
                            btnStart.setClickable(false);
                            btnStart.setBackgroundResource(R.drawable.button_enable_bg);
                            btnEnd.setBackgroundResource(R.drawable.button_bg);
                            EventBus.getDefault().post(new ShareOrder());
                        } else {
                            Toast.makeText(OrderDetailActivity.this, "出错了", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void changeStatusToEnd() {
        llLoad.setVisibility(View.VISIBLE);
        OkHttpUtils.post().url(Constant.UPDATE_ORDER_END)
                .addParams("orderId", shareOrderInfo.getShareOrder().getId() + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        llLoad.setVisibility(View.GONE);
                        Toast.makeText(OrderDetailActivity.this, "网络出错了", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        llLoad.setVisibility(View.GONE);
                        JSONObject jsonObject = JSON.parseObject(response);
                        if (jsonObject.getInteger("code") == 200) {
                            tvStatus.setText(status[2]);
                            btnEnd.setClickable(false);
                            btnStart.setClickable(false);
                            btnStart.setBackgroundResource(R.drawable.button_enable_bg);
                            btnEnd.setBackgroundResource(R.drawable.button_enable_bg);
                            EventBus.getDefault().post(new ShareOrder());
                        } else {
                            Toast.makeText(OrderDetailActivity.this, "出错了", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void start() {
        new CommomDialog(OrderDetailActivity.this, R.style.dialog, "您要拍照上传停车的图片吗？以便车主方便查看停车情况", new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    dialog.dismiss();
                    pop.showAtAnchorView(root, YGravity.ALIGN_BOTTOM, XGravity.CENTER, 0, 0);
                } else {
                    changeStatusToStart();
                }
            }
        }).setTitle("提示").setNegativeButton("不用了").show();
    }

    public void end() {
        new CommomDialog(OrderDetailActivity.this, R.style.dialog, "您确定要结束本次停车吗？", new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    dialog.dismiss();
                    changeStatusToEnd();
                }
            }
        }).setTitle("提示").show();
    }
}
