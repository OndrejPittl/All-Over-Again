#ifndef MESSAGE_H
#define MESSAGE_H

// libraries
#include <string>
#include "../partial/SafeQueue.h"

// headers

class Message {
	private:
		int sock;
		long byteSize;
		std::string message;

	public:
		Message();
		Message(int sock, std::string msg);
		Message(int sock, long byteSize, std::string msg);

        int getSock();
        void setSock(int sock);

        std::string getMsg();
        void setMsg(std::string msg);

        long getSize();
		void setSize(long bytes);

};


#endif

