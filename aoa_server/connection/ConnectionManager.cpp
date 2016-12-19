// libraries
#include <string>
#include <iostream>
#include <sstream>

// headers
#include "../core/Logger.h"
#include "ConnectionManager.h"

// namespace



const int PORT_NUM = 22222;

const int ConnectionManager::CLIENT_FD_OFFSET = 4;


ConnectionManager::ConnectionManager(int portNumber) {
	// store number of a port
	this->portNum = portNumber;
	init();
}

void ConnectionManager::init(){
	// clearing a memory
	memset(&this->srvAddr, 0, sizeof(struct sockaddr_in));

	// IPv4
	this->srvAddr.sin_family = AF_INET;

	// port number definition
	this->srvAddr.sin_port = htons(this->portNum);

	// all IPs of any network card
	this->srvAddr.sin_addr.s_addr = INADDR_ANY;

	Logger::info("ConnectionManager is initialized.");
}

int ConnectionManager::startListening(){
	std::ostringstream log;
	
	// socket receiving connections, IPv4 & TCP
	this->srvSocket = socket(AF_INET, SOCK_STREAM, 0);

	// binding a socket with a port, using global namespace
	this->result = ::bind(this->srvSocket, (struct sockaddr *) &this->srvAddr, sizeof(struct sockaddr_in));

	if (this->result != 0) {
		return Logger::printErr(ERR_BIND);
	}

	// listen for new connecitons
	this->result = listen(this->srvSocket, 5);

	if (this->result != 0){
		return Logger::printErr(ERR_LISTEN);
	}

	log << "Server is listening on port " << getPortNumber() << ".";
	Logger::info(log.str());

	return true;
}

int ConnectionManager::getPortNumber(){
	return this->portNum;
}

int ConnectionManager::getServerSocket(){
	return this->srvSocket;
}
