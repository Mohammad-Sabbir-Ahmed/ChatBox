package com.example.chatbox;

public class GroupMessages {
    private String from, message,time, date, name,type,messageID,groupName;

    public GroupMessages(){

    }

    public GroupMessages(String from, String message, String time, String date,
                         String name, String type,String messageID,String groupName) {
        this.from = from;
        this.message = message;
        this.time = time;
        this.date = date;
        this.name = name;
        this.type = type;
        this.messageID = messageID;
        this.groupName = groupName;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
