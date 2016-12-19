#include <string>
#include <ctype.h>

#include "../libs/easylogging++.h"
#include "../partial/tools.h"
#include "Logger.h"

//cmakovská
const std::string Logger::CFG_PATH = "../config/log.conf";

//mejkfa
//const std::string Logger::CFG_PATH = "config/log.conf";

/**
*	Table of errors.
*/
const std::string Logger::ERROR_TABLE[] = {
	"No error.",
	"Invalid input arguments.",
	"Bind error at bind().",
	"Listen error at listen().",
	"Select error at select().",
	"Accept error at accept().",
	"Message receive error."
};


void Logger::init(int argc, char **argv){
	el::Configurations conf(CFG_PATH);
    el::Loggers::reconfigureLogger("default", conf);
    el::Loggers::reconfigureAllLoggers(conf);
	START_EASYLOGGINGPP(argc, argv);
}

void Logger::trace(std::string msg, bool stdOut) {
	LOG(TRACE) << msg;
	if(stdOut) println(msg);
}

void Logger::debug(std::string msg, bool stdOut) {
	LOG(DEBUG) << msg;
	if(stdOut) println(msg);
}

void Logger::fatal(std::string msg, bool stdOut) {
	LOG(FATAL) << msg;
	if(stdOut) println(msg);
}

void Logger::error(std::string msg, bool stdOut) {
	LOG(ERROR) << msg;
	if(stdOut) println(msg);
}

void Logger::warning(std::string msg, bool stdOut) {
	LOG(WARNING) << msg;
	if(stdOut) println(msg);
}

void Logger::info(std::string msg, bool stdOut) {
	LOG(INFO) << msg;
	if(stdOut) println(msg);
}

/**
*	Prints an error corresponding with error code given.
*/
int Logger::printErr(ErrCode ec){
	Logger::error(Logger::ERROR_TABLE[ec].c_str(), false);
	printf("%s\n", Logger::ERROR_TABLE[ec].c_str());
	printf("\n---------------------\n");
	return ec;
}