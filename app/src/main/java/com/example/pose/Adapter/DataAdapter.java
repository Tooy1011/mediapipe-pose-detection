package com.example.pose.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pose.Dao.DataDao;
import com.example.pose.Model.Data;
import com.example.pose.R;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder>{
    private List<Data> dataList;

    public DataAdapter(List<Data> dataList){
        this.dataList = dataList;
    }

    public void deleteAll(){
        DataDao dataDao = new DataDao();
        dataDao.deleteAll();
        Data data = new Data("test", 0, "test");
        data.save();
        dataList = dataDao.findAll();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data_list,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Data data;
        data = dataList.get(position);
        holder.text_sports_name.setText(data.getSportsName());
        holder.text_num.setText(String.valueOf(data.getNum()));
        holder.text_time.setText(data.getTime());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView text_sports_name;
        TextView text_num;
        TextView text_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.text_sports_name = itemView.findViewById(R.id.text_sports_name);
            this.text_num = itemView.findViewById(R.id.text_num);
            this.text_time = itemView.findViewById(R.id.text_time);
        }
    }
}


