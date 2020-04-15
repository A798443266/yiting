package com.example.yiting.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.yiting.R;
import com.example.yiting.bean.Car;
import com.example.yiting.utils.Constant;

import java.util.List;

public class MyCarAdapter extends BaseAdapter {
    private Context context;
    private List<Car> cars;
    private LayoutInflater listContainer;
    private String[] status = {"未认证", "已认证"};

    public MyCarAdapter(Context context, List<Car> cars) {
        this.context = context;
        this.cars = cars;
        listContainer = LayoutInflater.from(context);
    }

    public void setData(List<Car> cars) {
        this.cars = cars;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return cars == null ? 0 : cars.size();
    }

    @Override
    public Object getItem(int position) {
        return cars.get(position);
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
            convertView = listContainer.inflate(R.layout.item_my_car, null);
            holder.iv_delete = convertView.findViewById(R.id.iv_delete);
            holder.btn = convertView.findViewById(R.id.btn);
            holder.tv_car = convertView.findViewById(R.id.tv_car);
            holder.tv_status = convertView.findViewById(R.id.tv_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Car car = cars.get(position);
        holder.tv_car.setText(car.getLicensePlate());
        holder.tv_status.setText(status[car.getStatus()]);
        if (car.getStatus() == 0) {
            holder.btn.setVisibility(View.VISIBLE);
        } else {
            holder.btn.setVisibility(View.GONE);
        }

        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onClick(v, position);
                }
            }
        });

        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onVerifyClickListener != null) {
                    onVerifyClickListener.onClick(v, position);
                }
            }
        });
        return convertView;
    }

    static class ViewHolder {
        private ImageView iv_delete;
        private Button btn;
        private TextView tv_car;
        private TextView tv_status;
    }

    public interface OnDeleteClickListener {
        void onClick(View view, int position);
    }
    public interface OnVerifyClickListener {
        void onClick(View view, int position);
    }

    private OnDeleteClickListener onDeleteClickListener;
    private OnVerifyClickListener onVerifyClickListener;

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    public void setOnVerifyClickListener(OnVerifyClickListener onVerifyClickListener) {
        this.onVerifyClickListener = onVerifyClickListener;
    }

}
