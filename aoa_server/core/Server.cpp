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



    this->log = new StringBuilder();



    Logger::info("----------------------------------");
    Logger::info("----    running AOA server    ----");
    Logger::info("----------------------------------");


    // accepting input args
    this->handleInputArguments();

    // initialization
    this->init();

    // basic server setup
    this->conn->prepare();

    // Runs separate threads.
    this->runServices();

    for (;;){

        this->app->handleSuspiciousClients();

		// working copy of a set of sockets
		this->conn->restoreSocketSets();

		Logger::info("Server is waiting for a request...");

        // Server waits until being requested. (select())
        result = this->conn->waitForRequests();

        if(result < 0) {
            // Logger::error(std::to_string(errno));
            // EINTR: sys call, a socket of online user removed from a set
            // EBADF: Bad File Descriptor, a FD removed from a set due to amount of incorrect messages
            if(errno == EINTR || errno == EBADF) continue;
            exit(Logger::printErr(ERR_SELECT));
        }

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
}

void Server::init() {
    this->conn = new ConnectionManager(this->portNumber);
    this->comm = new CommunicationManager();
    this->app = new Application(this->conn, this->comm);
    this->comm->setApp(this->app);
}

void Server::handleInputArguments() {
    if(this->opts->has(ServerOptions::OPT_HELP)) {
        this->opts->printHelp();
        exit(0);
    }

    if(this->opts->has(ServerOptions::OPT_PORT)) {
        this->portNumber = this->opts->get(ServerOptions::OPT_PORT);

        // -- log --
        this->log->clear(); this->log->append("port:   ");
        this->log->append(this->portNumber); this->log->append(" (accepted)");
        Logger::info(this->log->getString());

    } else {
        this->portNumber = ConnectionManager::DEFAULT_PORT;

        // -- log --
        this->log->clear(); this->log->append("port:   ");
        this->log->append(this->portNumber); this->log->append(" (default)");
        Logger::info(this->log->getString());
    }

    if(this->opts->has(ServerOptions::OPT_QUIET)) {

        // -- log --
        Logger::info("quiet:  active");
        Logger::info("----------------------------------");
        Logger::disableLogging(true);
    } else {

        // -- log --
        Logger::info("quiet:  not active (default)");
        Logger::info("----------------------------------");
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
