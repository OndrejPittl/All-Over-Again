#ifndef COMMUNICATION_MANAGER_H
#define COMMUNICATION_MANAGER_H

#include <string>
#include <sys/socket.h>

#include "../partial/Semaphore.h"
#include "../partial/SafeQueue.h"
#include "MessageProcessor.h"
#include "Message.h"
#include "RawMessage.h"



class CommunicationManager {
	private:
		
		/**
		*	Input message buffer.
		*/
		std::string inputBuffer;

		/**
		 * A queue of validated deserialized incoming messages.
		 */
		SafeQueue<Message *> *messageQueue;

		/**
		 * A queue of read raw messages from sockets.
		 */
		SafeQueue<RawMessage *> *readableMessages;


		/**
		 * Validates and deserializates raw incoming messages.
		 */
		MessageProcessor *msgProcessor;

		/**
		 *	Separate thread processing raw messages.
		 */
		std::thread msgProcessorThrd;


		/**
		*	Receives messages via a socket given.
		*/
		std::string recvMsg(int sock, int byteCount);

		/**
		*	Sends a message via a socket given.
		*/
		bool sendMsg(int sock, std::string txt);

		/**
		*	Broadcasts a message to all clients passed by arguments.
		*/
		void sendMsg(fd_set *socks, std::string txt);



	public:

		/**
		 * Constructor.
		 * @param messageQueue
		 */
		CommunicationManager(SafeQueue<Message *> *messageQueue);


        void startMessageProcessor();

		/**
		*	
		*/
		void receiveMessage(int fdIndex, int byteCount);


};

#endif