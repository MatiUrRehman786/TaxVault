package com.sar.taxvault.Model;

public class Remainder {

    private String id;
    private String userId;
    private String msg;
    private String title;

    public Remainder() {

    }

    public Remainder(String id, String userId, String msg, String title) {
        this.id = id;
        this.userId = userId;
        this.msg = msg;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMsg() {

        if (msg == null)
            return "";

        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTitle() {

        if (title == null)
            return "";

        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
