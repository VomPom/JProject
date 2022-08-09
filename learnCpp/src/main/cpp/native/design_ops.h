//
// Created by julis.wang on 2022/7/31.
//

#ifndef JPROJECT_DESIGN_OPS_H
#define JPROJECT_DESIGN_OPS_H

#include <logutil.h>


class design_ops {

};

class Command {
public:
    virtual ~Command() {};

    virtual void execute() = 0;
};

class FrameBuffer {
public:
    void draw(int x, int y) {
        pixels[(WIDTH * y) + x] = 1;
    }

    char *getPixels() {
        return pixels;
    }

private:
    static const int WIDTH = 10;
    static const int HEIGHT = 10;
    char pixels[WIDTH * HEIGHT];
};

class Scene {
public:
    Scene() : current(&buffers[0]), next(&buffers[1]) {}

    void draw() {
        next->draw(1, 1);
        next->draw(0, 3);
        next->draw(2, 1);
        swap();
    }

    FrameBuffer *getBuffer() {
        return current;
    }

private:
    FrameBuffer buffers[2];
    FrameBuffer *current;
    FrameBuffer *next;

    void swap() {
        FrameBuffer *temp = current;
        current = next;
        next = temp;
    };
};


#endif //JPROJECT_DESIGN_OPS_H
