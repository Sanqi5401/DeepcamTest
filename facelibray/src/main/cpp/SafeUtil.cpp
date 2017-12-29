//
// Created by DeepCam on 2017/9/25.
//
#include <jni.h>
#include "SafeUtil.h"
#include <string.h>
#include <vector>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include <string>
#include <bits/ios_base.h>
#include <ios>
#include <fstream>
#include <iostream>
#include <malloc.h>

using namespace std;
string packets[1] = {"com.deepcam.access"};
string keys[1] ={"d34jf282df"};


jstring getPackageName(JNIEnv *env, jobject obj) {
    jclass native_class = env->GetObjectClass(obj);
    jmethodID mId = env->GetMethodID(native_class, "getPackageName", "()Ljava/lang/String;");
    jstring packName = static_cast<jstring>(env->CallObjectMethod(obj, mId));
    return packName;
}


char *jstringToChar(JNIEnv *env, jstring jstr) {
    char *rtn = NULL;
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("utf-8");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
    jsize alen = env->GetArrayLength(barr);
    jbyte *ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (char *) malloc(alen + 1);

        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    return rtn;
}

/**
 * 获取模型文件数组
 * @param env
 * @param assetManager
 * @param tmp_model_file
 * @return
 */
int obtModelFile(JNIEnv *env, jobject assetManager, char *model_name, std::vector<char> &model) {

//        jboolean ret = false;
    off_t fileSize;
//        int numBytesRead;

    AAssetManager *mgr = AAssetManager_fromJava(env, assetManager);
    if (mgr == NULL) {
//            LOGE("the assetManager is NULL");
        return -1;
    }
    AAsset *asset = AAssetManager_open(mgr, model_name, AASSET_MODE_UNKNOWN);
    if (asset == NULL) {
//            LOGE("AAssetManager_open model.pb failed");
        return -2;
    }

    fileSize = AAsset_getLength(asset);
    model.resize(fileSize);
    AAsset_read(asset, model.data(), model.size());
    AAsset_close(asset);
//        ret = load_model(model);
    return 0;
}


/**
 * 爱华项目的白名单
 * @param env
 * @param context
 * @param apiKey
 * @return
 */
jboolean whiteLists(JNIEnv *env, jobject obj, jstring apiKey) {
    jboolean ret = false;
    jstring pname = getPackageName(env, obj);
    const char *_name = jstringToChar(env, pname);
    const char *_key = jstringToChar(env, apiKey);
    for (int i = 0; i < sizeof(packets); ++i) {
        int p_ret = strcmp(_name, packets[i].c_str());
        int k_ret = strcmp(_key, keys[i].c_str());
        if (p_ret == 0 && k_ret == 0) {
            env->ReleaseStringUTFChars(pname, _name);
            env->ReleaseStringUTFChars(apiKey, _key);
            return true;
        }

    }
    exit:
    env->ReleaseStringUTFChars(pname, _name);
    env->ReleaseStringUTFChars(apiKey, _key);
    return ret;
}

/**
 * 加载文件
 * @param fname 文件路径
 * @param buf 返回字节流
 * @return
 */
int load_file(std::string &fname, std::vector<char> &buf) {

    std::ifstream fs(fname.c_str(), std::ios::binary | std::ios::in);

    if (!fs.good()) {
        std::cerr << fname << " does not exist" << std::endl;
        return -1;
    }

    fs.seekg(0, std::ios::end);
    int fsize = fs.tellg();

    fs.seekg(0, std::ios::beg);
    buf.resize(fsize);
    fs.read(buf.data(), fsize);

    fs.close();

    return 0;
}

/**
 * 对pb文件进行解密
 * @param buf
 */
void pb_decrypt(std::vector<char> &buf) {
    static const char *key = "deepcam_tensorflow_protect";
    int key_len = strlen(key);
    int key_index = 0;
    for (int i = 0; i < buf.size(); i++) {
        buf[i] = buf[i] ^= key[key_index++];
        if (key_index >= key_len) {
            key_index = 0;
        }
    }
}




