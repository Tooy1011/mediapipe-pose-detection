package com.example.pose.Model;

import org.litepal.crud.LitePalSupport;

public class Data extends LitePalSupport {
    private String sportsName;
    private int num;
    private String time;

    public Data(String sportsName, int num, String time){
        this.sportsName = sportsName;
        this.num = num;
        this.time = time;
    }

    public String getSportsName() {
        return sportsName;
    }

    public void setSportsName(String sportsName) {
        this.sportsName = sportsName;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
