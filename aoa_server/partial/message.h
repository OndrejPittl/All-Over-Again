#ifndef MESSAGE_H
#define MESSAGE_H

// libraries
#include <string>

// headers
//



class Message {
	private:
		int sock;
		std::string msg;

	public:
		Message(int sock, std::string msg);
		int getSock();
		void setSock(int sock);
		std::string getMsg();
		void setMsg(std::string msg);
};


#endif

