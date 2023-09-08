package com.example.pose.Judgement;

import com.example.pose.Model.PoseLandMark;

public class SitUpJudgement extends Judgement {

    private int direction = 1;

    public float countTheNum() {
        // 判断膝盖弯曲度
        int right = 0;
        PoseLandMark knee = poseMarkers.get(25);
        PoseLandMark right_knee = poseMarkers.get(26);

        if (knee.getY() < right_knee.getY()) {
            knee = right_knee;
            right = 1;
        }

        PoseLandMark shoulder = poseMarkers.get(11 + right);
        PoseLandMark elbow = poseMarkers.get(13 + right);
        PoseLandMark wrist = poseMarkers.get(15 + right);
        PoseLandMark hip = poseMarkers.get(23 + right);
        PoseLandMark ankle = poseMarkers.get(27 + right);

        double knee_angle = getAngle(hip, knee, ankle);
        float difference = Math.abs(ankle.getX() - hip.getX()) * width;
        double elbow_angle = getAngle(shoulder, elbow, wrist);
        double hip_angle = getAngle(shoulder, hip, knee);
        System.out.println(knee_angle);
        System.out.println(elbow_angle);
        System.out.println(hip_angle);
        if (knee_angle < 120) {
            switch (direction) {
                case 0:     // 回位
                    if (hip_angle > 135) {
                        direction = 1;
                    }
                    break;
                case 1:     // 起坐
                    if (hip_angle < 105 && elbow_angle < 90) {
                        count += 1;
                        direction = 0;
                    }
                    break;
            }
        }

        return count;
    }

}
