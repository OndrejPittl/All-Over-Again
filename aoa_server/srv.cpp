// headers
#include "libs/easylogging++.h"
#include "core/App.h"
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

	App *app = new App(argc, argv);
	app->run();

	return 0;
}
