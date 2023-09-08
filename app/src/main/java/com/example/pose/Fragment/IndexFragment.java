package com.example.pose.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.pose.Activity.PoseRecognitionActivity;
import com.example.pose.R;

import java.math.RoundingMode;

public class IndexFragment extends Fragment {

    public IndexFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_index, container, false);
        initClick(view);
        return view;
    }

    private void initClick(View view) {
        LinearLayout tab_rope = view.findViewById(R.id.layout_rope);
        LinearLayout tab_jump = view.findViewById(R.id.layout_jump);
        LinearLayout tab_sit_up = view.findViewById(R.id.layout_sit_up);

        tab_rope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageJump("跳绳");
            }
        });

        tab_jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageJump("开合跳");
            }
        });

        tab_sit_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageJump("仰卧起坐");
            }
        });

    }

    private void pageJump(String sports){
        Bundle bundle = new Bundle();
        bundle.putString("sports", sports);
        Intent intent = new Intent(getContext(), PoseRecognitionActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}