#include <iostream>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/un.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/ioctl.h>
#include <stdio.h>
#include <ctype.h>
#include <string>
#include <time.h>
#include <vector>
#include <algorithm>

// #include "libs/easylogging++.h"

#include "App.h"
#include "../core/Logger.h"
#include "../connection/ConnectionManager.h"
#include "../communication/CommunicationManager.h"
#include "../partial/tools.h"
#include "../partial/message.h"
#include "../partial/StringBuilder.h"




const int App::INPUT_ARGS_NUM = 2;

const int App::PORT_NUM_LOWER_LIMIT = 1024;

const int App::PORT_NUM_UPPER_LIMIT = 65535;


App::App(int argc, char **argv) {
	this->argc = argc;
	this->argv = argv;

	this->cliSocket = 0;
}

void App::run() {

	/**
	*	Operation result / return value being used to check its successful.
	*/
	int result;

	/**
	*	A variable used to iterate through descriptors;
	*/
	int fdIndex;

	/**
	*	Number of bytes received.
	*/
	int bytesReceived;

	int srvSocket;

	/**
	*	Set of tracked sockets.
	*/
    fd_set cliSet;

	/**
	*	"Working" copy of a set of sockets. Collection that is being modified by select();
	*/
	fd_set readSockSet;

	/**
	*	"Working" copy of a set of sockets. Collection that is being modified by select();
	*/
	fd_set writeSockSet;

	/**
	*	IP adresa & port of a new client.
	*/
//	struct sockaddr_in cliAddr;

	/**
	*	Length of client address.
	*/
//	int cliAddrLen;




    Logger::info("------ Starting server -------");

    // check input arguments
    if(!checkArgs()){
        // Logger::info("Input args are WRONG!");
        exit(Logger::printErr(ERR_INVALID_ARGS));
    } else {
        Logger::info("Input args are OK!");
    }


    this->logMessage = new StringBuilder();

    this->conn = new ConnectionManager(this->argv[1]);

    this->comm = new CommunicationManager();



    result = this->conn->startListening();

    if(!result){
        exit(result);
    }


    srvSocket = this->conn->getServerSocket();


//	// clear a set of sockets
//	FD_ZERO(&cliSet);
//
//	// place a server socket into a set being checked with select()
//	FD_SET(srvSocket, &cliSet);

    this->conn->prepareClientSocketSet();

    for (;;){

		// working copy of a set of sockets
        cliSet = this->conn->getClientSocketSet();
		readSockSet = cliSet;
		writeSockSet = cliSet;

		Logger::info("Server is waiting for a request...");

		// After every select() call is a set of descriptors overridden.
		// waiting/checking until some readable data appear on a socket of a set
		//result = select(FD_SETSIZE, readSockSet, writeSockSet, (fd_set *)0, (struct timeval *)0 );
		result = select(FD_SETSIZE, &readSockSet, (fd_set *)0, (fd_set *)0, (struct timeval *)0 );

		Logger::info("Server recognized a new request.");

		if (result < 0) {
			exit(Logger::printErr(ERR_SELECT));
		}

		// check all file descriptors excluding stdin, stdout, stderr
		for(fdIndex = ConnectionManager::CLIENT_FD_OFFSET; fdIndex < FD_SETSIZE; fdIndex++ ){

			// is a file descriptor in a readSocks set?
			if(FD_ISSET(fdIndex, &readSockSet)){

				if (this->conn->isServerSocket(fdIndex)){
					// server socket –> accept a new connetion
                    // FD_SET(this->cliSocket, &cliSet);
                    this->conn->registerNewClient();

				} else {

					// client socket –> accept data
					// check number of bytes received
					ioctl(fdIndex, FIONREAD, &bytesReceived);

					if (bytesReceived > 0){
						this->comm->setSocketSets(&cliSet, &writeSockSet, &readSockSet);
						this->comm->receiveMessage(fdIndex, bytesReceived);
					} else {
						// disconnection of a client

						//FD_CLR(fdIndex, &cliSet);
						//Logger::info("A client was disconnected and removed from the set.");
                        this->conn->deregisterNewClient(fdIndex);
					}
				}
			}

			// is a file descriptor in a writeSocks set?
			// if(FD_ISSET(fdIndex, writeSockSet)){
				// vector<Message>::iterator msgIt;
				// Message msg;
				// for(msgIt = messageQueue.begin(); msgIt != messageQueue.end(); ++msgIt) {
				// 	if(msgIt.)
				// }
			// }


		}
	}

	Logger::info("---------- Finished ----------");
}

bool App::checkArgs() {
	bool correct = true;

	// port given
	int port;

	// number of args
	if(this->argc != INPUT_ARGS_NUM)
		return false;

	// port is a number in an allowed range
	if(!isNumberInRange(this->argv[1], PORT_NUM_LOWER_LIMIT, PORT_NUM_UPPER_LIMIT)) {
		correct = false;
	}

	return correct;
}
