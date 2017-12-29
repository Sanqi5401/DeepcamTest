#include <jni.h>
#include <android/log.h>
#include <android/bitmap.h>
#include <assert.h>
#include <unistd.h>
#include <iostream>
#include <fstream>
#include <vector>
#include <dlfcn.h>
#include <math.h>
#include <malloc.h>
#include "SafeUtil.h"
#include <pthread.h>
#include "yuv2rgb.h"
#include <opencv2/opencv.hpp>

#define LOG_TAG "native"
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOG_ASSERT(_cond, ...) if (!_cond) __android_log_assert("conditional", LOG_TAG, __VA_ARGS__)
#define JNIREG_CLASS "com/deepcam/deblocks/jnis/NativeClass"
#define FACEINFO_CLASS "com/deepcam/deblocks/jnis/FaceInfo"

using namespace cv;
#define SET_LIGHT_METHOD "setLight"
#define SET_BLUR_METHOD "setBlur"

struct face_landmark {
    float x[5];
    float y[5];
};

struct face_box {
    float x0;
    float y0;
    float x1;
    float y1;

    /* confidence score */
    float score;

    /*regression scale */

    float regress[4];

    /* padding stuff*/
    float px0;
    float py0;
    float px1;
    float py1;

    face_landmark landmark;
};


jclass faceInfoClass;
jmethodID faceInfoConstruct;
jmethodID faceInfoAddLandmark;
jmethodID faceInfoGetLandmark;

jfieldID faceInfoX0;
jfieldID faceInfoY0;
jfieldID faceInfoX1;
jfieldID faceInfoY1;

jclass arrayListClass;
jmethodID arrayListConstruct;
jmethodID arrarListAdd;

jclass floatClass;
jmethodID floatConstruct;

jboolean copy = false;

jboolean has_disable = true;

jmethodID setBlurMethod;
jmethodID setLightMethod;

//typedef bool (*CORE_INIT)(const char* model_dir);
typedef bool (*CORE_INIT)(std::vector<char> &model_mt, std::vector<char> &model_dr);

typedef bool (*CORE_LOAD_MODEL)(const std::vector<char> models);

typedef bool (*CORE_FACE_DETECT)(int rows, int cols, void *pixelAddr,
                                 std::vector<face_box> &face_info);

/**
 * 模糊度和亮度的识别
 */
typedef int (*FACE_LIGHT_BLUR)(int rows, int cols, void *pixelAddr, std::vector<float> landmarks,
                               float &light, float &blur);
//typedef bool (*CORE_FACE_FEATURE)(
//        int rows, int cols, void* pixelAddr,
//        float& x0, float& y0, float& x1, float& y1,
//        std::vector<float>& feature);

// 2017-11-06加入landmark模块
typedef int (*CORE_FACE_FEATURE)(
        int rows, int cols, void *pixelAddr,
        std::vector<float> landmarks,
        std::vector<float> &feature);


void *libcore_handle = NULL;
CORE_INIT libcore_init = NULL;
CORE_FACE_DETECT libcore_face_detect = NULL;
CORE_FACE_FEATURE libcore_face_feature = NULL;
FACE_LIGHT_BLUR libFaceLightBlur = NULL;
CORE_LOAD_MODEL load_models = NULL;


static jboolean getFaceInfoValue(JNIEnv *env, jobject faceInfo, std::vector<float> &landmarks) {
    faceInfoClass = env->FindClass(FACEINFO_CLASS);
    if (faceInfoClass == 0) {
        LOGE("find class %s failed", FACEINFO_CLASS);
        return false;
    }
    faceInfoGetLandmark = env->GetMethodID(faceInfoClass, "getLandmark", "(I)F");
    if (faceInfoGetLandmark == 0) {
        return false;
    }
    for (int i = 0; i < 5; i++) {
        landmarks.push_back(env->CallFloatMethod(faceInfo, faceInfoGetLandmark, 2 * i));
        landmarks.push_back(env->CallFloatMethod(faceInfo, faceInfoGetLandmark, 2 * i + 1));
    }
    return true;
}

