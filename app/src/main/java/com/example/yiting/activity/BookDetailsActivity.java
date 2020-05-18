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
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.yiting.bean.User;
import com.example.yiting.bean.UserSharePark;
import com.example.yiting.utils.Constant;
import com.example.yiting.utils.UIUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zyyoona7.popup.EasyPopup;
import com.zyyoona7.popup.XGravity;
import com.zyyoona7.popup.YGravity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

public class BookDetailsActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.cv)
    CircleImageView cv;
    @BindView(R.id.tv_name)
    TextView tvName;
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
    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.ll_pic)
    LinearLayout llPic;
    @BindView(R.id.btn)
    Button btn;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;
    @BindView(R.id.ll_load)
    LinearLayout llLoad;
    @BindView(R.id.root)
    RelativeLayout root;
    @BindView(R.id.iv_verify)
    ImageView ivVerify;
    @BindView(R.id.tv_verify_status)
    TextView tvVerifyStatus;
    @BindView(R.id.tv_verify_time)
    TextView tvVerifyTime;
    @BindView(R.id.ll_verify)
    LinearLayout llVerify;

    private ShareOrderInfo shareOrderInfo;
    private String[] status = {"未开始", "进行中", "停车结束", "已退订"};
    private String[] verifyStatus = {"","待审核", "审核通过", "审核未通过"};
    private int statu = 0;
    private EasyPopup pop;
    private EasyPopup pop1;
    private Uri mOutPutFileUri;
    private String picturePath;
    private Bitmap bitmap;
    private static final int TAKE_PICTURE = 0;
    private static final int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        ButterKnife.bind(this);

        shareOrderInfo = (ShareOrderInfo) getIntent().getSerializableExtra("info");
        tvTitle.setText("预定详情");
        initView();
    }

    public void initView() {
        User user = shareOrderInfo.getUser();
        UserSharePark userSharePark = shareOrderInfo.getUserSharePark();
        ShareOrder shareOrder = shareOrderInfo.getShareOrder();
        statu = shareOrder.getStatus();
        if (statu == 1 || statu == 2) {
            llPic.setVisibility(View.VISIBLE);
            llVerify.setVisibility(View.VISIBLE);
        }
        if (statu == 0) {
            llPic.setVisibility(View.GONE);
        }
        Glide.with(this).load(Constant.BASE + user.getAvatar())
                .error(R.drawable.touxiang)
                .into(cv);
        tvName.setText(user.getName());
        tvDay.setText(userSharePark.getDay());
        tvStartTime.setText(shareOrder.getStarttime());
        tvEndTime.setText(shareOrder.getEndtime());
        tvStatus.setText(status[shareOrder.getStatus()]);
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
                if (ContextCompat.checkSelfPermission(BookDetailsActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(BookDetailsActivity.this, new String[]{Manifest.permission.CAMERA}, 102);
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
                if (ContextCompat.checkSelfPermission(BookDetailsActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(BookDetailsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
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

        View view1 = View.inflate(this, R.layout.pop_timeout, null);
        pop1 = new EasyPopup(this)
                .setContentView(view1)
                .setWidth(ViewGroup.LayoutParams.MATCH_PARENT - UIUtils.dp2px(25))
                .setBackgroundDimEnable(true)
                .setDimValue(0.2f)
                .setFocusAndOutsideEnable(false)
                .apply();

        checkPic();
    }

    public void checkPic() {
        if (statu == 3 || statu == 0) {
            llBottom.setVisibility(View.GONE);
            return;
        }
        OkHttpUtils.get().url(Constant.GET_UpPicByOrderId)
                .addParams("orderId", shareOrderInfo.getShareOrder().getId() + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(BookDetailsActivity.this, "网络出错了", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JSONObject jsonObject = JSON.parseObject(response);
                        if (jsonObject.getInteger("code") == 200) {
                            Log.e("TAG", response);
                            String bookerUpPic = jsonObject.getString("bookerUpPic");
                            if (!TextUtils.isEmpty(bookerUpPic)) {
                                llPic.setVisibility(View.GONE);
                                llPic.setVisibility(View.VISIBLE);
                                Glide.with(BookDetailsActivity.this)
                                        .load(Constant.BASE + bookerUpPic)
                                        .error(R.drawable.no_pic)
                                        .into(iv);
                            }
                            if (TextUtils.isEmpty(jsonObject.getString("announcerUpPic"))) {
                                llVerify.setVisibility(View.GONE);
                                return;
                            }
                            llBottom.setVisibility(View.GONE);
                            tvVerifyTime.setText(jsonObject.getString("upVerifyTime"));
                            tvVerifyStatus.setText(verifyStatus[jsonObject.getInteger("verifyStatus")]);
                            Glide.with(BookDetailsActivity.this)
                                    .load(Constant.BASE + jsonObject.getString("announcerUpPic"))
                                    .error(R.drawable.no_pic)
                                    .into(ivVerify);
                        } else {
                            Toast.makeText(BookDetailsActivity.this, "出错了", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

        mOutPutFileUri = getUriForFile(BookDetailsActivity.this, file);
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
                        showVerify();
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
                        showVerify();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public void showVerify() {
        pop1.showAtAnchorView(root, YGravity.CENTER, XGravity.CENTER, 0, 0);
        ImageView iv = pop1.findViewById(R.id.iv);
        if (bitmap != null) {
            iv.setImageBitmap(bitmap);
        }
        EditText et_remark = pop1.findViewById(R.id.et_remark);
        pop1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload(et_remark.getText().toString().trim());
            }
        });
        pop1.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop1.dismiss();
            }
        });
    }

    public void upload(String remark) {
        if (TextUtils.isEmpty(picturePath)) {
            Toast.makeText(this, "图片出错了", Toast.LENGTH_SHORT).show();
            return;
        }
        PostFormBuilder client = OkHttpUtils.post().url(Constant.UP_TIMEOUT);

        if (!TextUtils.isEmpty(remark)) {
            client.addParams("remark", remark);
        }
        client.addFile("pic", picturePath, new File(picturePath))
                .addParams("orderId", shareOrderInfo.getShareOrder().getId() + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(BookDetailsActivity.this, "网络出错了", Toast.LENGTH_SHORT).show();
                        pop1.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JSONObject jsonObject = JSON.parseObject(response);
                        if (jsonObject.getInteger("code") == 200) {
                            Toast.makeText(BookDetailsActivity.this, "上传成功，等待管理员审核", Toast.LENGTH_SHORT).show();
                            btn.setClickable(false);
                            btn.setBackgroundResource(R.drawable.button_enable_bg);
                            llVerify.setVisibility(View.VISIBLE);
                            tvVerifyStatus.setText(verifyStatus[0]);
                            ivVerify.setImageBitmap(bitmap);
                            tvVerifyTime.setText(UIUtils.formatDate1(new Date()));
                        } else {
                            Toast.makeText(BookDetailsActivity.this, "出错了", Toast.LENGTH_SHORT).show();
                        }
                        pop1.dismiss();
                    }
                });
    }

    @OnClick({R.id.rl_back, R.id.btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn:
                pop.showAtAnchorView(root, YGravity.ALIGN_BOTTOM, XGravity.CENTER, 0, 0);
                break;
        }
    }
}
