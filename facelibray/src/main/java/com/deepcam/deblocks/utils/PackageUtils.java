package com.deepcam.deblocks.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

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

public class PackageUtils {
    public static String obtainMetaData(Context context){

        ApplicationInfo ai = null;
        try {
            ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("obtain api_deepcam_key badly!");
        }
        Bundle obj = ai.metaData;
        if (obj !=null){
            return obj.getString("api_deepcam_key",null);
        }else{
            throw new RuntimeException("AndroidMainfest api_deepcam_key not null");
        }

    }
}
