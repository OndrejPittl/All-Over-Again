package cz.kiv.ups.communication;

import cz.kiv.ups.application.Connection;
import cz.kiv.ups.config.CommunicationConfig;
import cz.kiv.ups.partial.Tools;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;


public class MessageSender implements Runnable {


    private Logger logger;

    private Connection conn;

    private LinkedBlockingQueue<Message> messageQueue;

    private StringBuilder sb;

    /**
     * Output byte stream.
     */
    //private BufferedOutputStream bos;

    private OutputStream outStream;


    public MessageSender(Connection conn, LinkedBlockingQueue<Message> messageQueue) {
        this.conn = conn;
        this.messageQueue = messageQueue;
        this.init();
    }

    private void init(){
        this.logger = Logger.getLogger(this.getClass().getName());
        this.sb = new StringBuilder();
    }

    public void run() {
        for (;;) {
            Message m;

            try {
                m = this.messageQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }

            this.sendMessage(m);
        }
    }

    private void sendMessage(Message m) {
        String msg = m.getMessage();

        this.sb.append(CommunicationConfig.MSG_STX);
        this.sb.append(Tools.checksum(msg, CommunicationConfig.MSG_CHECKSUM_MODULO));
        this.sb.append(CommunicationConfig.MSG_DELIMITER);
        this.sb.append(msg);
        this.sb.append(CommunicationConfig.MSG_ETX);
        this.writeMsg(this.sb.toString());

        this.clearStringBuilder();
    }

    /**
     * Universal method sending server a message.
     * @param msg	message to send
     */
    public void writeMsg(String msg) {
        try {
//            this.logger.info("<<<<<<<<< sending: " + msg);
            System.out.println("<<<<<<<<< sending: " + msg);
            this.outStream.write(msg.getBytes());
            this.outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearStringBuilder() {
        this.sb.setLength(0);
    }

    public void setConnection(Connection conn){
        this.conn = conn;
        this.outStream = conn.getOutStream();
    }
}