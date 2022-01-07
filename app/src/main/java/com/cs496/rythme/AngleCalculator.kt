package com.cs496.rythme

import android.graphics.PointF
import android.util.Log
import androidx.core.graphics.minus
import com.google.mlkit.vision.pose.Pose
import kotlin.math.atan

interface AngleCalculator {
    fun getAngle(a: PointF, b : PointF , c : PointF) : Float
    {
        val A1 = a - b
        val A2 = c - b
        return ((atan(A2.y / A2.x) - atan(A1.y / A1.x)) * (180/3.141592)).toFloat()
    }
    // 임시 분석함
    fun getVector(pose : Pose): MutableList<Float> {
        val list = mutableListOf<Float>()
        val list2 = mutableListOf<PointF>()
        //상



        Log.d("왼쪽 어깨 위치",pose.allPoseLandmarks.size.toString())



        list.add(getAngle(pose.getPoseLandmark(16).position,pose.getPoseLandmark(14).position,pose.getPoseLandmark(12).position))
        list.add(getAngle(pose.getPoseLandmark(14).position,pose.getPoseLandmark(12).position,pose.getPoseLandmark(24).position))
        list.add(getAngle(pose.getPoseLandmark(23).position,pose.getPoseLandmark(11).position,pose.getPoseLandmark(13).position))
        list.add(getAngle(pose.getPoseLandmark(11).position,pose.getPoseLandmark(13).position,pose.getPoseLandmark(15).position))

        //하체
        list.add(getAngle(pose.getPoseLandmark(28).position,pose.getPoseLandmark(26).position,pose.getPoseLandmark(24).position))
        list.add(getAngle(pose.getPoseLandmark(26).position,pose.getPoseLandmark(24).position,pose.getPoseLandmark(12).position))
        list.add(getAngle(pose.getPoseLandmark(25).position,pose.getPoseLandmark(23).position,pose.getPoseLandmark(11).position))
        list.add(getAngle(pose.getPoseLandmark(27).position,pose.getPoseLandmark(25).position,pose.getPoseLandmark(23).position))
        return list
    }
}
//java.lang.NullPointerException: Attempt to invoke virtual method 'android.graphics.PointF com.google.mlkit.vision.pose.PoseLandmark.getPosition()' on a null object reference