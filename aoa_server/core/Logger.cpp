#include <stdio.h>
#include <string>
#include <iostream>
#include <sys/time.h>
#include <fstream>
#include <mutex>



#include "../partial/StringBuilder.h"
#include "../partial/tools.h"
#include "Logger.h"


LoggerSeverity Logger::lvl;
std::mutex Logger::mtx;
StringBuilder *Logger::sb;

bool Logger::logging = true;

const std::string Logger::LOG_FILE_PATH = "../logs/aoa_server.log";

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



void Logger::init(){
    Logger::sb = new StringBuilder();
}

void Logger::trace(std::string msg, bool stdOut) {
    if(!logging) return;
	Logger::log(LoggerSeverity::TRACE, msg);
	//if(stdOut) println(msg);
}

void Logger::debug(std::string msg, bool stdOut) {
    if(!logging) return;
	Logger::log(LoggerSeverity::DEBUG, msg);
	//if(stdOut) println(msg);
}

void Logger::fatal(std::string msg, bool stdOut) {
    if(!logging) return;
	Logger::log(LoggerSeverity::FATAL, msg);
	//if(stdOut) println(msg);
}

void Logger::error(std::string msg, bool stdOut) {
    if(!logging) return;
	Logger::log(LoggerSeverity::ERROR, msg);
	//if(stdOut) println(msg);
}

void Logger::warning(std::string msg, bool stdOut) {
    if(!logging) return;
	Logger::log(LoggerSeverity::WARNING, msg);
	//if(stdOut) println(msg);
}

void Logger::info(std::string msg, bool stdOut) {
    if(!logging) return;
	Logger::log(LoggerSeverity::INFO, msg);
	//if(stdOut) println(msg);
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

void Logger::log(LoggerSeverity lvl, std::string msg, bool consoleLog) {

    Logger::mtx.lock();

    timeval curr;
    int ms;
    char buff[256], currTime[260] = "";


    gettimeofday(&curr, NULL);
    ms = curr.tv_usec / 1000;

    strftime(buff, 80, "%H:%M:%S", localtime(&curr.tv_sec));
    sprintf(currTime, "[%s:%03d]: ", buff, ms);

    Logger::sb->clear();

    switch (lvl) {
        case LoggerSeverity::TRACE: Logger::sb->append("TRACE "); break;
        case LoggerSeverity::DEBUG: Logger::sb->append("DEBUG "); break;
        case LoggerSeverity::FATAL: Logger::sb->append("FATAL "); break;
        case LoggerSeverity::ERROR: Logger::sb->append("ERROR "); break;
        case LoggerSeverity::WARNING: Logger::sb->append("WARN "); break;
        case LoggerSeverity::INFO: Logger::sb->append("INFO "); break;
    }

    Logger::sb->append(currTime);
    Logger::sb->append(msg);
    Logger::sb->append("\n");


    if(consoleLog) {
        if(lvl == LoggerSeverity::ERROR) {
            Logger::logConsoleErr();
        } else {
            Logger::logConsole();
        }
    }

    Logger::logFile();
    Logger::mtx.unlock();
}

void Logger::logFile() {
    std::ofstream myfile;
    myfile.open (Logger::LOG_FILE_PATH, std::ios::app);
    myfile << Logger::sb->getString();
    myfile.close();
}

void Logger::logConsole() {
    std::cout << Logger::sb->getString();
}

void Logger::logConsoleErr() {
    std::cerr << Logger::sb->getString();
}
