package com.example.yiting.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.yiting.R;
import com.example.yiting.bean.Information;
import com.example.yiting.utils.Constant;

import java.util.List;

public class InformationAdapter extends BaseAdapter {

    private Context context;
    private List<Information> infos;
    private LayoutInflater listContainer;

    public InformationAdapter(Context context, List<Information> infos) {
        this.context = context;
        this.infos = infos;
        listContainer = LayoutInflater.from(context);
    }

    public void setData(List<Information> infos) {
        this.infos = infos;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.infos == null ? 0 : this.infos.size();
    }

    @Override
    public Object getItem(int position) {
        return infos.get(position);
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
            convertView = listContainer.inflate(R.layout.item_infomation, null);
            holder.iv = convertView.findViewById(R.id.iv);
            holder.tv_title = convertView.findViewById(R.id.tv_title);
            holder.tv_subTitle = convertView.findViewById(R.id.tv_subTitle);
            holder.tv_time = convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Information info = infos.get(position);
        Glide.with(context).load(Constant.BASE + info.getPic()).error(R.drawable.no_pic).into(holder.iv);
        holder.tv_time.setText(info.getCreatetime());
        holder.tv_title.setText(info.getTitle());
        holder.tv_subTitle.setText(info.getSubtitle());
        return convertView;
    }

    static class ViewHolder {
        private ImageView iv;
        private TextView tv_title;
        private TextView tv_subTitle;
        private TextView tv_time;
    }
}
