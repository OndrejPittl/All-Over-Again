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
        RawMessage(int sock, size_t size, std::string msg) {
            this->sock = sock;
            this->size = size;
            this->message = msg;
        }

        int getSock(){
            return this->sock;
        }

        size_t getSize(){
            return this->size;
        }

        std::string getMessage(){
            return this->message;
        }
};


#endif
