//
// Created by DeepCam on 2017/9/25.
//

#ifndef DEXPARSER_SAFEUTIL_H
#define DEXPARSER_SAFEUTIL_H

#include <vector>
#include <string>
#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

/**
 * 获取包名
 * @param env
 * @param obj  当前context
 * @return  包名
 */
jstring getPackageName(JNIEnv *env, jobject obj);


char* jstringToChar(JNIEnv* env, jstring jstr);

int obtModelFile(JNIEnv *env,jobject assetManager, char* model_name,std::vector<char>& model);
/**
 * 白名单文件
 * @param env
 * @param obj 当前对象
 * @param apiKey apikey
 * @return
 */
jboolean whiteLists(JNIEnv *env,jobject obj,jstring apiKey);

/**
 * 加载模型文件
 * @param fname
 * @param buf
 * @return
 */
int load_file(std::string &fname, std::vector<char>& buf);

/**
 * pb文件进行解密
 * @param buf
 */
void pb_decrypt(std::vector<char>& buf);
#ifdef __cplusplus
}
#endif
#endif //DEXPARSER_SAFEUTIL_H
