//
// Created by julis.wang on 2022/8/11.
//

#ifndef JPROJECT_ENGINE_H
#define JPROJECT_ENGINE_H

#include <android_native_app_glue.h>
#include <cstring>
#include "webp_decode.h"

/*
 * main object handles Android window frame update, and use webp to decode
 * pictures
 */
class Engine {
public:
    explicit Engine(android_app *app) :
            app_(app),
            decoder_(nullptr),
            animating_(false) {
        memset(&frameStartTime_, 0, sizeof(frameStartTime_));
    }

    ~Engine() {}

    struct android_app *AndroidApp(void) const { return app_; }

    void StartAnimation(bool start) { animating_ = start; }

    bool IsAnimating(void) const { return animating_; }

    void TerminateDisplay(void) { StartAnimation(false); }

    // PrepareDrawing(): Initialize the Engine with current native window geometry
    //   and blank current screen to avoid garbbage displaying on device
    bool PrepareDrawing(void);

    // Update webp file into display window when it is decoded
    // and current frame has been displayed with requested time
    bool UpdateDisplay(void);

private:
    static void UpdateFrameBuffer(ANativeWindow_Buffer *buf, uint8_t *src);

    struct android_app *app_;
    WebpDecoder *decoder_;
    bool animating_;
    struct timespec frameStartTime_;
};



#endif //JPROJECT_ENGINE_H
