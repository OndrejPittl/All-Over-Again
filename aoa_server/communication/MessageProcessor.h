#ifndef MESSAGE_PROCESSOR_H
#define MESSAGE_PROCESSOR_H


#include <thread>

#include "../partial/SafeQueue.h"
#include "../partial/StringBuilder.h"
#include "Message.h"

class MessageProcessor {

    private:

        int clientSocket;

        StringBuilder *sbMessage;

        SafeQueue<Message *> *messageQueue;
        SafeQueue<Message *> *sendMessageQueue;

        void perform(Message *msg);
        bool handleMessageType(Message *msg);
        void runProcessing();

        bool checkHelloPacket(std::string msg);
        void answerMessage();
        void proceedHelloPacket();
        void proceedSignIn(Message *msg);
        void proceedGameList(Message *msg);
        void proceedNewGame(Message *msg);
        void proceedJoinGame(Message *msg);
        void proceedStartGame(Message *msg);
        void proceedTurnData(Message *msg);
        void proceedLeaveGame(Message *msg);
        void proceedSignOut(Message *msg);



//        void (*processFunctions[7])() = {
//                proceedSignIn(),
//                proceedGameList,
//                proceedNewGame,
//                proceedJoinGame,
//                proceedTurnData,
//                proceedLeaveGame,
//                proceedSignOut
//        };

    public:
        MessageProcessor(SafeQueue<Message *> *messageQueue, SafeQueue<Message *> *sendMessageQueue);
        std::thread run();


};

typedef void (MessageProcessor::*processFunction) ();


#endif
