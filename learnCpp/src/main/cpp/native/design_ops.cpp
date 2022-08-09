//
// Created by julis.wang on 2022/7/31.
//

#include "design_ops.h"
#include <jni.h>

class JumpCommand : public Command {
public:

    void execute() override {
        jump();
    }

private:
    static void jump() {
        LOGE("run jump");
    }
};

class ShotCommand : public Command {
public:

    void execute() override {
        shot();
    }

private:
    static void shot() {
        LOGE("run shot");
    }
};


class InputHandler {
public:
    Command *handleInput(int type) {
        if (type == 0) {
            return btnX;
        } else if (type == 1) {
            return btnY;
        } else {
            return nullptr;
        }
    }

private:
    Command *btnX = new JumpCommand();
    Command *btnY = new ShotCommand();
};

extern "C"
JNIEXPORT void JNICALL
Java_wang_julis_learncpp_ops_DesignOps_command(JNIEnv *env, jobject thiz) {
    auto *inputHandler = new InputHandler();
    inputHandler->handleInput(0)->execute();
    inputHandler->handleInput(1)->execute();
}





extern "C"
JNIEXPORT void JNICALL
Java_wang_julis_learncpp_ops_DesignOps_doubleFrameBuffer(JNIEnv *env, jobject thiz) {
    auto *scene = new Scene();
    scene->draw();
    auto *current = scene->getBuffer();
    char *currentPixels = current->getPixels();

    LOGE("currentPixels:%s", currentPixels);
}