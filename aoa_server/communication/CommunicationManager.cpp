#include <iostream>
#include <sys/socket.h>
#include <cstring>


#include "../core/Logger.h"
#include "CommunicationManager.h"



CommunicationManager::CommunicationManager() {
    this->init();
}

void CommunicationManager::init() {
    this->messageQueue = new SafeQueue<Message *>();
    this->sendMessageQueue = new SafeQueue<Message *>();
    this->rawMessageQueue = new SafeQueue<RawMessage *>();
    this->log = new StringBuilder();
    Logger::debug("CommunicationManager is initialized.");
}

void CommunicationManager::startMessageValidator(){
    this->msgValidator = new MessageValidator(this->messageQueue, this->rawMessageQueue, this->app);
    this->msgValidatorThrd = this->msgValidator->run();
}

void CommunicationManager::startMessageProcessor(){
    this->msgProcessor = new MessageProcessor(this->messageQueue, this->sendMessageQueue);
    this->msgProcessor->setApp(this->app);
    this->msgProcessorThrd = this->msgProcessor->run();
}

void CommunicationManager::startMessageSender(){
    this->msgSender = new MessageSender(this->sendMessageQueue);
    this->msgSenderThrd = this->msgSender->run();
}

void CommunicationManager::receiveMessage(int sock, int byteCount) {
    std::string strMsg = this->recvMsg(sock, byteCount);

    RawMessage *msg = new RawMessage(sock, byteCount, strMsg);
    this->rawMessageQueue->push(msg);
}

//void CommunicationManager::recvMsg(int sock, int byteCount, std::string *buff) {
std::string CommunicationManager::recvMsg(int sock, int byteCount) {

    // result of an operation
    ssize_t result;

    // message length in bytes
    size_t msgLen = byteCount * sizeof(char);

    // buffer
    char buffer[msgLen];

    // clearMsg memory
    memset(buffer, 0, msgLen + 1);

    // receive data
    result = recv(sock, buffer, msgLen, 0);
    //result = read(sock, msgBuffer, BUFF_LEN);

    if(byteCount > 2048)
        return std::string();


    this->log->clear(); this->log->append("<<<<<<< "); this->log->append(msgLen);
    this->log->append(" B received from: "); this->log->append(sock);
    this->log->append(" in a message:\n                     > "); this->log->append(buffer);
    Logger::info(this->log->getString());
    //Logger::error(this->log->getString());


    // an error during receiving data
    if(result < 0) {
        Logger::printErr(ERR_MSG_RECEIVE);
    }

    return std::string(buffer);
}

void CommunicationManager::setApp(Application *app) {
    this->app = app;
}

MessageProcessor *CommunicationManager::getMsgProcessor() const {
    return this->msgProcessor;
}
