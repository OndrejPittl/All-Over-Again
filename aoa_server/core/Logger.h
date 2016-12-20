#ifndef LOGGER_H
#define LOGGER_H

#include <string>
#include <ctype.h>


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
    ERR_MSG_RECEIVE,
};


class Logger {
	private:
		static const std::string CFG_PATH;

		/**
		*	Table of errors.
		*/
		static const std::string ERROR_TABLE[];

		static bool logging;

	public:
		// static Logger& instance() {
		// 	static Singleton INSTANCE;
		// 	return INSTANCE;
		// }

		static void init(int argc, char **argv);

		/**
		*	helps with backtracing events
		*/
		static void trace(std::string msg, bool stdOut = true);

		/**
		*	Informs a developer during debugging.
		*/
		static void debug(std::string msg, bool stdOut = true);

		/**
		*	Informs about very severe error.
		*/
		static void fatal(std::string msg, bool stdOut = true);

		/**
		*	Informs about a serious error which does not interrupt execution.
		*/
		static void error(std::string msg, bool stdOut = true);

		/**
		*	Informs about a less serous error which does not interrupt execution.
		*/
		static void warning(std::string msg, bool stdOut = true);

		/**
		*	Mainly informs about a progress.
		*/
		static void info(std::string msg, bool stdOut = true);

		/**
		*	Mainly informs about a progress.
		*/
		static int printErr(ErrCode ec);
};

#endif