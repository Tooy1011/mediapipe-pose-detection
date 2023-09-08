package com.example.pose.Dao;

import android.database.Cursor;

import com.example.pose.Model.Data;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class DataDao {



    public List<Data> findAll(){
        List<Data> dataList = LitePal.findAll(Data.class);
        return dataList;
    }

    public void deleteAll(){
        LitePal.deleteAll(Data.class);
    }

}
