// libraries
#include <string>
#include <iostream>
#include <sstream>
#include <zconf.h>
#include <stdio.h>
#include <cstring>
#include <netinet/tcp.h>

// headers
#include "../core/Logger.h"
#include "ConnectionManager.h"
#include "../partial/StringBuilder.h"


const uint ConnectionManager::DEFAULT_PORT = 23456;

const int ConnectionManager::CLIENT_FD_OFFSET = 4;

const int ConnectionManager::COMM_INVALID_MSG_LIMIT = 5;



ConnectionManager::ConnectionManager(uint portNumber) {
    this->portNum = portNumber;
    this->init();
}

void ConnectionManager::init(){
    this->log = new StringBuilder();

	// clearing a memory
	memset(&this->srvAddr, 0, sizeof(struct sockaddr_in));

	// IPv4
	this->srvAddr.sin_family = AF_INET;

	// port number definition
	this->srvAddr.sin_port = htons(this->portNum);

	// all IPs of any network card
	this->srvAddr.sin_addr.s_addr = INADDR_ANY;

	Logger::info("ConnectionManager is initialized.", false);
}

void ConnectionManager::prepare(){

    int optval = 1; // == enabled

	// socket receiving connections, IPv4 & TCP
	this->srvSocket = socket(AF_INET, SOCK_STREAM, 0);

    if (setsockopt(this->srvSocket, SOL_SOCKET, SO_REUSEADDR, &optval, sizeof(int)) < 0) {
        exit(Logger::printErr(ERR_SETSOCKOPT));
    }

	// binding a socket with a port, using global namespace
	this->result = ::bind(this->srvSocket, (struct sockaddr *) &this->srvAddr, sizeof(struct sockaddr_in));

	if (this->result != 0) {
		exit(Logger::printErr(ERR_BIND));
	}

	// listen for new connecitons
	this->result = listen(this->srvSocket, 5);

	if (this->result != 0){
		exit(Logger::printErr(ERR_LISTEN));
	}

    this->log->clear();
    this->log->append("-----------------------------------\n");
    this->log->append("                     Server is listening on port ");
    this->log->append(getPortNumber());
    this->log->append(".\n                     -----------------------------------");
    Logger::info(this->log->getString());

    // clearMsg a set of sockets
    FD_ZERO(&this->cliSockSet);

    // place a server socket into a set being checked with select()
    FD_SET(this->srvSocket, &this->cliSockSet);
}

uint ConnectionManager::getPortNumber(){
	return this->portNum;
}

int ConnectionManager::getServerSocket(){
    return this->srvSocket;
}

bool ConnectionManager::isServerSocket(int sock){
    return this->srvSocket == sock;
}


void ConnectionManager::prepareClientSocketSet(){

}

void ConnectionManager::restoreSocketSets(){
    FD_ZERO(&this->readSockSet);
    this->readSockSet = this->cliSockSet;
}

int ConnectionManager::waitForRequests(){
    int result;

    // After every select() call is a set of descriptors overridden.
    // waiting/checking until some readable data appear on a socket of a set
    //result = select(FD_SETSIZE, readSockSet, writeSockSet, (fd_set *)0, (struct timeval *)0 );
    result = select(FD_SETSIZE, &this->readSockSet, (fd_set *)0, (fd_set *)0, (struct timeval *)0 );

//    if (result < 0) {
//        exit(Logger::printErr(ERR_SELECT));
//    }

    return result;
}

int ConnectionManager::isSockReadable(int sock){
    return FD_ISSET(sock, &this->readSockSet);
}

fd_set ConnectionManager::getClientSocketSet(){
    return this->cliSockSet;
}

fd_set ConnectionManager::getReadSocketSet(){
    return this->readSockSet;
}


//void ConnectionManager::registerNewClient(int cliSock){
//    FD_SET(cliSock, &this->cliSockSet);
//
//    this->log->clear();
//    this->log->append("New client connected to socket: ");
//    this->log->append(cliSock);
//    this->log->append(".");
//    Logger::info(this->log->getString());
//}

void ConnectionManager::deregisterClient(int sock){
    close(sock);
    FD_CLR(sock, &this->cliSockSet);

    this->log->clear();
    this->log->append("A client ");
    this->log->append(sock);
    this->log->append(" was disconnected and removed from the set.");
    Logger::info(this->log->getString());
}

//void ConnectionManager::registerClient(int sock) {
//    FD_SET(sock, &this->cliSockSet);
//}

int ConnectionManager::acceptConnection() {

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


    int optVal = 1,
        optLen = sizeof(optVal);


    if(setsockopt(cliSock, SOL_SOCKET, SO_KEEPALIVE, &optVal, optLen) < 0) {
        close(cliSock);
        exit(Logger::printErr(ERR_SETSOCKOPT));
    }


    // TCP KEEP ALIVE: probes
    optVal = 2;

    if(setsockopt(cliSock, IPPROTO_TCP, TCP_KEEPCNT, &optVal, optLen) < 0) {
        close(cliSock);
        exit(Logger::printErr(ERR_SETSOCKOPT));
    }


    // TCP KEEP ALIVE: time
//    optVal = 3;
//
//    if(setsockopt(cliSock, IPPROTO_TCP, TCP_KEEPIDLE, &optVal, optLen) < 0) {
//        close(cliSock);
//        exit(Logger::printErr(ERR_SETSOCKOPT));
//    }



    // TCP KEEP ALIVE: intvl
    optVal = 2;

    if(setsockopt(cliSock, IPPROTO_TCP, TCP_KEEPINTVL, &optVal, optLen) < 0) {
        close(cliSock);
        exit(Logger::printErr(ERR_SETSOCKOPT));
    }


    FD_SET(cliSock, &this->cliSockSet);

    this->log->clear();
    this->log->append("New client connected to socket: ");
    this->log->append(cliSock);
    this->log->append(".");
    Logger::info(this->log->getString());


    return cliSock;
}


