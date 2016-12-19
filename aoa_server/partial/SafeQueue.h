#ifndef SAFE_QUEUE_H
#define SAFE_QUEUE_H

// libraries
#include <string>
#include <queue>
#include <thread>
#include <mutex>
#include <condition_variable>


template <typename T>
class SafeQueue {
	private:
		/**
		*	Standard queue native representation.
		*/
		std::queue<T> queue;

		/**
		*	Mutex for memory access management/locking shared memory.
		*/
		std::mutex mutex;

		/**
		*	
		*/
		std::condition_variable cond;

	public:
		T pop() {
			std::unique_lock<std::mutex> mlock(this->mutex);

			while (this->queue.empty()) {
				this->cond.wait(mlock);
			}
			auto item = this->queue.front();
			this->queue.pop();
			return item;
		}

		void pop(T& item) {
			std::unique_lock<std::mutex> mlock(this->mutex);

			while (this->queue.empty()) {
				this->cond.wait(mlock);
			}
			item = this->queue.front();
			this->queue.pop();
		}

		void push(const T& item) {
			std::unique_lock<std::mutex> mlock(this->mutex);

			this->queue.push(item);
			mlock.unlock();
			this->cond.notify_one();
		}

		// void push(T&& item) {
		// 	std::unique_lock<std::mutex> mlock(this->mutex);

		// 	this->queue.push(std::move(item));
		// 	mlock.unlock();
		// 	this->cond.notify_one();
		// }
};



#endif





