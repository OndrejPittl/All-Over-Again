// headers
#include "libs/easylogging++.h"
#include "core/Server.h"
#include "core/Logger.h"


/**
*	Logger initialization.
*/
INITIALIZE_EASYLOGGINGPP;


/**
*	Main function of this app.
*/
int main(int argc, char **argv) {

	Logger::init(argc, argv);

	Server *server = new Server(argc, argv);
	server->run();

	return 0;
}
