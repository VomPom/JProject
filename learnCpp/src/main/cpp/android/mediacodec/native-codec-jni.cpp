#include <jni.h>

//
// Created by julis.wang on 2022/8/12.
//

/* This is a JNI example where we use native methods to play video
 * using OpenMAX AL. See the corresponding Java source file located at:
 *
 *   src/com/example/nativemedia/NativeMedia/NativeMedia.java
 *
 * In this example we use assert() for "impossible" error conditions,
 * and explicit handling and recovery for more likely error conditions.
 */


#include <cassert>
#include <jni.h>
#include <cstdio>
#include <cstring>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <cerrno>
#include <climits>

#include "looper.h"
#include "media/NdkMediaCodec.h"
#include "media/NdkMediaExtractor.h"

// for __android_log_print(ANDROID_LOG_INFO, "YourApp", "formatted message");
#include <android/log.h>

#define TAG "NativeCodec"
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

// for native window JNI
#include <android/native_window_jni.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>


typedef struct {
    int fd;
    ANativeWindow *window;
    AMediaExtractor *ex;
    AMediaCodec *codec;
    int64_t renderstart;
    bool sawInputEOS;
    bool sawOutputEOS;
    bool isPlaying;
    bool renderonce;
} workerdata;

workerdata data = {-1, nullptr, nullptr, nullptr, 0, false, false, false, false};

enum {
    kMsgCodecBuffer,
    kMsgPause,
    kMsgResume,
    kMsgPauseAck,
    kMsgDecodeDone,
    kMsgSeek,
};

class mylooper : public looper {
    void handle(int what, void *obj) override;
};

static mylooper *mlooper = nullptr;

int64_t systemnanotime() {
    timespec now{};
    clock_gettime(CLOCK_MONOTONIC, &now);
    return now.tv_sec * 1000000000LL + now.tv_nsec;
}

void doCodecWork(workerdata *d) {
    ssize_t bufidx;
    if (!d->sawInputEOS) {
        bufidx = AMediaCodec_dequeueInputBuffer(d->codec, 2000);
        LOGV("input buffer %zd", bufidx);
        if (bufidx >= 0) {
            size_t bufsize;
            auto buf = AMediaCodec_getInputBuffer(d->codec, bufidx, &bufsize);
            auto sampleSize = AMediaExtractor_readSampleData(d->ex, buf, bufsize);
            if (sampleSize < 0) {
                sampleSize = 0;
                d->sawInputEOS = true;
                LOGV("EOS");
            }
            auto presentationTimeUs = AMediaExtractor_getSampleTime(d->ex);

            AMediaCodec_queueInputBuffer(d->codec, bufidx, 0, sampleSize, presentationTimeUs,
                                         d->sawInputEOS ? AMEDIACODEC_BUFFER_FLAG_END_OF_STREAM : 0);
            AMediaExtractor_advance(d->ex);
        }
    }

    if (!d->sawOutputEOS) {
        AMediaCodecBufferInfo info;
        auto status = AMediaCodec_dequeueOutputBuffer(d->codec, &info, 0);
        if (status >= 0) {
            if (info.flags & AMEDIACODEC_BUFFER_FLAG_END_OF_STREAM) {
                LOGV("output EOS");
                d->sawOutputEOS = true;
            }
            int64_t presentationNano = info.presentationTimeUs * 1000;
            if (d->renderstart < 0) {
                d->renderstart = systemnanotime() - presentationNano;
            }
            int64_t delay = (d->renderstart + presentationNano) - systemnanotime();
            if (delay > 0) {
                usleep(delay / 1000);
            }
            AMediaCodec_releaseOutputBuffer(d->codec, status, info.size != 0);
            if (d->renderonce) {
                d->renderonce = false;
                return;
            }
        } else if (status == AMEDIACODEC_INFO_OUTPUT_BUFFERS_CHANGED) {
            LOGV("output buffers changed");
        } else if (status == AMEDIACODEC_INFO_OUTPUT_FORMAT_CHANGED) {
            auto format = AMediaCodec_getOutputFormat(d->codec);
            LOGV("format changed to: %s", AMediaFormat_toString(format));
            AMediaFormat_delete(format);
        } else if (status == AMEDIACODEC_INFO_TRY_AGAIN_LATER) {
            LOGV("no output buffer right now");
        } else {
            LOGV("unexpected info code: %zd", status);
        }
    }

    if (!d->sawInputEOS || !d->sawOutputEOS) {
        mlooper->post(kMsgCodecBuffer, d);
    }
}

