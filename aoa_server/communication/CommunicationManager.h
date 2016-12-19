#ifndef COMMUNICATION_MANAGER_H
#define COMMUNICATION_MANAGER_H

#include <string>
#include <sys/socket.h>


class CommunicationManager {
	private:

		/**
		*	Supposed upper limit of a message length.
		*/
		static const int BUFF_LEN;

		static const std::string BROADCAST_FLAG;

		
		/**
		*	Input message buffer.
		*/
		std::string inputBuffer;

		fd_set *cliSockSet;
		
		fd_set *writeSockSet;

		fd_set *readSockSet;

		


	public:
		CommunicationManager();

		void initBuffer();

		/**
		*	
		*/
		void receiveMessage(int fdIndex, int byteCount);

		/**
		*	Receives messages via a socket given.
		*/
//		void recvMsg(int sock, std::string *buff);
        std::string recvMsg(int sock, int byteCount);

		/**
		*	Sends a message via a socket given.
		*/
		bool sendMsg(int sock, std::string txt);
		
		/**
		*	Broadcasts a message to all clients passed by arguments.
		*/
		void sendMsg(fd_set *socks, std::string txt);

		/**
		*	Detects a broadcast.
		*/
		bool checkBroadcast(std::string *msg);

		/**
		*	Message confirm demonstration.
		*/
		void answerClient(int sock, std::string message);

		/**
		*	Transforms (reverses) a message.
		*/
		void transformMsg(std::string *msg);

		/**
		*	Timestamp at the server.
		*/
		std::string getTimestamp();

		void setSocketSets(fd_set *cliSockSet, fd_set *writeSockSet, fd_set *readSockSet);
		
		void setWriteSocketSet(fd_set *writeSockSet);
		
		void setReadSocketSet(fd_set *readSockSet);



};

#endif