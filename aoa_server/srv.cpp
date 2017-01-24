// headers

#include "partial/tools.h"
#include "core/Server.h"
#include "core/Logger.h"
#include "libs/easylogging++.h"




INITIALIZE_EASYLOGGINGPP;

/**
*	Main function of this app.
*/
int main(int argc, char **argv) {

	Tools::init();
	Logger::init();

	Server *server = new Server(argc, argv);
	server->run();

	return 0;
}
