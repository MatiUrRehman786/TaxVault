package com.sar.taxvault.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Chat implements Parcelable {

    public Boolean isRead;
    public String message;
    public String fromUser;
    public String fromUserName;
    public Long messageTime;
    public String messageType;
    public String type;
    public String toUser;
    public String toUserName;
    public String id;

    public String fileUrl;
    public String fileName;

    public Chat() {

    }

    protected Chat(Parcel in) {
        byte tmpIsRead = in.readByte();
        isRead = tmpIsRead == 0 ? null : tmpIsRead == 1;
        message = in.readString();
        fromUser = in.readString();
        fromUserName = in.readString();
        if (in.readByte() == 0) {
            messageTime = null;
        } else {
            messageTime = in.readLong();
        }
        messageType = in.readString();
        type = in.readString();
        toUser = in.readString();
        toUserName = in.readString();
        id = in.readString();
        fileUrl = in.readString();
        fileName = in.readString();
    }

    public static final Creator<Chat> CREATOR = new Creator<Chat>() {
        @Override
        public Chat createFromParcel(Parcel in) {
            return new Chat(in);
        }

        @Override
        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isRead == null ? 0 : isRead ? 1 : 2));
        dest.writeString(message);
        dest.writeString(fromUser);
        dest.writeString(fromUserName);
        if (messageTime == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(messageTime);
        }
        dest.writeString(messageType);
        dest.writeString(type);
        dest.writeString(toUser);
        dest.writeString(toUserName);
        dest.writeString(id);
        dest.writeString(fileUrl);
        dest.writeString(fileName);
    }
}
