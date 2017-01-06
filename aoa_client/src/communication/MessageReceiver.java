package communication;

import application.Connection;
import config.CommunicationConfig;
import partial.Tools;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageReceiver implements Runnable {

    private Connection conn;

    private CommunicationParser parser;

    private LinkedBlockingQueue<Message> receivedMessages;

    /**
     * Input byte stream.
     */
    private BufferedInputStream bis;

    /**
     *	Buffer for incoming message.
     */
    private byte[] msgBuffer;




    public MessageReceiver(Connection conn, LinkedBlockingQueue<Message> receivedMessages) {
        this.conn = conn;
        this.receivedMessages = receivedMessages;
        this.init();
    }

    private void init(){
        this.parser = new CommunicationParser();
        this.msgBuffer = new byte[1024];
    }

    public void run() {

        boolean helloFound;

        ArrayList<String> messages;

        for (;;) {

            System.out.println("MSGReceiver: waiting for a message.");
            String msgTxt = this.recvMsg();
            System.out.println("MSGReceiver: received a message.");

            messages = this.separateMessages(msgTxt);

            for (String msg : messages) {
                String msgValidText = "";

                helloFound = this.checkHelloPacket(msg);

                // checksum
                msgValidText = this.checkMessageChecksum(msg);

                if(helloFound) {
                    msgValidText = msg;
                }

                if(msgValidText == null) {
                    continue;
                }

                Message m = new Message(msgValidText);
                System.out.println("MSGReceiver: a message accepted: " + m.getMessage());
                this.receivedMessages.add(m);
            }
        }
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

//        System.out.println("len: " + msgLen + " delimPos: " + delimPos + " of a message: " + msg + " with result: " + message);

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


    private String recvMsg(){
        int msgLen;
        String msg = "";

        try {
            initBufferedInputStream();

            while((msgLen = this.bis.read(this.msgBuffer)) > 0) {
                for (int i = 0; i < msgLen; i++) {
                    if(this.msgBuffer[i] == 0) {
                        break;
                    } else if (this.msgBuffer[i] > 31) {
                        msg = msg + (char) this.msgBuffer[i];
                    }
                }
                System.out.print(">>> received: \"" + msg + "\" (" + msgLen + " bytes)\n");
                return msg;
            }

        } catch (IOException e) {
            System.err.print("ErrorConfig: BufferedInputStream initialization.\n");
            e.printStackTrace();
        }

        return null;
    }

    private void initBufferedInputStream() throws IOException{
        this.bis = new BufferedInputStream(this.conn.getClientSocket().getInputStream());
    }

    public void setConnection(Connection conn){
        this.conn = conn;
    }
}
