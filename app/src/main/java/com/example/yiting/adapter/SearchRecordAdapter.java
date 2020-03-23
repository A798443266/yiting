package com.example.yiting.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.yiting.R;
import com.example.yiting.bean.RecordInfo;

import java.util.List;

public class SearchRecordAdapter extends BaseAdapter {

    private Context context;
    private List<RecordInfo> datas;

    public SearchRecordAdapter(Context context, List<RecordInfo> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = View.inflate(context,R.layout.item_search_record,null);
            holder.tv_content = convertView.findViewById(R.id.tv_content);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        RecordInfo record = datas.get(position);
        holder.tv_content.setText(record.getContent());
        return convertView;
    }

    static class ViewHolder{
        TextView tv_content;
    }
}
