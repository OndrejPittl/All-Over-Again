#ifndef RECEIVER_H
#define RECEIVER_H

#include <string>
#include <vector>
#include <thread>


#include "../partial/SafeQueue.h"
#include "../partial/Semaphore.h"
#include "Message.h"
#include "RawMessage.h"
#include "../partial/StringBuilder.h"


class MessageValidator {

    /**
     *
     * Separates messages with STX and ETX flags.
     *
     * Controls:
     *  a) STX, ETX
     *  b) checksum
     *
     * example of an accepted message: *138;Ahoooj#*2;Jak se mas?#
     *
     */


    private:
        static const char MSG_STX;
        static const char MSG_ETX;
        static const char MSG_DELIMITER;

        SafeQueue<Message *> *messageQueue;
        SafeQueue<RawMessage *> *rawMessageQueue;
        StringBuilder *log;

        void init();
        void runValidation();
        std::vector<std::string> separateMessages(RawMessage msg);
        bool checkMessageChecksum(std::string msg, std::string *pureMessage);



    public:
        /**
         * Constructor.
         * @param queue
         * @param readableMessages
         */
        MessageValidator(SafeQueue<Message *> *queue, SafeQueue<RawMessage *> *readableMessages);

        /**
         * Runs a MSGValidator in a separate thread.
         * @return  thread
         */
        std::thread run();

        bool checkHelloPacket(std::string msg, std::string *pureMessage);
};

#endif
