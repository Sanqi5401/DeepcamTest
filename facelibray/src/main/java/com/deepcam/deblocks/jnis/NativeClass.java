package com.deepcam.deblocks.jnis;

import android.content.Context;
import android.content.res.AssetManager;

import java.util.ArrayList;

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

public class NativeClass {
    /*static {
        System.loadLibrary("deepcam_face");
    }*/

    public NativeClass() {
        try {
            System.loadLibrary("deepcam_face");
        } catch (UnsatisfiedLinkError e) {
            throw new RuntimeException("load so file badly !!!");
        }
    }

    /**
     * 初始化SO库
     *
     * @param context 当前上下问
     * @param apiKey  api_deep_key
     * @return
     */
    public static native int init(Context context, String apiKey, AssetManager assetManager);

    /**
     * 人脸位置解析
     *
     * @param argb  图片
     * @param infos
     * @return
     */
    public native boolean faceDetect(int[] argb, int width, int height, boolean is_strict, ArrayList<FaceInfo> infos);

    /**
     * 人脸特征值提取
     *
     * @param argb
     * @param info
     * @param features
     * @return
     */
    public native boolean faceFeature(int[] argb, int width, int height, FaceInfo info, ArrayList<Float> features);

    /**
     * 特征值对比
     *
     * @param features1
     * @param features2
     * @return
     */
    public native float sorce(float[] features1, float[] features2);

    /**
     * 特征值对比
     *
     * @return
     */
    public native void disable();


    public native void convertYUV420SPToARGB8888(byte[] nv21, int[] argb, int width, int height);


//    public native boolean blurDetection(int[] argb, int width, int height, FaceInfo info);
}