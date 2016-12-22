#ifndef RECEIVER_H
#define RECEIVER_H

#include <string>
#include <thread>


#include "../partial/SafeQueue.h"
#include "../partial/Semaphore.h"
#include "Message.h"
#include "MessageSerializer.h"


class Receiver {

    private:

        MessageSerializer *serializer;

        SafeQueue<Message *> *messageQueue;
        SafeQueue<Message *> *readableMessages;

        void runReceiving();
        std::string recvMsg(int sock, int byteCount);
        void dumpFunction();


    public:
        Receiver(SafeQueue<Message *> *queue, SafeQueue<Message *> *readableMessages);
        std::thread run();


};

#endif
