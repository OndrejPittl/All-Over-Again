#include <stdio.h>
#include <string>
#include <iostream>
#include <sys/time.h>
#include <fstream>
#include <mutex>



#include "../partial/StringBuilder.h"
#include "../partial/tools.h"
#include "Logger.h"


const std::string Logger::LOG_FILE_PATH = "../logs/aoa_server.log";

const bool Logger::DEVELOPER_MODE = true;


std::mutex Logger::mtx;

StringBuilder *Logger::sb;

bool Logger::logging = true;

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
    "Set Socket Option Error.",
	"Message receive error."
};



void Logger::init(){
    Logger::sb = new StringBuilder();
}

void Logger::trace(std::string msg) {
	Logger::log(LoggerSeverity::TRACE, msg);
}

void Logger::debug(std::string msg) {
	Logger::log(LoggerSeverity::DEBUG, msg);
}

void Logger::fatal(std::string msg) {
	Logger::log(LoggerSeverity::FATAL, msg);
}

void Logger::error(std::string msg) {
	Logger::log(LoggerSeverity::ERROR, msg);
}

void Logger::warning(std::string msg) {
	Logger::log(LoggerSeverity::WARNING, msg);
}

void Logger::info(std::string msg) {
	Logger::log(LoggerSeverity::INFO, msg);
}

/**
*	Prints an error corresponding with error code given.
*/
int Logger::printErr(ErrCode ec){
	Logger::error(Logger::ERROR_TABLE[ec].c_str());
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


    if(Logger::logging && consoleLog) {
        if(!(lvl == LoggerSeverity::DEBUG && !Logger::DEVELOPER_MODE)) {  // jo?
            if (lvl == LoggerSeverity::ERROR) {
                Logger::logConsoleErr();
            } else {
                Logger::logConsole();
            }
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

void Logger::print(std::string str) {
    println(str);
}

void Logger::disableLogging(bool disabled) {
    Logger::logging = disabled;
}
