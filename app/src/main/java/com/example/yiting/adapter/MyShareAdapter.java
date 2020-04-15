package com.example.yiting.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.yiting.R;
import com.example.yiting.bean.Information;
import com.example.yiting.bean.ShareOrder;
import com.example.yiting.bean.ShareOrderInfo;
import com.example.yiting.bean.User;
import com.example.yiting.bean.UserSharePark;
import com.example.yiting.utils.Constant;

import java.util.List;

public class MyShareAdapter extends BaseAdapter {

    private Context context;
    private List<UserSharePark> userShareParks;
    private LayoutInflater listContainer;

    private String[] status = {"待审核", "审核通过", "未通过"};

    public MyShareAdapter(Context context, List<UserSharePark> userShareParks) {
        this.context = context;
        this.userShareParks = userShareParks;
        listContainer = LayoutInflater.from(context);
    }

    public void setData(List<UserSharePark> userShareParks) {
        this.userShareParks = userShareParks;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.userShareParks == null ? 0 : this.userShareParks.size();
    }

    @Override
    public Object getItem(int position) {
        return userShareParks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = listContainer.inflate(R.layout.item_my_share, null);
            holder.iv = convertView.findViewById(R.id.iv);
            holder.tv_name = convertView.findViewById(R.id.tv_name);
            holder.tv_createTime = convertView.findViewById(R.id.tv_createTime);
            holder.tv_status = convertView.findViewById(R.id.tv_status);
            holder.tv_price = convertView.findViewById(R.id.tv_price);
            holder.tv_day = convertView.findViewById(R.id.tv_day);
            holder.tv_startTime = convertView.findViewById(R.id.tv_startTime);
            holder.tv_endTime = convertView.findViewById(R.id.tv_endTime);
            holder.btn1 = convertView.findViewById(R.id.btn1);
            holder.btn2 = convertView.findViewById(R.id.btn2);
            holder.btn3 = convertView.findViewById(R.id.btn3);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        UserSharePark sharePark = userShareParks.get(position);
        String pic = "";
        if (!TextUtils.isEmpty(sharePark.getPic())) {
            pic = sharePark.getPic().split(",")[0];
        }
        Glide.with(context).load(Constant.BASE + pic)
                .error(R.drawable.no_pic)
                .into(holder.iv);
        holder.tv_name.setText(sharePark.getAddress());
        holder.tv_price.setText(sharePark.getPrice() + "");
        holder.tv_status.setText(status[sharePark.getReview()]);
//        holder.tv_createTime.setText(sharePark.getSubmittime());
        holder.tv_day.setText(sharePark.getDay());
        holder.tv_startTime.setText(sharePark.getStarttime());
        holder.tv_endTime.setText(sharePark.getEndtime());

        holder.btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onEditClickListener != null) {
                    onEditClickListener.onClick(v, position);
                }
            }
        });
        holder.btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onLookBookClickListener != null) {
                    onLookBookClickListener.onClick(v, position);
                }
            }
        });
        holder.btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onClick(v, position);
                }
            }
        });
        return convertView;
    }

    static class ViewHolder {
        private ImageView iv;
        private TextView tv_name;
        private TextView tv_status;
        private TextView tv_price;
        private TextView tv_day;
        private TextView tv_startTime;
        private TextView tv_endTime;
        private TextView tv_createTime;
        private Button btn1;
        private Button btn2;
        private Button btn3;
    }

    private OnEditClickListener onEditClickListener;
    private OnLookBookClickListener onLookBookClickListener;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnEditClickListener {
        void onClick(View view, int position);
    }

    public interface OnLookBookClickListener {
        void onClick(View view, int position);
    }

    public interface OnDeleteClickListener {
        void onClick(View view, int position);
    }

    public void setOnEditClickListener(OnEditClickListener onEditClickListener) {
        this.onEditClickListener = onEditClickListener;
    }

    public void setOnLookBookClickListener(OnLookBookClickListener onLookBookClickListener) {
        this.onLookBookClickListener = onLookBookClickListener;
    }

    public void setOnDeleteClickListenr(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }
}
