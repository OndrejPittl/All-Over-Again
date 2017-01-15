#ifndef SERVER_H
#define SERVER_H

#include <string>
#include <thread>


#include "../partial/StringBuilder.h"
#include "../connection/ConnectionManager.h"
#include "../communication/CommunicationManager.h"
#include "../communication/MessageValidator.h"
#include "../communication/Message.h"
#include "../partial/SafeQueue.h"
#include "Application.h"
#include "../partial/ServerOptions.h"


class Server {
	private:

		Application *app;
		ConnectionManager *conn;
		CommunicationManager *comm;
        ServerOptions *opts;
		StringBuilder *log;
        uint portNumber;
		void init();

	public:
		Server(int argc, char **argv);

		/**
		*	Main function, starts the server.
		*/
		void run();

        void handleInputArguments();

    void runServices();
};

#endif



