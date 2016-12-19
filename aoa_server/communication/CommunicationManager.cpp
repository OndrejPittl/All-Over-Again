// libraries
#include <iostream>
#include <string>
#include <sys/socket.h>

// headers
#include "../core/Logger.h"
#include "CommunicationManager.h"
#include "../connection/ConnectionManager.h"
#include "../partial/tools.h"


const int CommunicationManager::BUFF_LEN = 2048;

const std::string CommunicationManager::BROADCAST_FLAG = "#";



CommunicationManager::CommunicationManager() {
	Logger::info("CommunicationManager is initialized.");
}

void CommunicationManager::initBuffer() {

}

void CommunicationManager::receiveMessage(int fdIndex, int byteCount) {
    bool broadDetected;

    // read data
    this->inputBuffer = recvMsg(fdIndex, byteCount);



	// checks whether it is determined to be sent to all clients in a set or not
	broadDetected = checkBroadcast(&this->inputBuffer);

	// transforms a message
	transformMsg(&this->inputBuffer);

	if(broadDetected) {
		// sends to all clients
        sendMsg(this->cliSockSet, this->inputBuffer);
	} else {
		// send an automatic answer/response to a sender
		//answerClient(fdIndex, this->inputBuffer);
		sendMsg(fdIndex, this->inputBuffer + "\n");
	}
}


//void CommunicationManager::recvMsg(int sock, int byteCount, std::string *buff) {
std::string CommunicationManager::recvMsg(int sock, int byteCount) {
	// result of an operation
    ssize_t result;

    size_t msgLen = byteCount * sizeof(char);

	// buffer
    // char msgBuffer[BUFF_LEN];
    char msgBuffer[msgLen];

	// clear memory
	memset(msgBuffer, 0, msgLen + 1);

	// receive data
	result = recv(sock, msgBuffer, msgLen, 0);
	//result = read(sock, msgBuffer, BUFF_LEN);

//    std::cout << "---------------------" << std::endl;
//    std::cout << msgLen << " bytes received in a message: " << msgBuffer;
//    std::cout << "---------------------" << std::endl;


	// an error during receiving data
	if(result < 0) {
		Logger::printErr(ERR_MSG_RECEIVE);
	}

//	(*buff) = msgBuffer;
//	printf("Received message: >>%s<<\n", (*buff).c_str());

    return std::string(msgBuffer);
}

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

bool CommunicationManager::checkBroadcast(std::string *msg) {
	bool detected = (*msg).find(BROADCAST_FLAG) != std::string::npos;

	if(detected) {
		println("Broadcast detected.");
		(*msg).erase((*msg).begin()); 
	}

	return detected;
}

void CommunicationManager::answerClient(int sock, std::string message) {
	println("Sending an automatic response.");

	std::string response = "";
	response.append(message);
	response.append(" (");
	response.append(getTimestamp());
	response.append(")");
	
	sendMsg(sock, response);
}

void CommunicationManager::transformMsg(std::string *msg) {
	std::reverse((*msg).begin(), (*msg).end());
}

std::string CommunicationManager::getTimestamp() {
	time_t rawtime;
	time(&rawtime);
	return asctime(localtime (&rawtime));
}

void CommunicationManager::setWriteSocketSet(fd_set *writeSockSet) {
	this->writeSockSet = writeSockSet;
}
		
void CommunicationManager::setReadSocketSet(fd_set *readSockSet) {
	this->readSockSet = readSockSet;
}

void CommunicationManager::setSocketSets(fd_set *cliSockSet, fd_set *writeSockSet, fd_set *readSockSet) {
	this->cliSockSet = cliSockSet;
	this->writeSockSet = writeSockSet;
	this->readSockSet = readSockSet;
}

