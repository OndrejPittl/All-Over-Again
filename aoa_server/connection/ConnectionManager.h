#ifndef ConnectionManager_H
#define ConnectionManager_H

#include <string>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#include "../partial/StringBuilder.h"




class ConnectionManager {
	private:

		/**
		*	Port number that is being used.
		*/
		uint portNum;

		/**
		*	Return values of partial operations.
		*/
		int result;

		/**
		*	IP adress & port of the server.
		*/
		struct sockaddr_in srvAddr;

		/**
		*	Socket accepting new connections.
		*/
		int srvSocket;


        StringBuilder *log;


        fd_set cliSockSet;

        /**
        *	"Working" copy of a set of sockets. Collection that is being modified by select();
        */
        fd_set readSockSet;


		/**
		*	Initializes a connection manager.
		*/
		void init();

	public:

		static const uint DEFAULT_PORT;

		/**
		*	Offset where tracked sockets begin.
		*	Stdin, stdout & stderr excluded.
		*/
		static const int CLIENT_FD_OFFSET;

        static const int COMM_INVALID_MSG_LIMIT;

		/**
		*	Constructor.
		*/
        ConnectionManager(uint portNumber);


		void prepare();

		/**
		*	Port number getter.
		*/
		uint getPortNumber();

        bool isServerSocket(int sock);

        void restoreSocketSets();

        int waitForRequests();

        int isSockReadable(int sock);

        void deregisterClient(int sock);

        int acceptConnection();
};




#endif