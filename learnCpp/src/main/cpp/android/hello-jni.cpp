#include <jni.h>
#include <cstdio>
#include <pthread.h>
#include <cassert>

//
// Created by julis.wang on 2022/8/2.
//


extern "C"
JNIEXPORT jstring JNICALL
Java_wang_julis_learncpp_ops_JniOperation_hellJni(JNIEnv *env, jobject thiz) {
#if defined(__arm__)
    #if defined(__ARM_ARCH_7A__)
        #if defined(__ARM_NEON__)
            #if defined(__ARM_PCS_VFP)
                #define ABI "armeabi-v7a/NEON (hard-float)"
            #else
                #define ABI "armeabi-v7a/NEON"
            #endif
        #else
        #if defined(__ARM_PCS_VFP)
            #define ABI "armeabi-v7a (hard-float)"
        #else
            #define ABI "armeabi-v7a"
        #endif
    #endif
    #else
#define ABI "armeabi"
#endif
#elif defined(__aarch64__)
#define ABI "arm64-v8a"
#else
#define ABI "unknown"
#endif
    return (*env).NewStringUTF("Hello from JNI !  Compiled with ABI " ABI ".");
}
