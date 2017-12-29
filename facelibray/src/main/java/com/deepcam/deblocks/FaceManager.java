package com.deepcam.deblocks;


import android.content.Context;

import com.deepcam.deblocks.jnis.FaceInfo;
import com.deepcam.deblocks.jnis.NativeClass;
import com.deepcam.deblocks.utils.PackageUtils;

import java.util.ArrayList;

public class FaceManager {
    private NativeClass mNativeClass;

    public FaceManager() {
        mNativeClass = new NativeClass();
    }

    public int init(Context context) {
        if (mNativeClass != null) {
            String key = PackageUtils.obtainMetaData(context);
//            if (!TextUtils.isEmpty(key)){
//                System.out.println("context========="+context.getPackageName());
            return mNativeClass.init(context, key, context.getAssets());
        } else {
            return -1;
        }
    }


  public   ArrayList<FaceInfo> detection(int[] argb, int width, int height, boolean is_strict) {
        if (argb != null && argb.length > 0) {
            ArrayList<FaceInfo> faceInfos = new ArrayList<>();
            mNativeClass.faceDetect(argb, width, height, is_strict, faceInfos);
            return faceInfos;
        } else {
            return null;
        }
    }

    public float[] recognition(int[] argb, int width, int height, FaceInfo face) {
        if (argb != null && argb.length > 0) {
            ArrayList<Float> features = new ArrayList<>();
            mNativeClass.faceFeature(argb, width, height, face, features);
            int len = features.size();
            if (len <= 0) {
                return null;
            } else {
                float[] temps = new float[len];
                for (int i = 0; i < len; i++) {
                    temps[i] = features.get(i);
                }
                return temps;
            }
        }
        return null;
    }

    public float sorce(float[] features1, float[] features2) {
        if (mNativeClass != null) {
            return mNativeClass.sorce(features1, features2);
        } else {
            return -1;
        }
    }

    /**
     * 特征值对比
     *
     * @return
     */
    public void disable() {
        if (mNativeClass != null) {
            mNativeClass.disable();
        }
    }


    public void convertYUV420SPToARGB8888(byte[] nv21, int[] argb, int width, int height) {
        if (mNativeClass != null) {
            mNativeClass.convertYUV420SPToARGB8888(nv21, argb, width, height);
        } else {
            argb = null;
        }
    }


}