package com.example.pose.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pose.Fragment.IndexFragment;
import com.example.pose.Fragment.MineFragment;
import com.example.pose.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Fragment> fragmentList;
    private LinearLayout tab_home;
    private LinearLayout tab_mine;
    private ImageButton tab_home_img;
    private ImageButton tab_mine_img;
    private TextView tab_home_text;
    private TextView tab_mine_text;

    private View.OnClickListener onClickListener = v -> {
        resetTabs();
        switch (v.getId()){
            case R.id.tab_home:
                selectTab(0);
                break;
            case R.id.tab_mine:
                selectTab(1);
                break;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFragment();
        initViews();
        initEvents();
        initFirstRun(0);
    }

    private void initFirstRun(int i) {
        resetTabs();
        selectTab(0);
        setCurrentFragment(0);
    }

    private void initEvents() {
        tab_home.setOnClickListener(onClickListener);
        tab_mine.setOnClickListener(onClickListener);
    }

    private void initViews() {
        tab_home = findViewById(R.id.tab_home);
        tab_mine = findViewById(R.id.tab_mine);
        tab_home_img = findViewById(R.id.tab_home_img);
        tab_mine_img = findViewById(R.id.tab_mine_img);
        tab_home_text = findViewById(R.id.tab_home_text);
        tab_mine_text = findViewById(R.id.tab_mine_text);
    }

    private void initFragment() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new IndexFragment());
        fragmentList.add(new MineFragment());
    }

    private void resetTabs() {
        tab_home_img.setImageResource(R.mipmap.img_home_unpressed);
        tab_mine_img.setImageResource(R.mipmap.img_mine_unpressed);
        tab_home_text.setTextColor(getResources().getColor(R.color.gray));
        tab_mine_text.setTextColor(getResources().getColor(R.color.gray));
    }

    private void selectTab(int i) {
        switch (i){
            case 0:
                tab_home_img.setImageResource(R.mipmap.img_home_pressed);
                tab_home_text.setTextColor(getResources().getColor(R.color.dark_orange));
                break;
            case 1:
                tab_mine_img.setImageResource(R.mipmap.img_mine_pressed);
                tab_mine_text.setTextColor(getResources().getColor(R.color.dark_orange));
                break;
        }
        setCurrentFragment(i);
    }

    private void setCurrentFragment(int i) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction trans = fm.beginTransaction();
        trans.replace(R.id.frag_layout, fragmentList.get(i));
        trans.commit();
    }
}