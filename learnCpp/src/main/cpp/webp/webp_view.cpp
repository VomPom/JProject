//
// Created by julis.wang on 2022/8/11.
//

#include <cstdio>
#include <cassert>
#include <android/native_window.h>
#include <android_native_app_glue.h>
#include <cstring>

#include <android/log.h>
#include <logutil.h>
#include "webp_decode.h"
#include "Engine.h"


static void ProcessAndroidCmd(struct android_app *app, int32_t cmd) {
    static int32_t format = WINDOW_FORMAT_RGB_565;
    auto *engine = reinterpret_cast<Engine *>(app->userData);

    switch (cmd) {
        case APP_CMD_INIT_WINDOW:
            if (engine->AndroidApp()->window != nullptr) {
                // save current format to format variable, and set
                // display format to 565 to save time coping. normally
                // 565 might be buggy ( if 565 works, 32 bit mostly like
                // would work )
                format = ANativeWindow_getFormat(app->window);
                ANativeWindow_setBuffersGeometry(app->window,
                                                 ANativeWindow_getWidth(app->window),
                                                 ANativeWindow_getHeight(app->window),
                                                 WINDOW_FORMAT_RGB_565);
                engine->PrepareDrawing();
                engine->UpdateDisplay();
                engine->StartAnimation(true);
            }
            break;
        case APP_CMD_TERM_WINDOW:
            engine->StartAnimation(false);
            engine->TerminateDisplay();
            ANativeWindow_setBuffersGeometry(app->window,
                                             ANativeWindow_getWidth(app->window),
                                             ANativeWindow_getHeight(app->window),
                                             format);
            break;
        case APP_CMD_LOST_FOCUS:
            engine->StartAnimation(false);
            engine->UpdateDisplay();
            break;
        default:
            break;
    }
}

static int32_t ProcessAndroidInput(struct android_app *app, AInputEvent *event) {
    auto *engine = reinterpret_cast<Engine *>(app->userData);
    if (AInputEvent_getType(event) == AINPUT_EVENT_TYPE_MOTION) {
        engine->StartAnimation(true);
        return 1;
    } else if (AInputEvent_getType(event) == AINPUT_EVENT_TYPE_KEY) {
        LOGI("Key event: action=%d keyCode=%d metaState=0x%x",
             AKeyEvent_getAction(event),
             AKeyEvent_getKeyCode(event),
             AKeyEvent_getMetaState(event));
    }
    return 0;
}

// Android application glue entry function for us
extern "C"
__attribute__((unused)) void android_main(struct android_app *state) {

    Engine engine(state);

    state->userData = reinterpret_cast<void *>(&engine);
    state->onAppCmd = ProcessAndroidCmd;
    state->onInputEvent = ProcessAndroidInput;

    // loop waiting for stuff to do.
    while (true) {
        // Read all pending events.
        int ident;
        int events;
        struct android_poll_source *source;

        // If not animating, we will block forever waiting for events.
        // If animating, we loop until all events are read, then continue
        // to draw the next frame of animation.
        while ((ident = ALooper_pollAll(engine.IsAnimating() ? 0 : -1, nullptr, &events,
                                        (void **) &source)) >= 0) {
            // Process this event.
            if (source != nullptr) {
                source->process(state, source);
            }
            if (ident == LOOPER_ID_MAIN) {
            }

            // Check if we are exiting.
            if (state->destroyRequested != 0) {
                LOGI("Engine thread destroy requested!");
                engine.TerminateDisplay();
                return;
            }
        }

        if (engine.IsAnimating()) {
            engine.UpdateDisplay();
        }
    }
}
