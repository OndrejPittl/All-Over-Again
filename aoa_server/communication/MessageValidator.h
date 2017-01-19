#ifndef RECEIVER_H
#define RECEIVER_H

#include <string>
#include <vector>
#include <thread>


#include "../partial/SafeQueue.h"
#include "Message.h"
#include "RawMessage.h"
#include "../partial/StringBuilder.h"
#include "../core/Application.h"


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
        SafeQueue<Message *> *messageQueue;

        SafeQueue<RawMessage *> *rawMessageQueue;

        Application *app;

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
        MessageValidator(SafeQueue<Message *> *queue, SafeQueue<RawMessage *> *readableMessages, Application *app);

        /**
         * Runs a MSGValidator in a separate thread.
         * @return  thread
         */
        std::thread run();

        bool checkHelloPacket(std::string msg, std::string *pureMessage);

};

#endif
