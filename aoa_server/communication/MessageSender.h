#ifndef MESSAGE_SENDER_H
#define MESSAGE_SENDER_H


#include <thread>

#include "../partial/SafeQueue.h"
#include "Message.h"
#include "../partial/StringBuilder.h"


class MessageSender {
    private:
        StringBuilder *sb;
        SafeQueue<Message *> *messageQueue;
        void runSending();

/**
        *	Sends a message via a socket given.
        */
            bool sendMsg(int sock, std::string txt);

        /**
        *	Broadcasts a message to all clients passed by arguments.
        */
        void sendMsg(fd_set *socks, std::string txt);

    public:
        MessageSender(SafeQueue<Message *> *messageQueue);
        std::thread run();
};




#endif
