package cz.kiv.ups.communication;

import cz.kiv.ups.application.Application;
import cz.kiv.ups.application.Connection;

import java.io.BufferedInputStream;
import java.net.SocketException;
import java.util.Arrays;

import cz.kiv.ups.application.Logger;
import cz.kiv.ups.config.CommunicationConfig;
import cz.kiv.ups.config.ConnectionConfig;
import cz.kiv.ups.config.ErrorConfig;
import cz.kiv.ups.config.ViewConfig;
import cz.kiv.ups.partial.Tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;


public class MessageReceiver implements Runnable {

    private LinkedBlockingQueue<Message> receivedMessages;

    private StringBuilder sb;

    private int incorrectMessages;

    private static Logger logger = Logger.getLogger();


    private InputStream inStream;

    /**
     *	Buffer for incoming message.
     */
    private byte[] buffer;


    public MessageReceiver(LinkedBlockingQueue<Message> receivedMessages) {
        this.receivedMessages = receivedMessages;
        this.init();
    }

    private void init(){
        this.sb = new StringBuilder();
        this.incorrectMessages = 0;
        this.buffer = new byte[1024];
    }

    public void run() {
        ArrayList<String> messages;

        for (;;) {

            String msgTxt;

            try {
                msgTxt = this.receiveMessage();
            } catch (SocketException e) {
                Application.disconnect(true, ErrorConfig.CONNECTION_SERVER_OFFLINE_READ);
                break;
            } catch (IOException e) {
                Application.disconnect(true, ErrorConfig.CONNECTION_SERVER_OFFLINE_READ);
                break;
            }

            if(msgTxt == null || msgTxt.length() < 0) {
                logger.error("MSGReceiver: received an EMPTY message. SERVER DOWN!");
                Application.disconnect(true, ErrorConfig.CONNECTION_SERVER_OFFLINE_READ);
                break;
            }

            messages = this.separateMessages(msgTxt);

            for (String msg : messages) {
                String msgValidText;
                MessageType type;

                // checksum
                msgValidText = this.checkMessageChecksum(msg);

                if(msgValidText == null) {
                    this.markIncorrectAndCheck();
                    continue;
                }

                if(this.checkHelloPacket(msgValidText)) {
                    type = MessageType.HELLO;
                } else {
                    type = this.checkMessageType(msgValidText);
                }


                Message m = new Message(type, msgValidText);
                logger.info("Message accepted: " + m.getMessage());
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

        if(delimPos < 0) {
            this.markIncorrectAndCheck();
            return null;
        }

        String checkSumStr = msg.substring(0, delimPos),
               message = msg.substring(delimPos + 1, msgLen);

        if(!Tools.isNumber(checkSumStr)){
            this.markIncorrectAndCheck();
            return null;
        }

        checkSum = Integer.parseInt(checkSumStr);

        if(checkSum != Tools.checksum(message, CommunicationConfig.MSG_CHECKSUM_MODULO)) {
            this.markIncorrectAndCheck();
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


        if(txt.indexOf(CommunicationConfig.MSG_STX) < 0 ||
           txt.indexOf(CommunicationConfig.MSG_ETX) < 0 ) {
            this.markIncorrectAndCheck();
            return messages;
        }


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


    private String receiveMessage() throws IOException {
        int msgLen;
        this.sb.setLength(0);

        logger.info("MSGReceiver: waiting for a message.");

        msgLen = this.inStream.read(this.buffer);

        if(msgLen < 0)
            return null;

        String out = new String(Arrays.copyOf(this.buffer, msgLen));
        logger.info(">>>>>>>>> received: \"" + out + "\" (" + msgLen + " bytes)");
        return out;
    }

    private boolean isValidCharacter(int c){
        return c >= CommunicationConfig.ASCII_LOWER && c <= CommunicationConfig.ASCII_UPPER;
    }

    public void setConnection(Connection conn){

        this.inStream = new BufferedInputStream(conn.getInStream());
    }

    public void markIncorrectAndCheck(){
        if(++this.incorrectMessages >= ConnectionConfig.MAX_INCORRECT_MESSAGES) {
            // too many incorrects
            Application.disconnect(true, ViewConfig.MSG_SERVER_SUSPICIOUS);
        }
    }
}
