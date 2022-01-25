package com.sar.taxvault.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Document implements Parcelable {

    String name;
    Long timeStamp;
    Long size;
    String url;
    Boolean hasAccessToShare;
    String time;
    String userId;
    String id;
    String type;
    String userName;
    String businessId;

    public Document(String name, Long timeStamp, Long size, String url) {

        this.name = name;
        this.timeStamp = timeStamp;
        this.size = size;
        this.url = url;
        this.hasAccessToShare = true;

        Date date = new Date();
        date.setTime(timeStamp);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        time = dateFormat.format(date);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public Document() {


    }

    protected Document(Parcel in) {
        name = in.readString();
        if (in.readByte() == 0) {
            timeStamp = null;
        } else {
            timeStamp = in.readLong();
        }
        if (in.readByte() == 0) {
            size = null;
        } else {
            size = in.readLong();
        }
        url = in.readString();
        byte tmpHasAccessToShare = in.readByte();
        hasAccessToShare = tmpHasAccessToShare == 0 ? null : tmpHasAccessToShare == 1;
        time = in.readString();
        userId = in.readString();
        id = in.readString();
        type = in.readString();
        userName = in.readString();
        businessId = in.readString();
    }

    public static final Creator<Document> CREATOR = new Creator<Document>() {
        @Override
        public Document createFromParcel(Parcel in) {
            return new Document(in);
        }

        @Override
        public Document[] newArray(int size) {
            return new Document[size];
        }
    };

    public Boolean getHasAccessToShare() {
        return hasAccessToShare;
    }

    public void setHasAccessToShare(Boolean hasAccessToShare) {
        this.hasAccessToShare = hasAccessToShare;
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

    public void setBusinessId(String businessId) {

        this.businessId = businessId;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        if (timeStamp == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(timeStamp);
        }
        if (size == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(size);
        }
        dest.writeString(url);
        dest.writeByte((byte) (hasAccessToShare == null ? 0 : hasAccessToShare ? 1 : 2));
        dest.writeString(time);
        dest.writeString(userId);
        dest.writeString(id);
        dest.writeString(type);
        dest.writeString(userName);
        dest.writeString(businessId);
    }
}
