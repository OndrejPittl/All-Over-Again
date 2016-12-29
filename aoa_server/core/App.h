#ifndef APP_H
#define APP_H

#include <cstring>
#include <thread>

#include "../partial/StringBuilder.h"
#include "../connection/ConnectionManager.h"
#include "../communication/CommunicationManager.h"
#include "../communication/MessageValidator.h"
#include "../communication/Message.h"
#include "../partial/SafeQueue.h"

class App {
	private:
		static const int INPUT_ARGS_NUM;
		static const int PORT_NUM_LOWER_LIMIT;
		static const int PORT_NUM_UPPER_LIMIT;


		ConnectionManager *conn;
		CommunicationManager *comm;

		SafeQueue<Message *> *messageQueue;

		StringBuilder *logMessage;

		int argc;
		char **argv;


		bool checkArgs();
		void init();

	public:
		App(int argc, char **argv);

		/**
		*	Main function, starts the server.
		*/
		void run();

};

#endif



