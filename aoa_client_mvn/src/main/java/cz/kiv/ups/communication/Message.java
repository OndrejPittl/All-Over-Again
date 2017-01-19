package cz.kiv.ups.communication;

public class Message {

    private MessageType type;

    private String message;

    public Message(String message) {
        this.message = message;
    }

    public Message(MessageType type, String message) {
        this.type = type;
        this.message = message;
    }

    public MessageType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
