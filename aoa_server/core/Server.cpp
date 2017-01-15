#include <iostream>
#include <sys/ioctl.h>
#include <zconf.h>


#include "Server.h"
#include "../core/Logger.h"
#include "../partial/tools.h"




Server::Server(int argc, char **argv) {
	this->opts = new ServerOptions(argc, argv);
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

    // accepting input args
    this->handleInputArguments();

    // initialization
    this->init();

    // basic server setup
    this->conn->prepare();

    // Runs separate threads.
    this->runServices();

    for (;;){

		// working copy of a set of sockets
		this->conn->restoreSocketSets();

		Logger::info("Server is waiting for a request...");

        // Server waits until being requested. (select())
        this->conn->waitForRequests();

		Logger::info("Server recognized a new request.");


		// check all file descriptors excluding stdin, stdout, stderr
		for(fdIndex = ConnectionManager::CLIENT_FD_OFFSET; fdIndex < FD_SETSIZE; fdIndex++ ){
			if(this->conn->isSockReadable(fdIndex)){

                // Is SRV?
				if (this->conn->isServerSocket(fdIndex)){

                    // SRV –> accept a new connection.
                    int cliSock = this->conn->acceptConnection();

                    // register a new player bound to a socket
                    this->app->registerUser(cliSock);

				} else {

					// CLI –> accept data.
					// check number of bytes received
					ioctl(fdIndex, FIONREAD, &bytesReceived);

					if (bytesReceived > 0){

                        // Receive a message.
						this->comm->receiveMessage(fdIndex, bytesReceived);

					} else {

						// Disconnect a client.
                        this->app->deregisterUser(fdIndex);
					}

				}
			}
		}
	}

	Logger::info("---------- Finished ----------");
}

void Server::init() {
    this->conn = new ConnectionManager(this->portNumber);
    this->comm = new CommunicationManager();
    this->app = new Application(this->conn, this->comm);
    this->comm->setApp(this->app);
    this->log = new StringBuilder();
}

void Server::handleInputArguments() {
    if(this->opts->has(ServerOptions::OPT_HELP)) {
        this->opts->printHelp();
        exit(0);
    }

    if(this->opts->has(ServerOptions::OPT_PORT)) {
        this->portNumber = this->opts->get(ServerOptions::OPT_PORT);
    } else {
        this->portNumber = ConnectionManager::DEFAULT_PORT;
    }

    if(this->opts->has(ServerOptions::OPT_QUIET)) {
        Logger::disableLogging(true);
        Logger::info("Running in quiet mode.");
    }
}

void Server::runServices() {
    // run message validating thread
    this->comm->startMessageValidator();

    // run message processing thread
    this->comm->startMessageProcessor();

    // run message senging thread
    this->comm->startMessageSender();
}
