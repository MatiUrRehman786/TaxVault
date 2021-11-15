package com.sar.taxvault.Model;

public class Remainder {

    private String id;
    private String userId;
    private String body;
    private Long time;
    private String title;

    public Long getReminderFiringTime() {
        if (reminderFiringTime == null)
            return time;
        return reminderFiringTime;
    }

    public void setReminderFiringTime(Long reminderFiringTime) {
        this.reminderFiringTime = reminderFiringTime;
    }

    private Long reminderFiringTime;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Remainder() {

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

    public String getTitle() {

        if (title == null)
            return "";

        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
