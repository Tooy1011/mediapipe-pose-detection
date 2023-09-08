package com.example.pose.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.pose.Adapter.DataAdapter;
import com.example.pose.Dao.DataDao;
import com.example.pose.Model.Data;
import com.example.pose.R;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class MineFragment extends Fragment {
    private List<Data> dataList = new ArrayList<>();
    private DataDao dataDao = new DataDao();
    private DataAdapter dataAdapter;

    public MineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataList = dataDao.findAll();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        ImageButton bt_delete = view.findViewById(R.id.bt_delete);
        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("注意！")
                        .setMessage("确定要删除所有数据吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dataAdapter.deleteAll();

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        });

        RecyclerView dataListView = view.findViewById(R.id.dataList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        dataListView.setLayoutManager(layoutManager);
        dataAdapter = new DataAdapter(dataList);
        dataListView.setAdapter(dataAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        dataList = dataDao.findAll();
        dataAdapter.notifyDataSetChanged();
    }
}