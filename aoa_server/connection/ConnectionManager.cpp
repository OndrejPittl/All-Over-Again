// libraries
#include <string>
#include <iostream>
#include <sstream>
#include <zconf.h>

// headers
#include "../core/Logger.h"
#include "ConnectionManager.h"
#include "../partial/StringBuilder.h"




const int PORT_NUM = 22222;

const int ConnectionManager::CLIENT_FD_OFFSET = 4;



ConnectionManager::ConnectionManager(char *portNumber) {
    this->portNum = atoi(portNumber);
    this->init();
}

void ConnectionManager::init(){
    this->sb = new StringBuilder();

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

bool ConnectionManager::isServerSocket(int sock){
    return this->srvSocket == sock;
}


void ConnectionManager::prepareClientSocketSet(){
    // clear a set of sockets
    FD_ZERO(&this->cliSockSet);

    std::cout << "Hledam, kde je chyba." << std::endl;

    // place a server socket into a set being checked with select()
    FD_SET(this->srvSocket, &this->cliSockSet);
}

fd_set ConnectionManager::getClientSocketSet(){
    return this->cliSockSet;
}

void ConnectionManager::registerNewClient(){
	/**
	*	IP adresa & port of a new client.
	*/
	struct sockaddr_in cliAddr;

	/**
    *	Length of client address.
    */
	int cliAddrLen;

	/**
	 * A new client socket.
	 */
	int cliSock = accept(this->srvSocket, (struct sockaddr *) &cliAddr, (socklen_t *) &cliAddrLen);

    FD_SET(cliSock, &this->cliSockSet);

    this->sb->clear();
    this->sb->append("New client connected to socket: ");
    this->sb->append(cliSock);
    this->sb->append(".");
    Logger::info(this->sb->getString());
}

void ConnectionManager::deregisterNewClient(int sock){
    close(sock);
    FD_CLR(sock, &this->cliSockSet);
    Logger::info("A client was disconnected and removed from the set.");
}


