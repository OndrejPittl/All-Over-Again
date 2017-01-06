#ifndef COMMUNICATION_MANAGER_H
#define COMMUNICATION_MANAGER_H

#include <string>
#include <sys/socket.h>

#include "../partial/SafeQueue.h"
#include "../partial/StringBuilder.h"
#include "MessageValidator.h"
#include "MessageProcessor.h"
#include "Message.h"
#include "RawMessage.h"
#include "MessageSender.h"
#include "../core/Application.h"


class CommunicationManager {
	private:
		
		/**
		*	Input message buffer.
		*/
		std::string inputBuffer;

		Application *app;

        /**
         * A queue of validated deserialized incoming messages.
         */
        SafeQueue<Message *> *messageQueue;

        /**
         *
         */
        SafeQueue<Message *> *sendMessageQueue;

		/**
		 * A queue of read raw messages from sockets.
		 */
		SafeQueue<RawMessage *> *rawMessageQueue;


        /**
         * Validates and deserializates raw incoming messages.
         */
        MessageValidator *msgValidator;

        /**
         *	Separate thread validating raw messages.
         */
        std::thread msgValidatorThrd;

        /**
         * Processes valid instantiated messages. Due to
         * their type handles their process.
         */
        MessageProcessor *msgProcessor;

        /**
         *  A separate thread of a MSGProcessor.
         */
        std::thread msgProcessorThrd;



        MessageSender *msgSender;

        std::thread msgSenderThrd;



		StringBuilder *log;



		void init();

	    /**
		*	Receives messages via a socket given.
		*/
		std::string recvMsg(int sock, int byteCount);



	public:

		/**
		 * Constructor.
		 * @param messageQueue
		 */
//		CommunicationManager(SafeQueue<Message *> *messageQueue);
		CommunicationManager(Application *app);


        void startMessageValidator();

        void startMessageProcessor();

        void startMessageSender();

		/**
		*	
		*/
		void receiveMessage(int fdIndex, int byteCount);



};

#endif