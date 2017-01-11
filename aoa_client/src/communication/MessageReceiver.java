package communication;

import application.Application;
import application.Connection;

import java.io.BufferedInputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.logging.Logger;
import config.CommunicationConfig;
import partial.Tools;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;


public class MessageReceiver implements Runnable {

    private LinkedBlockingQueue<Message> receivedMessages;

    private LinkedBlockingQueue<Message> incomingMessagesAsync;

    private StringBuilder sb;

    private Logger logger;

    /**
     * Input byte stream.
     */
    //private BufferedInputStream bis;

    private InputStream inStream;

    /**
     *	Buffer for incoming message.
     */
    private byte[] buffer;




    public MessageReceiver(Connection conn, LinkedBlockingQueue<Message> receivedMessages) {
        this.receivedMessages = receivedMessages;
        this.init();
    }

    private void init(){
        this.logger = Logger.getLogger(this.getClass().getName());
        this.sb = new StringBuilder();
        this.buffer = new byte[1024];
    }

    public void run() {

        boolean helloFound;



        ArrayList<String> messages;

        for (;;) {

            this.logger.info("MSGReceiver: waiting for a message.");

            String msgTxt = this.receiveMessage();

            if(msgTxt == null || msgTxt.length() < 0) {
                this.logger.info("MSGReceiver: received an EMPTY message. SERVER DOWN!");
                Application.disconnect(true);
                break;
            }

            this.logger.info("MSGReceiver: received a message.");

            messages = this.separateMessages(msgTxt);

            for (String msg : messages) {
                String msgValidText = "";
                MessageType type = null;

                // checksum
                msgValidText = this.checkMessageChecksum(msg);

                if(this.checkHelloPacket(msgValidText)) {
                    type = MessageType.HELLO;
                } else {
                    type = this.checkMessageType(msgValidText);
                }

                if(msgValidText == null) {
                    continue;
                }

                Message m = new Message(type, msgValidText);
                this.logger.info("MSGReceiver: a message accepted: " + m.getMessage());
                this.receivedMessages.add(m);
            }
        }
    }

    private MessageType checkMessageType(String msgValidText) {
        String[] parts = msgValidText.split(CommunicationConfig.MSG_DELIMITER);

        if(!Tools.isNumber(parts[0]))
            return null;

        return MessageType.nth(Integer.parseInt(parts[0]));
    }

    private String checkMessageChecksum(String msg) {
        // "checksum;message"
        // STX + SUM + DELIM + TXT + ETX
        // *608;Ahoooj#

        int checkSum,
            msgLen = msg.length(),
            delimPos = msg.indexOf(CommunicationConfig.MSG_DELIMITER);

        if(delimPos < 0)
            return null;

        String checkSumStr = msg.substring(0, delimPos),
               message = msg.substring(delimPos + 1, msgLen);

//        this.logger.info("len: " + msgLen + " delimPos: " + delimPos + " of a message: " + msg + " with result: " + message);

        if(!Tools.isNumber(checkSumStr))
            return null;

        checkSum = Integer.parseInt(checkSumStr);

        if(checkSum != Tools.checksum(message, CommunicationConfig.MSG_CHECKSUM_MODULO)) {
            return null;
        }

        return message;
    }

    public boolean checkHelloPacket(String msg) {
        if(msg.contains(CommunicationConfig.MSG_HELLO_SERVER_RESPONSE) && msg.length() == CommunicationConfig.MSG_HELLO_SERVER_RESPONSE.length())
            return true;
        return false;
    }

    private ArrayList<String> separateMessages(String txt){
        ArrayList<String> messages = new ArrayList<>();

        int stxPos = -1,
             etxPos = -1;

        for (int i = 0; i < txt.length(); i++){
            char aChar = txt.charAt(i),
                 aSTX = CommunicationConfig.MSG_STX,
                 aETX = CommunicationConfig.MSG_ETX;

            if(aChar == aSTX) {
                stxPos = i;
            } else if(aChar == aETX && stxPos > -1) {
                etxPos = i;
            }

            if(stxPos > -1 && etxPos > -1) {
                messages.add(txt.substring(stxPos + 1, etxPos));
                stxPos = etxPos = -1;
            }
        }

        return messages;
    }


    private String receiveMessage(){
        int msgLen;
        this.sb.setLength(0);

        try {

            msgLen = this.inStream.read(this.buffer);

            if(msgLen < 0) {
                return null;
            }

            String out = new String(Arrays.copyOf(this.buffer, msgLen));

            System.out.print(">>> received: \"" + out + "\" (" + msgLen + " bytes)\n");
            return out;


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    private boolean isValidCharacter(int c){
        return c >= CommunicationConfig.ASCII_LOWER && c <= CommunicationConfig.ASCII_UPPER;
    }

    public void setConnection(Connection conn){

        this.inStream = new BufferedInputStream(conn.getInStream());
    }
}
