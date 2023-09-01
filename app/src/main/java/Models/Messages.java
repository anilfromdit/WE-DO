package Models;

public class Messages {

    private String Date,Message,Sender,Time,Type,Receiver,lastMessage;
    public Messages(){}

public Messages(String msg){
        this.lastMessage=msg;
}
    public Messages(String sender, String type,String message,  String receiver,String time,String date  ) {
        Date = date;
        Message = message;
        Sender = sender;
        Time = time;
        Type = type;
        Receiver = receiver;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        Sender = sender;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getReceiver() {
        return Receiver;
    }

    public void setReceiver(String receiver) {
        Receiver = receiver;
    }
}
