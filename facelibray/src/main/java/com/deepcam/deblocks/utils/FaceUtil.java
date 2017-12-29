package com.deepcam.deblocks.utils;

import android.util.Log;


import com.deepcam.deblocks.jnis.FaceInfo;

import java.util.List;

/*************************************************************************
 *
 * deepCam CONFIDENTIAL
 * FILE: com.deepcam.plugin.listeners
 *
 *  [2016] - [2017] DeepCam, LLC and DeepCam
 *  All Rights Reserved.

 NOTICE:  
 * All information contained herein is, and remains the property of DeepCam LLC.
 * The intellectual and technical concepts contained herein are proprietary to DeepCam
 * and may be covered by U.S. and Foreign Patents,patents in process, and are protected by   
 * trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * DeepCam, LLC.
 *
 *
 * Written: ji.zheng(ji.zheng@deepcam.com)
 * Updated: 2017/5/9
 */

public class FaceUtil {

    final private static String TAG = "FaceLockLogUtils";
    /**
     * 获取最大人脸
     * @param faces
     */
    public static FaceInfo getMaxFace(List<FaceInfo> faces){
        if (faces ==null || faces.size() == 0){
            return null;
        }
        int size = faces.size();
        Log.d(TAG, String.format("getMaxFace: face size %d ",size));
        FaceInfo temp  = faces.get(0);
        if (size == 1){
            Log.d(TAG, String.format("getMaxFace: landmark == first: (%s , %s); second : (%s , %s) ; three : (%s , %s) ; four : (%s , %s) ;fire : (%s , %s) ; ",temp.getLandmark(0),temp.getLandmark(1),temp.getLandmark(2),temp.getLandmark(3),temp.getLandmark(4),temp.getLandmark(5),temp.getLandmark(6),temp.getLandmark(7),temp.getLandmark(8),temp.getLandmark(9)));
            return temp;
        }else {
            for (int i = 1; i < size; i++) {
                FaceInfo curr = faces.get(i);
                float t_x = temp.x1 - temp.x0;
                float t_y = temp.y1 - temp.y0;
                Log.d(TAG, String.format("getMaxFace: temp size x ::: %f,y ::: %f",t_x,t_y));

                float c_x = curr.x1 - curr.x0;
                float c_y = curr.y1 - curr.y0;
                Log.d(TAG, String.format("getMaxFace: curr size x ::: %f,y ::: %f",c_x,c_y));

                Log.d(TAG, String.format("getMaxFace: landmark == first : (%s , %s); second : (%s , %s) ; three : (%s , %s) ; four : (%s , %s) ;fire : (%s , %s) ; ",temp.getLandmark(0),temp.getLandmark(1),temp.getLandmark(2),temp.getLandmark(3),temp.getLandmark(4),temp.getLandmark(5),temp.getLandmark(6),temp.getLandmark(7),temp.getLandmark(8),temp.getLandmark(9)));

                if ((t_x * t_y) < (c_x * c_y)){
                    temp = curr;
                }
            }
            return temp;
        }
    }
}