void mylooper::handle(int what, void *obj) {
    switch (what) {
        case kMsgCodecBuffer:
            doCodecWork((workerdata *) obj);
            break;
        case kMsgDecodeDone: {
            auto *d = (workerdata *) obj;
            AMediaCodec_stop(d->codec);
            AMediaCodec_delete(d->codec);
            AMediaExtractor_delete(d->ex);
            d->sawInputEOS = true;
            d->sawOutputEOS = true;
        }
            break;

        case kMsgSeek: {
            auto *d = (workerdata *) obj;
            AMediaExtractor_seekTo(d->ex, 0, AMEDIAEXTRACTOR_SEEK_NEXT_SYNC);
            AMediaCodec_flush(d->codec);
            d->renderstart = -1;
            d->sawInputEOS = false;
            d->sawOutputEOS = false;
            if (!d->isPlaying) {
                d->renderonce = true;
                post(kMsgCodecBuffer, d);
            }
            LOGV("seeked");
        }
            break;

        case kMsgPause: {
            auto *d = (workerdata *) obj;
            if (d->isPlaying) {
                // flush all outstanding codecbuffer messages with a no-op message
                d->isPlaying = false;
                post(kMsgPauseAck, nullptr, true);
            }
        }
            break;

        case kMsgResume: {
            auto *d = (workerdata *) obj;
            if (!d->isPlaying) {
                d->renderstart = -1;
                d->isPlaying = true;
                post(kMsgCodecBuffer, d);
            }
        }
            break;
    }
}



// create streaming media player
extern "C"
JNIEXPORT jboolean JNICALL
Java_wang_julis_learncpp_mediacodec_NativeCodecActivity_00024Companion_createStreamingMediaPlayer(JNIEnv *env,
                                                                                                  jobject thiz,
                                                                                                  jobject asset_mgr,
                                                                                                  jstring filename) {
    // convert Java string to UTF-8
    const char *utf8 = env->GetStringUTFChars(filename, nullptr);
    LOGV("opening %s", utf8);

    off_t outStart, outLen;
    int fd = AAsset_openFileDescriptor(AAssetManager_open(AAssetManager_fromJava(env, asset_mgr), utf8, 0),
                                       &outStart, &outLen);

    env->ReleaseStringUTFChars(filename, utf8);
    if (fd < 0) {
        LOGE("failed to open file: %s %d (%s)", utf8, fd, strerror(errno));
        return JNI_FALSE;
    }

    data.fd = fd;

    workerdata *d = &data;

    AMediaExtractor *ex = AMediaExtractor_new();
    media_status_t err = AMediaExtractor_setDataSourceFd(ex, d->fd,
                                                         static_cast<off64_t>(outStart),
                                                         static_cast<off64_t>(outLen));
    close(d->fd);
    if (err != AMEDIA_OK) {
        LOGV("setDataSource error: %d", err);
        return JNI_FALSE;
    }

    int numtracks = AMediaExtractor_getTrackCount(ex);
    AMediaCodec *codec;

    LOGV("input has %d tracks", numtracks);
    for (int i = 0; i < numtracks; i++) {
        AMediaFormat *format = AMediaExtractor_getTrackFormat(ex, i);
        const char *s = AMediaFormat_toString(format);
        LOGV("track %d format: %s", i, s);
        const char *mime;
        if (!AMediaFormat_getString(format, AMEDIAFORMAT_KEY_MIME, &mime)) {
            LOGV("no mime type");
            return JNI_FALSE;
        } else if (!strncmp(mime, "video/", 6)) {
            // Omitting most error handling for clarity.
            // Production code should check for errors.
            AMediaExtractor_selectTrack(ex, i);
            codec = AMediaCodec_createDecoderByType(mime);
            AMediaCodec_configure(codec, format, d->window, NULL, 0);
            d->ex = ex;
            d->codec = codec;
            d->renderstart = -1;
            d->sawInputEOS = false;
            d->sawOutputEOS = false;
            d->isPlaying = false;
            d->renderonce = true;
            AMediaCodec_start(codec);
        }
        AMediaFormat_delete(format);
    }

    mlooper = new mylooper();
    mlooper->post(kMsgCodecBuffer, d);

    return JNI_TRUE;
}

extern "C"
JNIEXPORT void JNICALL
Java_wang_julis_learncpp_mediacodec_NativeCodecActivity_00024Companion_setPlayingStreamingMediaPlayer(JNIEnv *env,
                                                                                                      jobject thiz,
                                                                                                      jboolean is_playing) {
    if (mlooper) {
        if (is_playing) {
            mlooper->post(kMsgResume, &data);
        } else {
            mlooper->post(kMsgPause, &data);
        }
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_wang_julis_learncpp_mediacodec_NativeCodecActivity_00024Companion_setSurface(JNIEnv *env, jobject thiz,
                                                                                  jobject surface) {
    // obtain a native window from a Java surface
    if (data.window) {
        ANativeWindow_release(data.window);
        data.window = nullptr;
    }
    data.window = ANativeWindow_fromSurface(env, surface);
    LOGV("@@@ setsurface %p", data.window);
}

extern "C"
JNIEXPORT void JNICALL
Java_wang_julis_learncpp_mediacodec_NativeCodecActivity_00024Companion_shutdown(JNIEnv *env, jobject thiz) {
    LOGV("@@@ shutdown");
    if (mlooper) {
        mlooper->post(kMsgDecodeDone, &data, true /* flush */);
        mlooper->quit();
        delete mlooper;
        mlooper = nullptr;
    }
    if (data.window) {
        ANativeWindow_release(data.window);
        data.window = nullptr;
    }
}
extern "C"
JNIEXPORT void JNICALL
Java_wang_julis_learncpp_mediacodec_NativeCodecActivity_00024Companion_rewindStreamingMediaPlayer(JNIEnv *env,
                                                                                                  jobject thiz) {
    if (mlooper) {
        mlooper->post(kMsgSeek, &data);
    }
}
