#ifndef MESSAGE_H
#define MESSAGE_H

// libraries
#include <string>
#include "../partial/SafeQueue.h"
#include "MessageType.h"

// headers


class Message {
	private:

        /**
         * Socket connection of a sender of a message.
         */
		int sock;

        /**
         * Size of a message [bytes].
         */
		long byteSize;

        /**
         * Type of a message(/command/request).
         */
        MessageType type;

        /**
         * Body of a message.
         */
		std::string message;


	public:
        static const char STX;
        static const char ETX;
        static const char DELIMITER;
        static const std::string ACK;
        static const std::string NACK;
        static const std::string HELLO_PACKET;
        static const std::string HELLO_PACKET_RESPONSE;


		Message();
		Message(int sock, std::string msg);
		Message(int sock, long byteSize, std::string msg);

        int getSock();
        void setSock(int sock);

        std::string getMessage();
        void setMessage(std::string msg);

        long getSize();
		void setSize(long bytes);

        MessageType getType();
        void setType(MessageType type);

};


#endif

