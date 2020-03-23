package com.example.yiting.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.yiting.R;

import java.util.List;

public class ParkPhotoAdapter extends BaseAdapter {

    private LayoutInflater listContainer;
    private Context context;
    private List<Bitmap> bmps;

    public ParkPhotoAdapter(Context context, List<Bitmap> bmp) {
        listContainer = LayoutInflater.from(context);
        bmps = bmp;
        this.context = context;
    }

    public int getCount() {
        return bmps == null ? 0 : bmps.size() + 1;
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = listContainer.inflate(R.layout.item_release_photo, null);
            holder.iv = convertView.findViewById(R.id.iv);
            holder.iv_cha = convertView.findViewById(R.id.iv_cha);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position == bmps.size()) {
            holder.iv.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.add_photo));
            holder.iv_cha.setVisibility(View.GONE);
            if (position == 5) {
                holder.iv.setVisibility(View.GONE);
            }
        } else {
            holder.iv_cha.setVisibility(View.VISIBLE);
            holder.iv.setImageBitmap(bmps.get(position));
        }

        holder.iv_cha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onCloseClickListener != null) {
                    onCloseClickListener.closeClick(v, position);
                }
            }
        });

        return convertView;
    }

    static class ViewHolder {
        private ImageView iv;
        private ImageView iv_cha;
    }

    private OnCloseClickListener onCloseClickListener;

    //实现接口可以点击item内部的控件
    public interface OnCloseClickListener {
        void closeClick(View view, int position);
    }

    public void setOnCloseClickListener(OnCloseClickListener onCloseClickListener) {
        this.onCloseClickListener = onCloseClickListener;
    }
}
