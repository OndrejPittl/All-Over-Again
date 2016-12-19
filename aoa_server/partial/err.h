#ifndef ERR_H
#define ERR_H

// libraries
#include <string>

// headers



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

/**
*	Table of available errors.
*/
extern const std::string ERROR_TABLE[];

/**
*	Prints an error corresponding with error code given.
*/
int printErr(ErrCode ec);

#endif
