#ifndef MESSAGE_PROCESSOR_H
#define MESSAGE_PROCESSOR_H


#include <thread>

#include "../partial/SafeQueue.h"
#include "../partial/StringBuilder.h"
#include "Message.h"
#include "../core/Application.h"
#include "MessageSerializer.h"
#include "MessageParser.h"


class MessageProcessor {

    private:

        int clientSocket;

        StringBuilder *sbMsg;
        StringBuilder *log;

        Application *app;
        MessageSerializer *serializer;
        MessageParser *parser;

        SafeQueue<Message *> *messageQueue;
        SafeQueue<Message *> *sendMessageQueue;

        void perform(Message *msg);
        bool handleMessageType(Message *msg);
        void runProcessing();

        bool checkHelloPacket(std::string msg);
        void answerMessage();
        void proceedHelloPacket();
        void proceedSignIn(Message *msg);
        void proceedGameList();
        void proceedNewGame(Message *msg);
        void proceedJoinGame(Message *msg);
        void proceedRestartGame();
        void proceedStartGameBase();
        void proceedFirstTurnData();
        void proceedTurnDataBase(bool ack);
        void proceedTurnData(Message *msg);
        void proceedEndGame(Room *room);
        void proceedLeaveGame();
        void proceedSignOut(Message *msg);

//        void (*processFunctions[7])() = {
//                proceedSignIn(),
//                proceedGameList,
//                proceedNewGame,
//                proceedJoinGame,
//                proceedFirstTurnData,
//                proceedLeaveGame,
//                proceedSignOut
//        };

    public:
        MessageProcessor(SafeQueue<Message *> *messageQueue, SafeQueue<Message *> *sendMessageQueue);
        std::thread run();
        void setApp(Application *app);
        void init();
        void proceedStartGame(Room *r, bool ack);
        void answerRoomAndClean(const Room *r, void (MessageProcessor::*callback)());

    void clearMsg();

    void answerMessageAndClean();


};

typedef void (MessageProcessor::*processFunction) ();


#endif
