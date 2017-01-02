#include <thread>
#include <stdlib.h>


#include "MessageProcessor.h"
#include "MessageType.h"
#include "Message.h"
#include "../partial/SafeQueue.h"
#include "../partial/tools.h"







MessageProcessor::MessageProcessor(SafeQueue<Message *> *messageQueue, SafeQueue<Message *> *sendMessageQueue) {
    this->messageQueue = messageQueue;
    this->sendMessageQueue = sendMessageQueue;
    this->sbMessage = new StringBuilder();
}

std::thread MessageProcessor::run(){
    return std::thread([=] { this->runProcessing(); });
}

void MessageProcessor::runProcessing(){

    bool stop = false;

    for(;;) {
        if(stop) break;

        std::cout << "MSGProcessor: waiting for a message." << std::endl;
        Message *msg = this->messageQueue->pop();
        std::cout << "MSGProcessor: got a message." << std::endl;

        if(!this->handleMessageType(msg))
            continue;

        this->perform(msg);
        std::cout << "MSGProcessor: a message handled." << std::endl;

//        (this->*this->processFunctions[msg->getType()])();
    }
}

bool MessageProcessor::handleMessageType(Message *msg) {
    MessageType msgType;

    std::string txt,
                msgTypeStr,
                msgBody;

    u_long delimPos;

    txt = msg->getMessage();
    delimPos = txt.find(Message::DELIMITER);
    msgTypeStr = txt.substr(0, delimPos);
    msgBody = txt.substr(delimPos + 1);

    if(this->checkHelloPacket(txt)) {
        msg->setType(convertInternalMessageType(0));
        return true;
    }

    if(!isNumber(msgTypeStr)) {
        return false;
    }

    msgType = convertInternalMessageType(atoi(msgTypeStr.c_str()));
    std::cout << "setting type: " << msgType << std::endl;
    msg->setType(msgType);
    msg->setMessage(msgBody);

    return true;
}

bool MessageProcessor::checkHelloPacket(std::string msg) {
    return msg.find(Message::HELLO_PACKET) == 0l && msg.length() == Message::HELLO_PACKET.length();
}


void MessageProcessor::perform(Message *msg){

    this->clientSocket = msg->getSock();

    switch(msg->getType()) {
        case HELLO: this->proceedHelloPacket(); break;
        case SIGN_IN: this->proceedSignIn(msg); break;
        case GAME_LIST: this->proceedGameList(msg); break;
        case GAME_NEW: this->proceedNewGame(msg); break;
        case GAME_JOIN: this->proceedJoinGame(msg); break;
        case GAME_START: this->proceedStartGame(msg); break;
        case TURN_DATA: this->proceedTurnData(msg); break;
        case GAME_LEAVE: this->proceedLeaveGame(msg); break;
        default: case SIGN_OUT: this->proceedSignOut(msg); break;
    }
}

void MessageProcessor::answerMessage(){
    Message *m = new Message(this->clientSocket, this->sbMessage->getString());
    this->sendMessageQueue->push(m);
    this->sbMessage->clear();
}

void MessageProcessor::proceedHelloPacket() {
    std::cout << "processing: hello" << std::endl;
    this->sbMessage->append(Message::HELLO_PACKET_RESPONSE);
    this->answerMessage();
}

void MessageProcessor::proceedSignIn(Message *msg) {
    std::cout << "processing: signin" << std::endl;
    sbMessage->append(msg->getType());
    sbMessage->append(Message::DELIMITER);
    sbMessage->append(Message::ACK);
    sbMessage->append(Message::DELIMITER);
    sbMessage->append("3");                    // UID
    this->answerMessage();
}

void MessageProcessor::proceedGameList(Message *msg) {
    std::cout << "processing: gamelist" << std::endl;
    // type ; r-id ; p-count ; p-limit ; diff ; dim ; nicks
    sbMessage->append("2;1;1;2;1;3;marty;2;2;3;2;5;dendasda:gabin");
    this->answerMessage();
}

void MessageProcessor::proceedNewGame(Message *msg) {
    std::cout << "processing: newgame" << std::endl;

    // type ; (N)ACK ; r-id ; p-count ; p-limit ; diff ; dim ; nicks
    sbMessage->append("3;1;1;2;3;1;5;marty:denda");
    this->answerMessage();
}

void MessageProcessor::proceedJoinGame(Message *msg) {
    std::cout << "processing: joingame" << std::endl;
    // msg-type;ack;r-id;p-count;p-limit;diff;dim;
    sbMessage->append("4;1;1;2;3;2;5;marty:denda");
    this->answerMessage();

    //tmp
    this->proceedStartGame(msg);
    this->proceedTurnData(msg);
}

void MessageProcessor::proceedStartGame(Message *msg) {
    std::cout << "processing: startgame" << std::endl;
    sbMessage->append("5;1");
    this->answerMessage();
}

void MessageProcessor::proceedTurnData(Message *msg) {
    std::cout << "processing: turndata" << std::endl;

    // msg-type;turn;move-pos;move-col;move-shape;move-pos;move-col;move-shape;...
    sbMessage->append("6;5;1;1;2;2;3;3;4;4;5;5;6;6;7;7;8");
    this->answerMessage();
}

void MessageProcessor::proceedLeaveGame(Message *msg) {
    std::cout << "processing: leavegame" << std::endl;
}

void MessageProcessor::proceedSignOut(Message *msg) {
    std::cout << "processing: signout" << std::endl;
}
