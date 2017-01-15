// headers

#include "core/Server.h"
#include "partial/ServerOptions.h"
#include "core/Logger.h"
#include "libs/easylogging++.h"




INITIALIZE_EASYLOGGINGPP;

/**
*	Main function of this app.
*/
int main(int argc, char **argv) {
	Logger::init();

	Server *server = new Server(argc, argv);
	server->run();

	return 0;
}
