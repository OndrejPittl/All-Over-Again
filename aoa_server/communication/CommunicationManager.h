#ifndef COMMUNICATION_MANAGER_H
#define COMMUNICATION_MANAGER_H

#include <string>
#include <sys/socket.h>

#include "../partial/Semaphore.h"
#include "../partial/SafeQueue.h"
#include "Receiver.h"
#include "Message.h"


class CommunicationManager {
	private:
		
		/**
		*	Input message buffer.
		*/
		std::string inputBuffer;

		SafeQueue<Message *> *messageQueue;
		SafeQueue<Message *> *readableMessages;
//        Semaphore *semaphore;

		Receiver *receiver;
		std::thread receiverThrd;



//		fd_set *cliSockSet;
//		fd_set *writeSockSet;
//		fd_set *readSockSet;

		


	public:
		CommunicationManager();

        void startCommunication();

		/**
		*	
		*/
		void receiveMessage(int fdIndex, int byteCount);

		/**
		*	Receives messages via a socket given.
		*/
//		void recvMsg(int sock, std::string *buff);
//        std::string recvMsg(int sock, int byteCount);


		/**
		*	Sends a message via a socket given.
		*/
		bool sendMsg(int sock, std::string txt);
		
		/**
		*	Broadcasts a message to all clients passed by arguments.
		*/
		void sendMsg(fd_set *socks, std::string txt);


//		void setSocketSets(fd_set *cliSockSet, fd_set *writeSockSet, fd_set *readSockSet);
//
//		void setWriteSocketSet(fd_set *writeSockSet);
//
//		void setReadSocketSet(fd_set *readSockSet);



};

#endif