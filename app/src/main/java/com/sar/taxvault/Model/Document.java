package com.sar.taxvault.Model;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Document {

    String name;
    Long timeStamp;
    Long size;
    String url;
    Boolean accessToUsers;
    String userId;
    String id;
    String type;
    String userName;

    public Document(String name, Long timeStamp, Long size, String url) {

        this.name = name;
        this.timeStamp = timeStamp;
        this.size = size;
        this.url = url;
        this.accessToUsers = true;

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Document() {

    }

    public String getName() {

        if (name == null)
            return "";

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getHasAccessToShare() {
        return accessToUsers;
    }

    public void setHasAccessToShare(Boolean accessToUsers) {
        this.accessToUsers = accessToUsers;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTime() {

        Date date = new Date(timeStamp);

        SimpleDateFormat spf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

        return spf.format(date);
    }

    public boolean belongsToCurrentUser() {

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (userId.equalsIgnoreCase(currentUserId))
            return true;

        return false;
    }
}
