#ifndef SEMAPHORE_H
#define SEMAPHORE_H

#include <cstring>
#include <mutex>
#include <condition_variable>


class Semaphore {
    private:
        int count;
        std::mutex mtx;
        std::condition_variable cv;

    public:
        Semaphore (int count);
        void wait();
        void notify();
};


#endif



