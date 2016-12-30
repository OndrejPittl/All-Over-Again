package communication;

import application.Connection;
import config.CommunicationConfig;
import partial.Tools;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;


public class MessageSender implements Runnable {


    private Connection conn;

    private LinkedBlockingQueue<Message> messageQueue;

    private StringBuilder sb;

    /**
     * Output byte stream.
     */
    private BufferedOutputStream bos;


    public MessageSender(Connection conn, LinkedBlockingQueue<Message> messageQueue) {
        this.conn = conn;
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
            System.out.print("sending...\n");
            this.initBufferedOutputStream();
            this.bos.write(msg.getBytes(), 0, msg.length());
            this.bos.flush();
        } catch (IOException e) {
            System.err.print("Error: BufferedOutputStream initialization.\n");
            e.printStackTrace();
        }
    }

    private void initBufferedOutputStream() throws IOException {
        this.bos = new BufferedOutputStream(this.conn.getClientSocket().getOutputStream());
    }

    private void clearStringBuilder() {
        this.sb.setLength(0);
    }

    public void setConnection(Connection conn){
        this.conn = conn;
    }
}
