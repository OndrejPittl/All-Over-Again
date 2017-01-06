#include <iostream>
#include <sys/ioctl.h>


#include "Server.h"
#include "../core/Logger.h"
#include "../partial/tools.h"


const int Server::INPUT_ARGS_NUM = 2;

const int Server::PORT_NUM_LOWER_LIMIT = 1024;

const int Server::PORT_NUM_UPPER_LIMIT = 65535;



Server::Server(int argc, char **argv) {
	this->argc = argc;
	this->argv = argv;
//	this->messageQueue = new SafeQueue<Message *>();
}

void Server::run() {

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






    Logger::info("------ Starting server -------");

    // check input arguments
    if(!checkArgs()){
        // Logger::info("Input args are WRONG!");
        exit(Logger::printErr(ERR_INVALID_ARGS));
    } else {
        Logger::info("Input args are OK!");
    }


    this->init();

    // basic server setup (port & ip definition etc.)
    if(!(result = this->conn->prepare())){
        exit(result);
    }

    // run message validating thread
	this->comm->startMessageValidator();

	// run message processing thread
	this->comm->startMessageProcessor();

    // run message senging thread
    this->comm->startMessageSender();

    for (;;){

		// working copy of a set of sockets
		this->conn->restoreSocketSets();

		Logger::info("Server is waiting for a request...");

        // Server waits until being requested.
		if(!(result = this->conn->waitForRequests())){
			exit(result);
		}

		Logger::info("Server recognized a new request.");

		// check all file descriptors excluding stdin, stdout, stderr
		for(fdIndex = ConnectionManager::CLIENT_FD_OFFSET; fdIndex < FD_SETSIZE; fdIndex++ ){

			// Is a FD in a ReadSockSet?
			if(this->conn->isSockReadable(fdIndex)){

                // Is FD a SRV socket?
				if (this->conn->isServerSocket(fdIndex)){

					// SRV socket –> accept a new connection.
                    this->conn->registerNewClient();

				} else {

					// CLI socket –> accept data.

					// check number of bytes received
					ioctl(fdIndex, FIONREAD, &bytesReceived);

					if (bytesReceived > 0){
                        // Receive a message.
						this->comm->receiveMessage(fdIndex, bytesReceived);

					} else {
						// Disconnect a client.
                        this->conn->deregisterNewClient(fdIndex);
						this->app->deregisterUser(fdIndex);
					}

				}
			}

            // Is a FD in a WriteSockSet?
			 if(this->conn->isSockWritable(fdIndex)){
//				 vector<Message>::iterator msgIt;
//				 Message msg;
//				 for(msgIt = messageQueue.begin(); msgIt != messageQueue.end(); ++msgIt) {
//				 	if(msgIt.)
//				 }
			 }


		}
	}

	Logger::info("---------- Finished ----------");
}

bool Server::checkArgs() {
	bool correct = true;

	// number of args
	if(this->argc != INPUT_ARGS_NUM)
		return false;

	// port is a number in an allowed range
	if(!isNumberInRange(this->argv[1], PORT_NUM_LOWER_LIMIT, PORT_NUM_UPPER_LIMIT)) {
		correct = false;
	}

	return correct;
}


void Server::init() {
    this->conn = new ConnectionManager(this->argv[1]);
    this->comm = new CommunicationManager();
    this->app = new Application(this->conn, this->comm);
    this->comm->setApp(this->app);

    this->logMessage = new StringBuilder();
    // this->app = new Application();


}