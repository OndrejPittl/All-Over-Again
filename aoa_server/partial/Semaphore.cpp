#include <mutex>
#include "Semaphore.h"



Semaphore::Semaphore (int count) {
    this->count = count;
}

void Semaphore::notify() {
    std::unique_lock<std::mutex> lock(this->mtx);
    this->count++;
    this->cv.notify_one();
}

void Semaphore::wait() {
    std::unique_lock<std::mutex> lock(this->mtx);

    while(this->count == 0){
        this->cv.wait(lock);
    }

    this->count--;
}
