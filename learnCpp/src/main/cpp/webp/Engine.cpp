//
// Created by julis.wang on 2022/8/11.
//

#include <logutil.h>
#include "Engine.h"

/*
 * webp files that are inside assets folder:
 *    they will be decoded and displayed as slide-show
 *    decoding will happen in its own thread
 */
const char *frames[] = {
        "clips/frame1.webp",
        "clips/frame2.webp",
        "clips/frame3.webp",
};
const int kFRAME_COUNT = sizeof(frames) / sizeof(frames[0]);
const int kFRAME_DISPLAY_TIME = 2;

// Engine class implementations
bool Engine::PrepareDrawing() {
    // create decoder
    if (decoder_) {
        decoder_->DestroyDecoder();
    }
    ANativeWindow_Buffer buf;
    if (ANativeWindow_lock(app_->window, &buf, nullptr) < 0) {
        LOGW("Unable to lock window buffer to create decoder");
        return false;
    }
    UpdateFrameBuffer(&buf, nullptr);
    ANativeWindow_unlockAndPost(app_->window);
    DecodeSurfaceDescriptor descriptor{};
    switch (buf.format) {
        case WINDOW_FORMAT_RGB_565:
            descriptor.format_ = SurfaceFormat::SURFACE_FORMAT_RGB_565;
            break;
        case WINDOW_FORMAT_RGBX_8888:
            descriptor.format_ = SurfaceFormat::SURFACE_FORMAT_RGBX_8888;
            break;
        case WINDOW_FORMAT_RGBA_8888:
            descriptor.format_ = SurfaceFormat::SURFACE_FORMAT_RGBA_8888;
            break;
        default:
            return false;
    }
    descriptor.width_ = buf.width;
    descriptor.height_ = buf.height;
    descriptor.stride_ = buf.stride;

    decoder_ = new WebpDecoder(frames, kFRAME_COUNT, &descriptor,
                               app_->activity->assetManager);
    assert(decoder_);
    decoder_->DecodeFrame();

    return true;
}

/*
 * Only copy decoded webp picture when:
 *  - current frame has been on for kFrame_DISPLAY_TIME seconds
 *  - a new picture is decoded
 * After copying, start decoding the next frame
 */
bool Engine::UpdateDisplay() {
    if (!app_->window || !decoder_) {
        assert(0);
    }
    struct timespec curTime{};
    clock_gettime(CLOCK_MONOTONIC, &curTime);
    if (curTime.tv_sec <
        (frameStartTime_.tv_sec + (__kernel_time_t) kFRAME_DISPLAY_TIME)) {
        // current frame is displayed less than required duration
        return false;
    }
    uint8_t *frame = decoder_->GetDecodedFrame();
    if (!frame)
        return false;

    ANativeWindow_Buffer buffer;
    if (ANativeWindow_lock(app_->window, &buffer, nullptr) < 0) {
        LOGW("Unable to lock window buffer");
        return false;
    }
    UpdateFrameBuffer(&buffer, frame);
    ANativeWindow_unlockAndPost(app_->window);
    clock_gettime(CLOCK_MONOTONIC, &frameStartTime_);

    // start decoding next frame
    decoder_->DecodeFrame();
    return true;
}

/*
 * UpdateFrameBuffer():
 *     Internal function to perform bits copying onto current frame buffer
 *     src:
 *        - if nullptr, blank it
 *        - otherwise,  copy to given buf
 *     assumption:
 *         src and bug MUST be in the same geometry format & layout
 */
void Engine::UpdateFrameBuffer(ANativeWindow_Buffer *buf, uint8_t *src) {
    // src is either null: to blank the screen
    //     or holding exact pixels with the same fmt [stride is the SAME]
    auto *dst = reinterpret_cast<uint8_t *> (buf->bits);
    uint32_t bpp;
    switch (buf->format) {
        case WINDOW_FORMAT_RGB_565:
            bpp = 2;
            break;
        case WINDOW_FORMAT_RGBA_8888:
        case WINDOW_FORMAT_RGBX_8888:
            bpp = 4;
            break;
        default:
            assert(0);
    }
    uint32_t stride, width;
    stride = buf->stride * bpp;
    width = buf->width * bpp;
    if (src) {
        for (auto height = 0; height < buf->height; ++height) {
            memcpy(dst, src, width);
            dst += stride, src += stride;
        }
    } else {
        for (auto height = 0; height < buf->height; ++height) {
            memset(dst, 0, width);
            dst += stride;
        }
    }
}

