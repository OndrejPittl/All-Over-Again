package cz.kiv.ups.communication;

import cz.kiv.ups.application.Connection;
import cz.kiv.ups.application.Logger;
import cz.kiv.ups.config.CommunicationConfig;
import cz.kiv.ups.partial.Tools;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.LinkedBlockingQueue;


public class MessageSender implements Runnable {


    private static Logger logger = Logger.getLogger();

    private LinkedBlockingQueue<Message> messageQueue;

    private StringBuilder sb;

    /**
     * Output byte stream.
     */
    private OutputStream outStream;


    public MessageSender(LinkedBlockingQueue<Message> messageQueue) {
        this.messageQueue = messageQueue;
        this.init();
    }

    private void init(){
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

            try {
                this.sendMessage(m);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private void sendMessage(Message m) throws IOException {
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
    public void writeMsg(String msg) throws IOException {
        logger.info("<<<<<<<<< sending: " + msg);
        this.outStream.write(msg.getBytes());
        this.outStream.flush();
    }

    private void clearStringBuilder() {
        this.sb.setLength(0);
    }

    public void setConnection(Connection conn){
        this.outStream = conn.getOutStream();
    }
}
