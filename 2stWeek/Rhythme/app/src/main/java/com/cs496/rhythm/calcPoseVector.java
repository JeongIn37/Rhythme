package com.cs496.rhythm;

import static java.lang.Math.abs;
import static java.lang.Math.atan2;

import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

public interface calcPoseVector {

    static double getAngle(PoseLandmark first,PoseLandmark midle, PoseLandmark last)
    {

        double result = Math.toDegrees(atan2(last.getPosition().y - midle.getPosition().y, last.getPosition().x - midle.getPosition().x) - atan2(first.getPosition().y - midle.getPosition().y, first.getPosition().x - midle.getPosition().x));

        result = Math.abs(result);
        if(result > 180){
            result = 360 - result;
        }

        return result;
    }

    static double[] getPoseVector(Pose pose) {
        double[] temp = new double[]{0,0,0,0,0,0,0,0};
        temp[0]=getAngle(pose.getPoseLandmark(16),pose.getPoseLandmark(14),pose.getPoseLandmark(12));
        temp[1]=getAngle(pose.getPoseLandmark(14),pose.getPoseLandmark(12),pose.getPoseLandmark(24));
        temp[2]=getAngle(pose.getPoseLandmark(23),pose.getPoseLandmark(11),pose.getPoseLandmark(13));
        temp[3]=getAngle(pose.getPoseLandmark(11),pose.getPoseLandmark(13),pose.getPoseLandmark(15));

        temp[4]=getAngle(pose.getPoseLandmark(28),pose.getPoseLandmark(26),pose.getPoseLandmark(24));
        temp[5]=getAngle(pose.getPoseLandmark(26),pose.getPoseLandmark(24),pose.getPoseLandmark(12));
        temp[6]=getAngle(pose.getPoseLandmark(25),pose.getPoseLandmark(23),pose.getPoseLandmark(11));
        temp[7]=getAngle(pose.getPoseLandmark(27),pose.getPoseLandmark(25),pose.getPoseLandmark(23));
        return temp;
    }

    static int grade(double[] a, double[] b)
    {
        int sum=0;
        for(int i=0;i<8;i++)
        {
            if(abs(a[i]-b[i])<=20)
            {
                sum+=100;
            }
            else if(abs(a[i]-b[i])<=40)
            {
                sum+=80;
            }
            else if(abs(a[i]-b[i])<=60)
            {
                sum+=50;
            }
            else
            {
                sum+=0;
            }
        }
        return sum/8;
    }
}