static jboolean setOutputFaceInfo(JNIEnv *env, jobject output, std::vector<face_box> face_info,
                                  float blur, float light) {
    //TODO:2017-11-04修改了相关的landmarkd的相关模块
    faceInfoClass = env->FindClass(FACEINFO_CLASS);
    if (faceInfoClass == 0) {
        return false;
    }

    faceInfoConstruct = env->GetMethodID(faceInfoClass, "<init>", "(FFFFF)V");
    if (faceInfoConstruct == 0) {
        return false;
    }

    setBlurMethod = env->GetMethodID(faceInfoClass, SET_BLUR_METHOD, "(F)V");
    if (setBlurMethod == 0) {
        return false;
    }

    setLightMethod = env->GetMethodID(faceInfoClass, SET_LIGHT_METHOD, "(F)V");
    if (setLightMethod == 0) {
        return false;
    }

    faceInfoAddLandmark = env->GetMethodID(faceInfoClass, "setLandmark", "(F)V");
    if (faceInfoAddLandmark == 0) {
        return false;
    }

    arrayListClass = env->FindClass("java/util/ArrayList");
    if (arrayListClass == 0) {
        return false;
    }

    arrarListAdd = env->GetMethodID(arrayListClass, "add", "(Ljava/lang/Object;)Z");
    if (arrarListAdd == 0) {
        return false;
    }
    for (int i = 0; i < face_info.size(); i++) {
        face_box &box = face_info[i];
        jobject tmp = env->NewObject(faceInfoClass, faceInfoConstruct,
                                     box.x0, box.y0, box.x1, box.y1, box.score);

        env->CallVoidMethod(tmp, setBlurMethod, blur);//设置模糊度
        env->CallVoidMethod(tmp, setLightMethod, light);//设置亮度
        for (int j = 0; j < 5; j++) {
//            LOGD("add landmarks %d:  %f", 2 * i, box.landmark.x[j]);
//            LOGD("add landmarks %d:  %f", 2 * i + 1, box.landmark.y[j]);
            env->CallVoidMethod(tmp, faceInfoAddLandmark, box.landmark.x[j]);
            env->CallVoidMethod(tmp, faceInfoAddLandmark, box.landmark.y[j]);
        }
        env->CallBooleanMethod(output, arrarListAdd, tmp);
    }
    return true;
}

static jboolean setOutputFaceFeature(JNIEnv *env, jobject output, std::vector<float> feature) {
    arrayListClass = env->FindClass("java/util/ArrayList");
    if (arrayListClass == 0) {
        return false;
    }

    floatClass = env->FindClass("java/lang/Float");
    if (floatClass == 0) {
        return false;
    }

    floatConstruct = env->GetMethodID(floatClass, "<init>", "(F)V");
    if (floatConstruct == 0) {
        return false;
    }

    arrarListAdd = env->GetMethodID(arrayListClass, "add", "(Ljava/lang/Object;)Z");
    if (arrarListAdd == 0) {
        return false;
    }
    for (int i = 0; i < feature.size(); i++) {
        jobject tmp = env->NewObject(floatClass, floatConstruct, feature[i]);
        env->CallBooleanMethod(output, arrarListAdd, tmp);
    }
    return true;
}

/**
 *
 * @param env
 * @param clazz
 * @param context
 * @param apiKey
 * @param asset_manager
 * @return 0：表示初始化成功
 *         1：该包不再白名单里面
 *         2：打开人脸so库失败
 *         3：获取人脸相关方法失败
 *         4：初始化人脸模型失败
 *         5：初始化失败
 */
JNIEXPORT jint JNICALL
native_init(JNIEnv *env, jclass clazz, jobject context, jstring apiKey, jobject asset_manager) {
    LOGI("init native version %s", "11.10.19.50_v_1.1");
    int ret = 0;

    //TODO:验证白名单
    jboolean res = whiteLists(env, context, apiKey);
    if (!res) {
        LOGV("packename no error");
        return 1;
    }

    /*******************************************************************************************************************/
    /***********************************************        初始化sdk人脸模块           ********************************/
    /*******************************************************************************************************************/
    dlerror();
    const char *error;
    libcore_handle = dlopen("libface_detect_core.so", RTLD_NOW);
    char *err = dlerror();
//    LOGI("errr ------------------ %s",err);
    if (libcore_handle == NULL) {
        LOGE("dlopen libface_detect failed");
        return 2;
    }
    dlerror();
//    *(void **)(&libcore_init) = dlsym(libcore_handle, "mtcnn_ar_gan_init");
    *(void **) (&libcore_init) = dlsym(libcore_handle, "deepcam_sdk_init");
    if ((error = dlerror()) != NULL) {
        LOGE("find init method3 failed: %s", error);
    }
    dlerror();
    *(void **) (&libFaceLightBlur) = dlsym(libcore_handle, "deepcam_face_light_blur");
    if ((error = dlerror()) != NULL) {
        LOGE("init face light and blur error");
    }
    dlerror();
//    *(void **)(&libcore_face_detect) = dlsym(libcore_handle, "mtcnn_face_detect");
    *(void **) (&libcore_face_detect) = dlsym(libcore_handle, "deepcam_face_detect");
    if ((error = dlerror()) != NULL) {
        LOGE("find face_detect method failed: %s", error);
    }
    dlerror();
//    *(void **)(&libcore_face_feature) = dlsym(libcore_handle, "ar_gan_face_feature");
    *(void **) (&libcore_face_feature) = dlsym(libcore_handle, "deepcam_face_feature");
    if ((error = dlerror()) != NULL) {
        LOGE("find face_feature method failed: %s", error);
    }

    /* *(void **)(&load_models) = dlsym(libcore_handle, "deepcam_sdk_init");
     if ((error = dlerror()) != NULL)  {
         LOGE("find init method3 failed: %s", error);
     }*/
    if (libcore_init == NULL || libcore_face_detect == NULL || libcore_face_detect == NULL) {
        return 3;
    }
    //TODO：获取pb文件，逻辑如下：首先获取assets目录下的文件，没有获取到再读取sdcard配置文件的路径
    std::vector<char> model_mt;
    std::vector<char> model_dr;
    int load_mt_ret = obtModelFile(env, asset_manager, "deepcam_dr_model.pb", model_dr);
    int load_dr_ret = obtModelFile(env, asset_manager, "deepcam_mt_model.pb", model_mt);
//    ret = libcore_init(model_mt, model_dr);


    if (load_dr_ret != 0 || load_mt_ret != 0) {
        LOGE("Load pb file badly!");
        return 4;
    }

    pb_decrypt(model_dr);
    pb_decrypt(model_mt);

    ret = libcore_init(model_mt, model_dr);

    if (ret == 0) {
        LOGI("init sdk success");
        return 0;
    } else {
        return 5;
    }

}

