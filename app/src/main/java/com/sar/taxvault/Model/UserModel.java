package com.sar.taxvault.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserModel implements Parcelable {

    String userId;
    String firstName = "";
    String lastName = "";
    String phoneNumber = "";
    String email = "";
    String businessId = "";
    String password = "";
    String userType = "individual";
    Boolean rememberMe = true;
    String token = "";
    int maxPost = 10;
    String customerId;
    String clientSecret;
    String currentPackage;
    Long purchasedTSp;
    int postCount = 0;

    protected UserModel(Parcel in) {
        userId = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        phoneNumber = in.readString();
        email = in.readString();
        businessId = in.readString();
        password = in.readString();
        userType = in.readString();
        byte tmpRememberMe = in.readByte();
        rememberMe = tmpRememberMe == 0 ? null : tmpRememberMe == 1;
        token = in.readString();
        maxPost = in.readInt();
        customerId = in.readString();
        clientSecret = in.readString();
        currentPackage = in.readString();
        if (in.readByte() == 0) {
            purchasedTSp = null;
        } else {
            purchasedTSp = in.readLong();
        }
        postCount = in.readInt();
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businesId) {
        this.businessId = businesId;
    }

    public UserModel(String firstName, String lastName, String phoneNumber, String email, String password, String userType, Boolean rememberMe, String token, int maxPost, String customerId, String clientSecret, String currentPackage, Long purchasedTSp, int postCount) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.rememberMe = rememberMe;
        this.token = token;
        this.maxPost = maxPost;
        this.customerId = customerId;
        this.clientSecret = clientSecret;
        this.currentPackage = currentPackage;
        this.purchasedTSp = purchasedTSp;
        this.postCount = postCount;
    }

    public UserModel() {}

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCurrentPackage() {
        return currentPackage;
    }

    public void setCurrentPackage(String currentPackage) {
        this.currentPackage = currentPackage;
    }

    public Long getPurchasedTSp() {
        return purchasedTSp;
    }

    public void setPurchasedTSp(Long purchasedTSp) {
        this.purchasedTSp = purchasedTSp;
    }

    public Boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public int getMaxPost() {
        return maxPost;
    }

    public void setMaxPost(int maxPost) {
        this.maxPost = maxPost;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {

        if (email == null)
            return "";

        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public boolean isAllowedToPost() {

        if (postCount < maxPost)
            return true;

        return false;
    }

    public int getPercentShared() {

        double percent = (postCount * 100 / maxPost);

        return new Double(percent).intValue();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userId);
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeString(phoneNumber);
        parcel.writeString(email);
        parcel.writeString(businessId);
        parcel.writeString(password);
        parcel.writeString(userType);
        parcel.writeByte((byte) (rememberMe == null ? 0 : rememberMe ? 1 : 2));
        parcel.writeString(token);
        parcel.writeInt(maxPost);
        parcel.writeString(customerId);
        parcel.writeString(clientSecret);
        parcel.writeString(currentPackage);
        if (purchasedTSp == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(purchasedTSp);
        }
        parcel.writeInt(postCount);
    }
}