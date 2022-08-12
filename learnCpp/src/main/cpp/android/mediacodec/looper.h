//
// Created by julis.wang on 2022/8/12.
//


#include <pthread.h>
#include <semaphore.h>

struct loopermessage;

class looper {
public:
    looper();
    looper& operator=(const looper& ) = delete;
    looper(looper&) = delete;
    virtual ~looper();

    void post(int what, void *data, bool flush = false);
    void quit();

    virtual void handle(int what, void *data);

private:
    void addmsg(loopermessage *msg, bool flush);
    static void* trampoline(void* p);
    void loop();
    loopermessage *head{};
    pthread_t worker{};
    sem_t headwriteprotect{};
    sem_t headdataavailable{};
    bool running;
};
