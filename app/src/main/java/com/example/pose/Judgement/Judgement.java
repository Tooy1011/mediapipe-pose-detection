package com.example.pose.Judgement;

import com.example.pose.Model.PoseLandMark;

import java.util.ArrayList;

public abstract class Judgement {
    protected ArrayList<PoseLandMark> poseMarkers;
    protected float count = 0;
    protected int height = 1280;
    protected int width = 720;

    public void init(ArrayList<PoseLandMark> poseMarkers, Boolean isRotated) {
        this.poseMarkers = poseMarkers;
        if (isRotated) {
            int t;
            t = this.height;
            this.height = width;
            width = t;
        }
    }

    abstract public float countTheNum();

    protected double getAngle(PoseLandMark firstPoint, PoseLandMark midPoint, PoseLandMark lastPoint) {
        double result =
                Math.toDegrees(
                        Math.atan2(lastPoint.getY() - midPoint.getY(), lastPoint.getX() - midPoint.getX())
                                - Math.atan2(firstPoint.getY() - midPoint.getY(), firstPoint.getX() - midPoint.getX()));
        result = Math.abs(result); // Angle should never be negative
        if (result > 180) {
            result = (360.0 - result); // Always get the acute representation of the angle
        }
        return result;
    }

    protected PoseLandMark getMid(PoseLandMark left_hip, PoseLandMark right_hip) {
        float x = (left_hip.getX() + right_hip.getX()) / 2;
        float y = (left_hip.getY() + right_hip.getY()) / 2;
        float visible = (left_hip.getVisible() + right_hip.getVisible()) / 2;
        PoseLandMark mid_hip = new PoseLandMark(x, y, visible);
        return mid_hip;
    }
}
