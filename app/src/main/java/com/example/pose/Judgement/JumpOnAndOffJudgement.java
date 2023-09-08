package com.example.pose.Judgement;


import com.example.pose.Model.PoseLandMark;

public class JumpOnAndOffJudgement extends Judgement{
    private int direction = 1;
    @Override
    public float countTheNum() {
        PoseLandMark left_hip = poseMarkers.get(23);
        PoseLandMark right_hip = poseMarkers.get(24);
        PoseLandMark mid_hip = getMid(left_hip, right_hip);
        PoseLandMark left_ankle = poseMarkers.get(27);
        PoseLandMark right_ankle = poseMarkers.get(28);
        PoseLandMark left_elbow = poseMarkers.get(13);
        PoseLandMark left_shoulder = poseMarkers.get(11);
        PoseLandMark right_elbow = poseMarkers.get(14);
        PoseLandMark right_shoulder = poseMarkers.get(12);
        double left_arm_angle = getAngle(left_elbow, left_shoulder, left_hip);
        double right_arm_angle = getAngle(right_elbow, right_shoulder, right_hip);
        double leg_angle = getAngle(left_ankle, mid_hip, right_ankle);

        switch (direction){
            case 0:     // 合
                if(left_arm_angle<45 && right_arm_angle<45 && leg_angle<25){
                    count +=0.5;
                    direction = 1;
                }
                break;
            case 1:     // 开
                if(left_arm_angle>88 && right_arm_angle>88 && leg_angle>55){
                    count += 0.5;
                    direction = 0;
                }
                break;
        }
        return count;
    }
}
