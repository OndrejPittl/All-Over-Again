#ifndef RECEIVER_H
#define RECEIVER_H

#include <string>
#include <vector>
#include <thread>


#include "../partial/SafeQueue.h"
#include "../partial/Semaphore.h"
#include "Message.h"
#include "RawMessage.h"


class MessageProcessor {

    private:
        static const char MSG_STX;
        static const char MSG_ETX;
        static const char MSG_DELIMITER;

        SafeQueue<Message *> *messageQueue;
        SafeQueue<RawMessage *> *readableMessages;

        void runProcessing();
        std::vector<std::string> separateMessages(RawMessage msg);
        bool checkMessageChecksum(std::string msg, std::string *pureMessage);



    public:
        /**
         * Constructor.
         * @param queue
         * @param readableMessages
         */
        MessageProcessor(SafeQueue<Message *> *queue, SafeQueue<RawMessage *> *readableMessages);

        /**
         * Runs a MSGProcessor in a separate thread.
         * @return  thread
         */
        std::thread run();

};

#endif
