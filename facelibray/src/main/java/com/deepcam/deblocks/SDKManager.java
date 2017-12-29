package com.deepcam.deblocks;

import android.content.Context;

import com.deepcam.deblocks.jnis.FaceInfo;
import com.deepcam.deblocks.listeners.InitListener;

import java.util.List;

/**
 * Created by zsq on 17-12-27.
 */

public class SDKManager {
    private static SDKManager sdkManager;
    private FaceManager faceManager;
    private boolean isInit = false;

    SDKManager() {
        faceManager = new FaceManager();
    }

    public static SDKManager newInstant() {
        if (sdkManager == null)
            return sdkManager = new SDKManager();
        return sdkManager;
    }

    public void init(Context context, InitListener listener) {
        if (!isInit) {
            if (faceManager.init(context) != 0) {
                listener.errorMsg("Init failed");

            } else {
                isInit = true;
                listener.initSuccess();
            }
        } else {
            listener.initSuccess();
        }
    }

    public boolean isInit() {
        return isInit;
    }

    public List<FaceInfo> detection(int[] data, int width, int height, boolean is) throws Exception {
        if (isInit) {
            return faceManager.detection(data, width, height, is);
        } else
            throw new Exception("Not init");
    }

    public float[] recognition(int[] data, int width, int height, FaceInfo info) throws Exception {
        if (isInit) {
            return faceManager.recognition(data, width, height, info);
        } else
            throw new Exception("Not init");
    }

    public double score(float[] feature1, float[] feature2) throws Exception {
        if (isInit) {
            return faceManager.sorce(feature1, feature2);
        } else
            throw new Exception("Not init");
    }

    public void disable() {
        if (isInit) {
            faceManager.disable();
            isInit = false;
            faceManager = null;
        }
    }

    public void convertYUV420SPToARGB8888(byte[] nv21, int[] argb, int width, int height) throws Exception{
        if (isInit) {
            faceManager.convertYUV420SPToARGB8888(nv21, argb, width, height);
        } else {
            throw new Exception("Not init");
        }
    }
}
