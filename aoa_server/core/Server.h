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


class Server {
	private:
		static const int INPUT_ARGS_NUM;
		static const int PORT_NUM_LOWER_LIMIT;
		static const int PORT_NUM_UPPER_LIMIT;

		Application *app;
		ConnectionManager *conn;
		CommunicationManager *comm;
		StringBuilder *logMessage;

		int argc;
		char **argv;


		bool checkArgs();
		void init();

	public:
		Server(int argc, char **argv);

		/**
		*	Main function, starts the server.
		*/
		void run();

};

#endif