/**
* 解析人脸
* @param env
* @param clazz
* @param picture
* @param output
* @return
*/
JNIEXPORT jboolean JNICALL
native_face_detect(JNIEnv *env, jclass clazz, jintArray data, jint width, jint height,
                   jboolean isStrict, jobject output) {
    jint *pixelAddr = env->GetIntArrayElements(data, JNI_FALSE);
    int ret = 0;
    int tmp;
    std::vector<face_box> face_info;
    unsigned long start_time, end_time;

    if (!has_disable) {//关闭使用功能
        return false;
    }
    if (libcore_face_detect == NULL) {
        return false;
    }


    ret = libcore_face_detect(height, width, pixelAddr, face_info);

    LOGI("face detect ret === %d", ret);
    if (ret == 0) {
        if (isStrict) {
            for (unsigned int i = 0; i < face_info.size(); i++) {
                face_box &box = face_info[i];
                LOGD("face %d: x0,y0 %2.5f %2.5f  x1,y1 %2.5f  %2.5f conf: %2.5f\n", i,
                     box.x0, box.y0, box.x1, box.y1, box.score);
                //图片模糊度处理方法
                if (libFaceLightBlur != NULL) {
                    std::vector<float> landmarks;
                    landmarks.clear();
                    float light, blur;
                    for (int j = 0; j < 5; j++) {
                        landmarks.push_back(box.landmark.x[j]);
                        landmarks.push_back(box.landmark.y[j]);
                    }
                    libFaceLightBlur(height, width, pixelAddr, landmarks, light,
                                     blur);//人脸的模糊度计算

                    LOGD("light = %f, blur = %f", light, blur);
                    /*if (blur < 60.0f) {//小于60.0f表示比较模糊
                        return FACE_STATUS_OFF_QUALITY;
                    }
                    if (light < 80) {
                        return FACE_STATUS_OFF_QUALITY;
                    }*/
                    setOutputFaceInfo(env, output, face_info, light, blur);

//                AndroidBitmap_unlockPixels(env, picture);
                }
            }
        } else {
            setOutputFaceInfo(env, output, face_info, 0, 0);
        }
        env->ReleaseIntArrayElements(data, pixelAddr, JNI_ABORT);
        return true;
    } else {
        env->ReleaseIntArrayElements(data, pixelAddr, JNI_ABORT);
        return false;
    }

}

JNIEXPORT jboolean JNICALL native_face_feature(
        JNIEnv *env,
        jclass clazz,
        jintArray data,
        jint width,
        jint height,
        jobject faceinfo,
        jobject output) {

    jint *pixelAddr = env->GetIntArrayElements(data, JNI_FALSE);
    jboolean ret = false;
    int tmp;
    std::vector<float> feature;
    unsigned long start_time, end_time;
    std::vector<float> landmarks;
//    float x0, y0, x1, y1;
    int i;

    if (!has_disable) {//关闭使用功能
        return false;
    }

    //TODO：2017-11-06修改了人脸模型提取模块
    if (libcore_face_feature == NULL) {
        return false;
    }

    if (!getFaceInfoValue(env, faceinfo, landmarks)) {
        LOGE("get faceinfo value failed");
        return false;
    }
    for (int i = 0; i < 10; i++) {
        LOGD("the landmarks %d:  %f", i, landmarks[i]);
    }

    int res = libcore_face_feature(height, width, pixelAddr, landmarks, feature);
//    setOutputFaceFeature(env, output, feature);
    if (res == 0) {
        setOutputFaceFeature(env, output, feature);
        env->ReleaseIntArrayElements(data, pixelAddr, JNI_ABORT);
        return true;
    }
    env->ReleaseIntArrayElements(data, pixelAddr, JNI_ABORT);
    return ret;
}

