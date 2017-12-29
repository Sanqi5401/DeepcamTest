package com.deepcam.deblocks.listeners;

import android.graphics.Bitmap;
import android.graphics.Rect;

/*************************************************************************
 *
 * deepCam CONFIDENTIAL
 * FILE: com.aiwinn.deblocks.listeners
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
 * Updated: 2017/8/17
 */


public interface OnRegisterListener {

    boolean onRectFace(Rect rect, float blur, float light);

    /**
     * 检测到合适的人脸
     *
     * @param bitmap
     */
    void onRectFace(Bitmap bitmap);

    /**
     * 人脸比对成功
     *
     * @param score
     */

    boolean onRectFace(float score);

    /**
     * 人脸识别失败
     *
     * @param msg
     */
    void onError(int errCode, String msg);

}
