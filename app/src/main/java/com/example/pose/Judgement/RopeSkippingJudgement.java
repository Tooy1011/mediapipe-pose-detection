package com.example.pose.Judgement;

import com.example.pose.Model.PoseLandMark;

public class RopeSkippingJudgement extends Judgement {
    private PoseLandMark sd_point;
    private int direction = 1;

    public float countTheNum() {
        PoseLandMark left_hip = poseMarkers.get(23);
        PoseLandMark right_hip = poseMarkers.get(24);
        PoseLandMark mid_hip = getMid(left_hip, right_hip);
        if (sd_point == null) {
            sd_point = mid_hip;     //TO-DO 是否更新
        }

        // 判断手肘夹角，微曲

        switch (direction) {
            case 0:     // 下
                if (mid_hip.getY() * height < sd_point.getY() * height + 5) {
                    count += 1;
                    direction = 1;
                }
                break;
            case 1:     // 上
                if (mid_hip.getY() * height > sd_point.getY() * height + 15) {
                    direction = 0;
                }
                break;
        }

        return count;
    }


}
