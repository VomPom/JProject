//
// Created by glumes on 2018/5/29.
//

#include <stdio.h>
#include "thread_operation.h"
#include <commonutil.h>

void *run(void *);

void *printThreadHello(void *);

static jmethodID methodPrintThreadName;
static jmethodID methodPrintNativeMsg;

static JavaVM *gVm = nullptr;
static jobject gObj = nullptr;
static pthread_mutex_t mutex;
static const char *runtimeException = "java/lang/RuntimeException";


JNIEXPORT int JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    gVm = vm;
    return JNI_VERSION_1_6;
}


extern "C"
JNIEXPORT void JNICALL
Java_wang_julis_learncpp_ops_ThreadOps_simpleNativeThread(JNIEnv *env, jobject thiz) {
    pthread_t handles;
    int result = pthread_create(&handles, nullptr, printThreadHello, nullptr);
    if (result != 0) {
        LOGE("create thread failed");
    } else {
        LOGE("create thread success");
    }
}
extern "C"
JNIEXPORT void JNICALL
Java_wang_julis_learncpp_ops_ThreadOps_nativeInit(JNIEnv *env, jobject thiz) {
    jclass jcls = env->GetObjectClass(thiz);
    methodPrintThreadName = env->GetMethodID(jcls, "printThreadName", "()V");
    methodPrintNativeMsg = env->GetMethodID(jcls, "printNativeMsg", "(Ljava/lang/String;)V");
    if (gObj == nullptr) {
        gObj = env->NewGlobalRef(thiz);
    }

    if (pthread_mutex_init(&mutex, nullptr) != 0) {
        throwByName(env, runtimeException, "Unable to initialize mutex");
    }
}
extern "C"
JNIEXPORT void JNICALL
Java_wang_julis_learncpp_ops_ThreadOps_nativeFree(JNIEnv *env, jobject thiz) {
    if (gObj != nullptr) {
        env->DeleteGlobalRef(gObj);
        gObj = nullptr;
    }
    if (pthread_mutex_destroy(&mutex) != 0) {
        throwByName(env, runtimeException, "Unable to destroy mutex");
    }
}
extern "C"
JNIEXPORT void JNICALL
Java_wang_julis_learncpp_ops_ThreadOps_posixThreads(JNIEnv *env, jobject thiz, jint threads, jint iterations) {
    auto *handles = new pthread_t[threads];

    for (int i = 0; i < threads; ++i) {
        auto *threadRunArgs = new ThreadRunArgs();
        threadRunArgs->id = i;
        threadRunArgs->result = i * i + i;
        // 创建一个线程，
        int result = pthread_create(&handles[i], nullptr, run, (void *) threadRunArgs);
        if (result != 0) {
            throwByName(env, runtimeException, "Unable to create thread");
        }
    }

    for (int i = 0; i < threads; ++i) {
        void *result = nullptr;
        if (pthread_join(handles[i], &result) != 0) {
            throwByName(env, runtimeException, "Unable to join thread");
        } else {
            LOGE("return value is %d", result);
            char message[26];
            sprintf(message, "Worker %d returned %d", i, result);
            jstring msg = env->NewStringUTF(message);
            env->CallVoidMethod(gObj, methodPrintNativeMsg, msg);
            if (env->ExceptionOccurred() != nullptr) {
                return;
            }
        }
    }
}

/**
 * 相当于 Thread 的 run 方法
 * @param args
 * @return
 */
void *run(void *args) {
    JNIEnv *env = nullptr;
    // 将当前线程添加到 Java 虚拟机上
    auto *threadRunArgs = (ThreadRunArgs *) args;

    if (gVm->AttachCurrentThread(&env, nullptr) == 0) {
        if (pthread_mutex_lock(&mutex) != 0) {
            throwByName(env, runtimeException, "Unable to lock mutex");
        }
        env->CallVoidMethod(gObj, methodPrintThreadName);

        if (pthread_mutex_unlock(&mutex)) {
            throwByName(env, runtimeException, "Unable to unlock mutex");
        }
        // 从 Java 虚拟机上分离当前线程
        gVm->DetachCurrentThread();
    }
    return (void *) threadRunArgs->result;
}

void *printThreadHello(void *) {
    LOGE("hello native simple thread");
    return nullptr;
}