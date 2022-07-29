//
// Created by glumes on 2018/5/3.
//

#include <jni.h>
#include <logutil.h>

// 全局变量，作为缓存方法 id
jmethodID instanceMethodCache;


extern "C"
JNIEXPORT void JNICALL
Java_wang_julis_learncpp_ops_CacheFieldAndMethodOps_initCacheMethodId(JNIEnv *env, jclass clazz) {
    jclass cls = env->FindClass("wang/julis/learncpp/model/Animal");
    instanceMethodCache = env->GetMethodID(cls, "getName", "()Ljava/lang/String;");
}
extern "C"
JNIEXPORT void JNICALL
Java_wang_julis_learncpp_ops_CacheFieldAndMethodOps_callCacheMethod(JNIEnv *env, jobject thiz, jobject animal) {
    auto name = (jstring) env->CallObjectMethod(animal, instanceMethodCache);
    const char *c_name = env->GetStringUTFChars(name, nullptr);
    LOGE("call cache method and value is %s", c_name);
}
extern "C"
JNIEXPORT void JNICALL
Java_wang_julis_learncpp_ops_CacheFieldAndMethodOps_staticCacheField(JNIEnv *env, jobject thiz, jobject animal) {
    static jfieldID fid = nullptr; // 声明为 static 变量进行缓存
    jclass cls = env->FindClass("wang/julis/learncpp/model/Animal");
    jstring jstr;
    const char *c_str;
    if (fid == nullptr) {
        fid = env->GetFieldID(cls, "name", "Ljava/lang/String;");
        if (fid == nullptr) {
            LOGE("can't find method:name in Animal");
        }
    } else {
        LOGE("find name filed in Animal");
    }

    jstr = (jstring) env->GetObjectField(animal, fid);
    c_str = env->GetStringUTFChars(jstr, nullptr);
    if (c_str == nullptr) {
        return;
    }
    env->ReleaseStringUTFChars(jstr, c_str);
    jstr = env->NewStringUTF("new name");
    if (jstr == nullptr) {
        return;
    }
    env->SetObjectField(animal, fid, jstr);
}