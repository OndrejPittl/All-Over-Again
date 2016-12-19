// libraries
#include <stdio.h>
#include <string>

// headers
#include "../core/Logger.h"



// /**
// *	Table of errors.
// */
// const string ERROR_TABLE[] = {
// 	[0] = "No error.",
// 	[1] = "Invalid input arguments.",
// 	[2] = "Bind error at bind().",
// 	[3] = "Listen error at listen().",
// 	[4] = "Select error at select().",
// 	[5] = "Accept error at accept().",
// 	[6] = "Message rececive error."
// };


// /**
// *	Prints an error corresponding with error code given.
// */
// int printErr(ErrCode ec){
// 	printf("%s\n", ERROR_TABLE[ec].c_str());
// 	printf("\n---------------------\n");
// 	return ec;
// }