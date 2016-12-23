// libraries
#include <iostream>
#include <sys/socket.h>

// headers
#include "../core/Logger.h"
#include "CommunicationManager.h"
#include "../connection/ConnectionManager.h"


CommunicationManager::CommunicationManager(SafeQueue<Message *> *messageQueue) {
    this->messageQueue = messageQueue;
    this->readableMessages = new SafeQueue<RawMessage *>();
	Logger::info("CommunicationManager is initialized.");
}

void CommunicationManager::startMessageProcessor(){
    this->msgProcessor = new MessageProcessor(this->messageQueue, this->readableMessages);
    this->msgProcessorThrd = this->msgProcessor->run();
}

void CommunicationManager::receiveMessage(int sock, int byteCount) {

    std::string strMsg = this->recvMsg(sock, byteCount);

    RawMessage *msg = new RawMessage(sock, byteCount, strMsg);
    this->readableMessages->push(msg);
}

//void CommunicationManager::recvMsg(int sock, int byteCount, std::string *buff) {
std::string CommunicationManager::recvMsg(int sock, int byteCount) {

    // result of an operation
    ssize_t result;

    // message length in bytes
    size_t msgLen = byteCount * sizeof(char);

    // buffer
    char buffer[msgLen];

    // clear memory
    memset(buffer, 0, msgLen + 1);

    // receive data
    result = recv(sock, buffer, msgLen, 0);
    //result = read(sock, msgBuffer, BUFF_LEN);

    std::cout << "---------------------" << std::endl << msgLen << " bytes received from: " << sock << " in a message: " << buffer << "---------------------" << std::endl;

    // an error during receiving data
    if(result < 0) {
        Logger::printErr(ERR_MSG_RECEIVE);
    }

    return std::string(buffer);
}


bool CommunicationManager::sendMsg(int sock, std::string txt) {
    // std::cout << "---> Sending " << txt << " to: " << sock << "." << std::endl;
	// return send(sock, txt.c_str(), txt.length(), MSG_NOSIGNAL) >= 0;
	return send(sock, txt.c_str(), txt.length(), 0) >= 0;
}

void CommunicationManager::sendMsg(fd_set *socks, std::string txt) {
	int fd;

    // std::cout << "---> Sending " << txt << " to all." << std::endl;
    for(fd = ConnectionManager::CLIENT_FD_OFFSET; fd < FD_SETSIZE; fd++) {
        if (FD_ISSET(fd, socks)) {
            sendMsg(fd, txt);
        }
    }
}

