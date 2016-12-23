//#ifndef SAFE_QUEUE_H
//#define SAFE_QUEUE_H
//
//// libraries
//#include <iostream>
//#include <string>
//#include <queue>
//#include <thread>
//#include <mutex>
//#include <condition_variable>
//
//
//template <typename T>
//class SafeQueue {
//	private:
//		int counter = 0;
//
//		/**
//		*	Standard native queue.
//		*/
//		std::queue<T> queue;
//
//		/**
//		*	Mutex for memory access management/locking shared memory.
//		*/
//		std::mutex mutex;
//
//		/**
//		*
//		*/
//		std::condition_variable cond;
//
//	public:
//		T pop() {
//			std::unique_lock<std::mutex> mlock(this->mutex);
//
//			while (this->queue.empty()) {
//				this->cond.wait(mlock);
//			}
//			auto item = this->queue.front();
//			this->queue.pop();
//            this->counter--;
//            this->printCount();
//			return item;
//		}
//
//		void pop(T& item) {
//			std::unique_lock<std::mutex> mlock(this->mutex);
//
//			while (this->queue.empty()) {
//				this->cond.wait(mlock);
//			}
//			item = this->queue.front();
//			this->queue.pop();
//			this->counter--;
//            this->printCount();
//		}
//
//		void push(const T& item) {
//			std::unique_lock<std::mutex> mlock(this->mutex);
//
//			this->queue.push(item);
//            this->counter++;
//            this->printCount();
//			mlock.unlock();
//			this->cond.notify_one();
//
//
//		}
//
//        void printCount(){
//            std::cout << "Queue: " << this->counter << " messages." << std::endl;
//        }
//
//		// void push(T&& item) {
//		// 	std::unique_lock<std::mutex> mlock(this->mutex);
//
//		// 	this->queue.push(std::move(item));
//		// 	mlock.unlock();
//		// 	this->cond.notify_one();
//		// }
//};
//
//
//
//#endif
//
//
//
//
//





#ifndef SAFE_QUEUE_H
#define SAFE_QUEUE_H

#include <iostream>
#include <pthread.h>
#include <list>
#include "../communication/Message.h"

using namespace std;

template <typename T> class SafeQueue
{
    list<T>   m_queue;
    pthread_mutex_t m_mutex;
    pthread_cond_t  m_condv;

public:
    SafeQueue() {
        pthread_mutex_init(&m_mutex, NULL);
        pthread_cond_init(&m_condv, NULL);
    }
    ~SafeQueue() {
        pthread_mutex_destroy(&m_mutex);
        pthread_cond_destroy(&m_condv);
    }
    void push(T item) {
        pthread_mutex_lock(&m_mutex);
        m_queue.push_back(item);
        pthread_cond_signal(&m_condv);
        pthread_mutex_unlock(&m_mutex);
    }
    T pop() {
        pthread_mutex_lock(&m_mutex);
        while (m_queue.size() == 0) {
            pthread_cond_wait(&m_condv, &m_mutex);
        }
        T item = m_queue.front();
        m_queue.pop_front();
        pthread_mutex_unlock(&m_mutex);
        return item;
    }
    int size() {
        pthread_mutex_lock(&m_mutex);
        int size = (int) m_queue.size();
        pthread_mutex_unlock(&m_mutex);
        return size;
    }

    void print() {

    }
};

#endif