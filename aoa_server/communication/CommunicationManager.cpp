// libraries
#include <iostream>
#include <sys/socket.h>

// headers
#include "../core/Logger.h"
#include "CommunicationManager.h"
#include "../connection/ConnectionManager.h"
//#include "../partial/Semaphore.h"


CommunicationManager::CommunicationManager() {
    this->messageQueue = new SafeQueue<Message *>();
    this->readableMessages = new SafeQueue<Message *>();
	Logger::info("CommunicationManager is initialized.");
}


void CommunicationManager::startCommunication(){
    this->receiver = new Receiver(this->messageQueue, this->readableMessages);
    this->receiverThrd = this->receiver->run();
}



void CommunicationManager::receiveMessage(int sock, int byteCount) {

    Message *m = new Message();
    m->setSock(sock);
    m->setSize(byteCount);
    this->readableMessages->push(m);


    // read data
    // this->inputBuffer = recvMsg(sock, byteCount);

    // sendMsg(sock, this->inputBuffer);
    // sendMsg(sock, this->inputBuffer);
}

//void CommunicationManager::recvMsg(int sock, int byteCount, std::string *buff) {
//std::string CommunicationManager::recvMsg(int sock, int byteCount) {
//
//    // result of an operation
//    ssize_t result;
//
//    // message length in bytes
//    size_t msgLen = byteCount * sizeof(char);
//
//    // buffer
//    char buffer[msgLen];
//
//    // clear memory
//    memset(buffer, 0, msgLen + 1);
//
//    // receive data
//    result = recv(sock, buffer, msgLen, 0);
//    //result = read(sock, msgBuffer, BUFF_LEN);
//
//    std::cout << "---------------------" << std::endl;
//    std::cout << msgLen << " bytes received in a message: " << buffer;
//    std::cout << "---------------------" << std::endl;
//
//    // an error during receiving data
//    if(result < 0) {
//        Logger::printErr(ERR_MSG_RECEIVE);
//    }
//
//    return std::string(buffer);
//}


bool CommunicationManager::sendMsg(int sock, std::string txt) {
    // std::cout << "---> Sending " << txt << " to: " << sock << "." << std::endl;
	// return send(sock, txt.c_str(), txt.length(), MSG_NOSIGNAL) >= 0;
	return send(sock, txt.c_str(), txt.length(), 0) >= 0;
}

void CommunicationManager::sendMsg(fd_set *socks, std::string txt) {
	int fd;
//    std::cout << "---> Sending " << txt << " to all." << std::endl;

    for(fd = ConnectionManager::CLIENT_FD_OFFSET; fd < FD_SETSIZE; fd++) {
        	// std::cout << "Checking: " << fd << std::endl;
        if (FD_ISSET(fd, socks)) {
            sendMsg(fd, txt);
        }
    }
}

