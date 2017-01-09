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
		int portNum;

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
        fd_set writeSockSet;

        /**
        *	"Working" copy of a set of sockets. Collection that is being modified by select();
        */
        fd_set readSockSet;


		/**
		*	Initializes a connection manager.
		*/
		void init();

	public:
		
		/**
		*	Offset where tracked sockets begin.
		*	Stdin, stdout & stderr excluded.
		*/
		static const int CLIENT_FD_OFFSET;

		/**
		*	Constructor.
		*/
        ConnectionManager(char *portNumber);


		void prepare();

		/**
		*	Port number getter.
		*/
		int getPortNumber();

		/**
		*	Server socket getter.
		*/
		int getServerSocket();

        bool isServerSocket(int sock);



        void prepareClientSocketSet();

        void restoreSocketSets();

        void waitForRequests();

        int isSockReadable(int sock);

        int isSockWritable(int sock);

        fd_set getClientSocketSet();

        fd_set getReadSocketSet();

        fd_set getWriteSocketSet();

        void registerNewClient();

        void deregisterClient(int sock);

	void registerClient(int sock);
};




#endif