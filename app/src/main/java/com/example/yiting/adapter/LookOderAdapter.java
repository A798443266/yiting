package com.example.yiting.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.yiting.R;
import com.example.yiting.bean.ShareOrder;
import com.example.yiting.bean.ShareOrderInfo;
import com.example.yiting.bean.User;
import com.example.yiting.bean.UserSharePark;
import com.example.yiting.utils.Constant;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LookOderAdapter extends BaseAdapter {

    private Context context;
    private List<ShareOrderInfo> shareOrderInfos;
    private LayoutInflater listContainer;

    private String[] status = {"未开始", "进行中", "停车结束", "已退订"};

    public LookOderAdapter(Context context, List<ShareOrderInfo> shareOrderInfos) {
        this.context = context;
        this.shareOrderInfos = shareOrderInfos;
        listContainer = LayoutInflater.from(context);
    }

    public void setData(List<ShareOrderInfo> shareOrderInfos) {
        this.shareOrderInfos = shareOrderInfos;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.shareOrderInfos == null ? 0 : this.shareOrderInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return shareOrderInfos.get(position);
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
            convertView = listContainer.inflate(R.layout.item_look_order, null);
            holder.cv = convertView.findViewById(R.id.cv);
            holder.tv_name = convertView.findViewById(R.id.tv_name);
            holder.tv_day = convertView.findViewById(R.id.tv_day);
            holder.tv_startTime = convertView.findViewById(R.id.tv_startTime);
            holder.tv_createTime = convertView.findViewById(R.id.tv_createTime);
            holder.tv_endTime = convertView.findViewById(R.id.tv_endTime);
            holder.tv_status = convertView.findViewById(R.id.tv_status);
            holder.tv_price = convertView.findViewById(R.id.tv_price);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ShareOrderInfo info = shareOrderInfos.get(position);
        User user = info.getUser();
        UserSharePark userSharePark = info.getUserSharePark();
        ShareOrder shareOrder = info.getShareOrder();
        Glide.with(context).load(Constant.BASE + user.getAvatar())
                .error(R.drawable.touxiang)
                .into(holder.cv);
        holder.tv_name.setText(user.getName());
        holder.tv_createTime.setText(shareOrder.getCreatetime());
        holder.tv_price.setText(shareOrder.getPrice() + "");
        holder.tv_day.setText(userSharePark.getDay());
        holder.tv_startTime.setText(shareOrder.getStarttime());
        holder.tv_endTime.setText(shareOrder.getEndtime());
        holder.tv_status.setText(status[shareOrder.getStatus()]);

        return convertView;
    }

    static class ViewHolder {
        private CircleImageView cv;
        private TextView tv_name;
        private TextView tv_day;
        private TextView tv_startTime;
        private TextView tv_createTime;
        private TextView tv_endTime;
        private TextView tv_price;
        private TextView tv_status;
    }

}
