package com.sar.taxvault.Model;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

public class Document {

    String name;
    Long timeStamp;
    String size;
    String url;
    Boolean hasAccessToShare;
    String userId;


    public Document(String name, Long timeStamp, String size, String url) {
        this.name = name;
        this.timeStamp = timeStamp;
        this.size = size;
        this.url = url;
        this.hasAccessToShare = true;
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    public Document() {
        this.name = name;
        this.timeStamp = timeStamp;
        this.size = size;
        this.url = url;
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

    public String getSize() {

        if (size == null)
            return "0 GB";

        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public Boolean getHasAccessToShare() {
        return hasAccessToShare;
    }

    public void setHasAccessToShare(Boolean hasAccessToShare) {
        this.hasAccessToShare = hasAccessToShare;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTime() {

        Date date = new Date(timeStamp);

        return  date.toString();
    }
}
