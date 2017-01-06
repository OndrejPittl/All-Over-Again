#include <thread>
#include <stdlib.h>


#include "MessageProcessor.h"
#include "../partial/tools.h"
#include "../core/Logger.h"
#include "../partial/StringBuilder.h"
#include "../core/Application.h"

MessageProcessor::MessageProcessor(SafeQueue<Message *> *messageQueue, SafeQueue<Message *> *sendMessageQueue) {
    this->messageQueue = messageQueue;
    this->sendMessageQueue = sendMessageQueue;
    this->sbMsg = new StringBuilder();
    this->log = new StringBuilder();
}

std::thread MessageProcessor::run(){
    return std::thread([=] { this->runProcessing(); });
}

void MessageProcessor::runProcessing(){

    bool stop = false;

    for(;;) {
        if(stop) break;

        Message *msg = this->messageQueue->pop();
        this->log->clear();
        this->log->append("MSGProcessor: got a message.");
        Logger::info(this->log->getString());

        if(!this->handleMessageType(msg))
            continue;

        this->perform(msg);
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
    Message *m = new Message(this->clientSocket, this->sbMsg->getString());
    this->sendMessageQueue->push(m);
    this->sbMsg->clear();
}

void MessageProcessor::proceedHelloPacket() {
    this->log->clear();
    this->log->append("MSGProcessor, processing: hello.");
    Logger::info(this->log->getString());

    this->sbMsg->append(Message::HELLO_PACKET_RESPONSE);
    this->answerMessage();
}

/**
 * Task: check username availability. Store
 * @param msg
 */
void MessageProcessor::proceedSignIn(Message *msg) {

    int uID = msg->getSock();
    std::string username = msg->getMessage();

    this->log->clear();
    this->log->append("MSGProcessor, processing signin: ");
    this->log->append(username);
    Logger::info(this->log->getString());

    sbMsg->append(msg->getType());
    sbMsg->append(Message::DELIMITER);

    if(this->app->registerUser(uID, username)) {
        // accepted
        sbMsg->append(Message::ACK);
        sbMsg->append(Message::DELIMITER);
        sbMsg->append(uID); // UID
    } else {
        // rejected
        sbMsg->append(Message::NACK);
    }

    this->answerMessage();
}

void MessageProcessor::proceedGameList(Message *msg) {
    this->log->clear();
    this->log->append("MSGProcessor, processing gamelist:");
    Logger::info(this->log->getString());

    // type ; r-id ; p-count ; p-limit ; diff ; dim ; nicks
    sbMsg->append("2;1;1;2;1;3;marty;2;2;3;2;5;dendasda:gabin");
    this->answerMessage();
}

void MessageProcessor::proceedNewGame(Message *msg) {
    this->log->clear();
    this->log->append("MSGProcessor, processing newgame:");
    Logger::info(this->log->getString());

    // type ; (N)ACK ; r-id ; p-count ; p-limit ; diff ; dim ; nicks
    sbMsg->append("3;1;1;2;3;1;5;marty:denda");
    this->answerMessage();
}

void MessageProcessor::proceedJoinGame(Message *msg) {
    this->log->clear();
    this->log->append("MSGProcessor, processing joingame:");
    Logger::info(this->log->getString());

    // msg-type;ack;r-id;p-count;p-limit;diff;dim;
    sbMsg->append("4;1;1;2;3;2;5;marty:denda");
    this->answerMessage();

    //tmp
    this->proceedStartGame(msg);
    this->proceedTurnData(msg);
}

void MessageProcessor::proceedStartGame(Message *msg) {
    this->log->clear();
    this->log->append("MSGProcessor, processing startgame:");
    Logger::info(this->log->getString());

    sbMsg->append("5;1");
    this->answerMessage();
}

void MessageProcessor::proceedTurnData(Message *msg) {
    this->log->clear();
    this->log->append("MSGProcessor, processing turndata:");
    Logger::info(this->log->getString());


    // 4 s / tah?
    // msg-type;turn;time;move-pos;move-col;move-shape;move-pos;move-col;move-shape;...
    sbMsg->append("6;5;20;0;1;1;2;2;3;3;4;1;3;5;0;0;7;2;2");
    this->answerMessage();
}

void MessageProcessor::proceedLeaveGame(Message *msg) {
    this->log->clear();
    this->log->append("MSGProcessor, processing leavegame:");
    Logger::info(this->log->getString());

}

void MessageProcessor::proceedSignOut(Message *msg) {
    this->log->clear();
    this->log->append("MSGProcessor, processing signout:");
    Logger::info(this->log->getString());

}

void MessageProcessor::setApp(Application *app) {
    this->app = app;
}
