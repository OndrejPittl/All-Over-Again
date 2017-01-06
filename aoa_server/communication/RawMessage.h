#ifndef RAWMESSAGE_H
#define RAWMESSAGE_H


class RawMessage {

    private:
        int sock;

        /**
         * in bytes
         */
        size_t size;

        std::string message;

    public:
        RawMessage(int sock, size_t size, std::string msg);

        int getSock();

        size_t getSize();

        std::string getMessage();
};


#endif
