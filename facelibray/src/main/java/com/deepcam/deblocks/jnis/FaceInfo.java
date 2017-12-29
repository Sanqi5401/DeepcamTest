package com.deepcam.deblocks.jnis;

import java.util.ArrayList;

/**
 * Created by archermind on 17-10-19.
 */

public class FaceInfo {
    public float x0;
    public float y0;
    public float x1;
    public float y1;
    private float light = 0.0f;//亮度
    private float blur = 0.0f;//模糊度
    public float score;
    public ArrayList<Float> landmarks;

    FaceInfo(float _x0, float _y0, float _x1, float _y1, float _score){
        x0 = _x0;
        y0 = _y0;
        x1 = _x1;
        y1 = _y1;
        score = _score;
        landmarks = new ArrayList<Float>();
    }

    public void setLandmark(float value) {
        landmarks.add(value);
    }

    public float getLandmark(int i) {
        return landmarks.get(i);
    }

    public float getLight() {
        return light;
    }

    public void setLight(float light) {
        this.light = light;
    }

    public void setBlur(float blur){this.blur = blur;}
    public float getBlur(){return blur;}
}
