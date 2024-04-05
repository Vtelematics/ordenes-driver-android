package ordenese.rider.firebase_chat;

public class Chat {

    String sender, receiver, content,type;
    String seen;

    public Chat(String sender, String receiver, String content, String type,String seen) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.type = type;
        this.seen = seen;
    }

    public Chat(String sender, String receiver, String content,String type) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.type = type;
    }

    public Chat() {

    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