JNIEXPORT jfloat JNICALL
native_sorce(JNIEnv *env, jclass clazz, jfloatArray features1, jfloatArray features2) {
    if (!has_disable) {
        return 0.0f;
    }
    float ret = 0.0f;
    int size = 320;
    float tmp0 = 0.0f, tmp1 = 0.0f, tmp2 = 0.0f;
    float v1, v2;
    if (features1 == NULL || features2 == NULL) {
        return ret;
    }

    jsize len_1 = env->GetArrayLength(features1);
//    jsize len_1 = env->GetArrayLength(features1)
    jsize len_2 = env->GetArrayLength(features2);
    if (len_1 != size || len_2 != size) {
        return ret;
    }

    jfloat *c_array_feature_1;
    jfloat *c_array_feature_2;
    //1. 获取数组长度
    //2. 根据数组长度和数组元素的数据类型申请存放java数组元素的缓冲区
    c_array_feature_1 = (jfloat *) malloc(sizeof(jfloat) * len_1);
    c_array_feature_2 = (jfloat *) malloc(sizeof(jfloat) * len_2);
    //3. 初始化缓冲区
    memset(c_array_feature_1, 0, sizeof(jint) * len_1);
    memset(c_array_feature_2, 0, sizeof(jint) * len_2);
    printf("arr_len = %d ", len_1);
    //4. 拷贝Java数组中的所有元素到缓冲区中
    env->GetFloatArrayRegion(features1, 0, len_1, c_array_feature_1);
    env->GetFloatArrayRegion(features2, 0, len_2, c_array_feature_2);
    for (int i = 0; i < size; i++) {
        v1 = c_array_feature_1[i];
        v2 = c_array_feature_2[i];
        tmp0 += v1 * v2;
        tmp1 += v1 * v1;
        tmp2 += v2 * v2;  //5. 累加数组元素的和
    }
    ret = (tmp0 / (sqrt(tmp1) * sqrt(tmp2)));
    free(c_array_feature_1);  //6. 释放存储数组元素的缓冲区
    free(c_array_feature_2);  //6. 释放存储数组元素的缓冲区

    return ret;
}



JNIEXPORT void native_disable() {
    has_disable = false;
}


JNIEXPORT void JNICALL convertYUV420SPToARGB8888(
        JNIEnv *env, jclass clazz, jbyteArray input, jintArray output,
        jint width, jint height) {
    jbyte *_yuv = env->GetByteArrayElements(input, 0);
    jint *_argb = env->GetIntArrayElements(output, 0);
    Mat yuvMat(height + height / 2, width, CV_8UC1, (uchar *) _yuv);

    Mat src(height, width, CV_8UC4, _argb);
    cvtColor(yuvMat, src, CV_YUV420sp2BGRA);
    env->ReleaseByteArrayElements(input, _yuv, JNI_ABORT);
    env->ReleaseIntArrayElements(output, _argb, 0);
}


static JNINativeMethod gMethods[] = {
        {"init",                      "(Landroid/content/Context;Ljava/lang/String;Landroid/content/res/AssetManager;)I", (void *) native_init},
        {"faceDetect",                "([IIIZLjava/util/ArrayList;)Z",                                                    (void *) native_face_detect},
        {"faceFeature",               "([IIIL" FACEINFO_CLASS ";Ljava/util/ArrayList;)Z",                                 (void *) native_face_feature},
        {"sorce",                     "([F[F)F",                                                                          (void *) native_sorce},
        {"disable",                   "()V",                                                                              (void *) native_disable},
        {"convertYUV420SPToARGB8888", "([B[III)V",                                                                       (void *) convertYUV420SPToARGB8888}

};

static int registerNativeMethods(JNIEnv *env, const char *className,
                                 JNINativeMethod *gMethods, int numMethods) {
    jclass clazz;
    clazz = env->FindClass(className);
    if (clazz == 0) {
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

static int registerNatives(JNIEnv *env) {
    if (!registerNativeMethods(env, JNIREG_CLASS, gMethods,
                               sizeof(gMethods) / sizeof(gMethods[0])))
        return JNI_FALSE;

    return JNI_TRUE;
}

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;

    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        LOGE("ERROR: GetEnv failed\n");
        return -1;
    }
    assert(env != NULL);

    if (!registerNatives(env)) {
        return -1;
    }

    return JNI_VERSION_1_6;
}
