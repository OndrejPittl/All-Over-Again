#ifndef MESSAGE_H
#define MESSAGE_H

// libraries
#include <string>
#include "../partial/SafeQueue.h"

// headers

class Message {
	private:
		int sock;
		int byteSize;
		std::string message;

	public:
		Message();
		Message(int sock, std::string msg);

        int getSock();
        void setSock(int sock);

        std::string getMsg();
        void setMsg(std::string msg);

        int getSize();
		void setSize(int bytes);

//        static Message *buildMessages(std::string input);
        std::string *separateMessages(std::string input);

};


#endif

