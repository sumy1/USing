package com.usingstudioo.Models;

import org.json.JSONObject;

import java.io.Serializable;

public class CurrentUser extends BaseModel implements Serializable {
    private int userId;
    private String deviceId;
    private String username;
    private String email;
    private String password;
    private String phone;
    private String loginType;
    private String userType;
    private String hashKey;
    private String OTPVerified;
    private String emailVerified;

    public CurrentUser(JSONObject jsonResponse) {
        this.userId=Integer.valueOf(getValue(jsonResponse, kId, String.class));
        String firstname = getValue(jsonResponse,kFirstName,String.class);
        String lastname = getValue(jsonResponse,kLastName,String.class);
        this.username = firstname+lastname;
        this.email = getValue(jsonResponse,kEmail,String.class);
        if(jsonResponse.has(kDeviceId))
            this.deviceId = getValue(jsonResponse,kDeviceId, String.class);
        if(jsonResponse.has(kDeviceToken))
            this.deviceId = getValue(jsonResponse,kDeviceToken, String.class);
        if(jsonResponse.has(kPassword))
            this.password = getValue(jsonResponse,kPassword,String.class);
        if(jsonResponse.has(kPhone))
            this.phone=getValue(jsonResponse, kPhone,String.class);
        if(jsonResponse.has(kIsOTPVerified))
            this.OTPVerified =getValue(jsonResponse,kIsOTPVerified,String.class);
        else
            this.OTPVerified = "0";
        if(jsonResponse.has(kIsRegisterVerified))
            this.emailVerified =getValue(jsonResponse,kIsRegisterVerified,String.class);
        else
            this.emailVerified = "0";
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getOTPVerified() {
        return OTPVerified;
    }

    public void setOTPVerified(String OTPVerified) {
        this.OTPVerified = OTPVerified;
    }

    public String getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(String emailVerified) {
        this.emailVerified = emailVerified;
    }

}
