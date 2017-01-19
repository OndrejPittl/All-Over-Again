#ifndef LOGGER_H
#define LOGGER_H

#include <string>
#include <ctype.h>
#include <mutex>

#include "../partial/StringBuilder.h"



/**
*	Enummeration of available errors.
*/
enum ErrCode {
    ERR_NO_ERROR = 0,
    ERR_INVALID_ARGS,
    ERR_BIND,
    ERR_LISTEN,
    ERR_SELECT,
    ERR_ACCEPT,
    ERR_SETSOCKOPT,
	ERR_MSG_RECEIVE,
};

enum class LoggerSeverity {
    TRACE,
	DEBUG,
	FATAL,
	ERROR,
	WARNING,
	INFO
};


class Logger {
	private:
		static const std::string LOG_FILE_PATH;

        static const bool DEVELOPER_MODE;

		/**
		*	Table of errors.
		*/
		static const std::string ERROR_TABLE[];

		static bool logging;

        static std::mutex mtx;

        static StringBuilder *sb;

        static void log(LoggerSeverity lvl, std::string msg, bool consoleLog = true);

        static void logFile();

        static void logConsole();

        static void logConsoleErr();


	public:

		static void init();

		/**
		*	helps with backtracing events
		*/
		static void trace(std::string msg);

		/**
		*	Informs a developer during debugging.
		*/
		static void debug(std::string msg);

		/**
		*	Informs about very severe error.
		*/
		static void fatal(std::string msg);

		/**
		*	Informs about a serious error which does not interrupt execution.
		*/
		static void error(std::string msg);

		/**
		*	Informs about a less serous error which does not interrupt execution.
		*/
		static void warning(std::string msg);

		/**
		*	Mainly informs about a progress.
		*/
		static void info(std::string msg);

		/**
		*	Mainly informs about a progress.
		*/
		static int printErr(ErrCode ec);


		static void print(std::string str);

		static void disableLogging(bool disabled);
};

#endif