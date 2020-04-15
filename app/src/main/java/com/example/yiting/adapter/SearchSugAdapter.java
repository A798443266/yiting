package com.example.yiting.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.yiting.R;
import com.example.yiting.bean.SearchBean;

import java.util.List;

public class SearchSugAdapter extends BaseAdapter {

    private Context context;
    private List<SearchBean> infos;
    private LayoutInflater listContainer;

    public SearchSugAdapter(Context context, List<SearchBean> infos) {
        this.context = context;
        this.infos = infos;
        listContainer = LayoutInflater.from(context);
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
            convertView = listContainer.inflate(R.layout.item_search, null);
            holder.tv_key = convertView.findViewById(R.id.tv_key);
            holder.tv_city_dis = convertView.findViewById(R.id.tv_city_dis);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SearchBean info = infos.get(position);

        holder.tv_key.setText(info.getKey());
        holder.tv_city_dis.setText(info.getCity() + info.getDis());
        return convertView;
    }

    static class ViewHolder {
        private TextView tv_key;
        private TextView tv_city_dis;
    }
}
