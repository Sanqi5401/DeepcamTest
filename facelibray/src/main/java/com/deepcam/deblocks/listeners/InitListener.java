package com.deepcam.deblocks.listeners;

/*************************************************************************
 *
 * deepCam CONFIDENTIAL
 * FILE: com.deepcam.deblock.listeners
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
 * Updated: 2017/8/1
 */


public interface InitListener {

    /**
     * 初始化成功
     */
    void initSuccess();

    /**
     * 初始化失败
     */
    void errorMsg(String msg);
}
