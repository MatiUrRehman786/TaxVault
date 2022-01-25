package com.sar.taxvault.Model;

import com.google.firebase.database.Exclude;

public class CustomUserModel {

    public String userId;
    public String firstName = "";
    public String lastName = "";
    public String phoneNumber = "";
    public String email = "";
    public String businessId = "";
    public String password = "";
    public String businessType = "";
    public Boolean rememberMe = true;
    public String businessStatus;
    public String token = "";
    public String uniqueID;
    int maxPost = 10;
    public String customerId;
    public String clientSecret;
    public String currentPackage;
    public Long purchasedTSp;
    public int postCount = 0;
    public Boolean twoFactorAuthenticated = false;
    public String userType = "individual";

    @Exclude
    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    @Exclude
    public void setTwoFactorAuthenticated(Boolean twoFactorAuthenticated) {
        this.twoFactorAuthenticated = twoFactorAuthenticated;
    }

    @Exclude
    public String getUniqueID() {

        if(uniqueID == null)
            return "";
        return uniqueID;
    }

    public CustomUserModel() {}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBusinessId() {

        if (businessId == null || businessId.equalsIgnoreCase("null"))

            return "";

        return businessId;
    }

    @Exclude
    public void setBusinessId(String businesId) {
        this.businessId = businesId;
    }


    @Exclude
    public String getClientSecret() {
        return clientSecret;
    }

    @Exclude
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @Exclude
    public String getCustomerId() {
        return customerId;
    }

    @Exclude
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @Exclude
    public String getCurrentPackage() {
        return currentPackage;
    }

    @Exclude
    public void setCurrentPackage(String currentPackage) {
        this.currentPackage = currentPackage;
    }

    @Exclude
    public Long getPurchasedTSp() {
        return purchasedTSp;
    }

    @Exclude
    public void setPurchasedTSp(Long purchasedTSp) {
        this.purchasedTSp = purchasedTSp;
    }

    @Exclude
    public Boolean getRememberMe() {
        return rememberMe;
    }

    @Exclude
    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    @Exclude
    public int getMaxPost() {
        return maxPost;
    }

    @Exclude
    public void setMaxPost(int maxPost) {
        this.maxPost = maxPost;
    }

    @Exclude
    public int getPostCount() {
        return postCount;
    }

    @Exclude
    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    @Exclude
    public String getToken() {
        return token;
    }

    @Exclude
    public void setToken(String token) {
        this.token = token;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    @Exclude
    public String getBusinessType() {
        return businessType;
    }

//    public int getStatus() {
//        return status;
//    }

    @Exclude
    public Boolean getTwoFactorAuthenticated() {
        return twoFactorAuthenticated;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Exclude
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Exclude
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Exclude
    public String getEmail() {

        if (email == null)
            return "";

        return email;
    }

    @Exclude
    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getPassword() {

        if(password == null)
            return "";

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

}
