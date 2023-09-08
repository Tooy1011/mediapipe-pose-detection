package com.example.pose.Judgement;

public class JudgementFactory {
    public static Judgement getJudgementFactory(String sports){
        if(sports.equals("跳绳")){
            return new RopeSkippingJudgement();
        }else if (sports.equals("开合跳")){
            return new JumpOnAndOffJudgement();
        }else if(sports.equals("仰卧起坐")){
            return new SitUpJudgement();
        }else{
            return null;
        }
    }
}
