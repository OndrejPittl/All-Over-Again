package communication;

public class Message {

    private MessageType type;

    private String message;

    private boolean async = false;


    public Message(String message) {
        this.message = message;
    }

    public Message(String message, boolean async) {
        this.message = message;
        this.async = async;
    }

    public Message(MessageType type, String message) {
        this.type = type;
        this.message = message;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
