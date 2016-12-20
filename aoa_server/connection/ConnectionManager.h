#ifndef ConnectionManager_H
#define ConnectionManager_H

#include <string>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#include "../partial/StringBuilder.h"


/**
*	Default port.
*/
extern const int PORT_NUM;



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


        StringBuilder *sb;


        fd_set cliSockSet;

        fd_set writeSockSet;

        fd_set readSockSet;



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

		/**
		*	Initializes a connection manager.
		*/
		void init();
		
		int startListening();

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

        fd_set getClientSocketSet();

        void registerNewClient();

        void deregisterNewClient(int sock);

};




#endif