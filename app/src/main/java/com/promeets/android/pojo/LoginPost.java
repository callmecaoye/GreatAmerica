package com.promeets.android.pojo;

public class LoginPost {

    public final static String REGISTER = "register";
    public final static String RESET_PASSWORD = "resetPassword";

    String userId;
    String accountNumber;
    String oldPassword;
    String newPassword;
    String password;
    String platform;
    String platformToken;
    String verifyCode;
    String codeType;
    String fullName;

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getPassword() {
        return password;
    }

    public String getPlatform() {
        return platform;
    }

    public String getPlatformToken() {
        return platformToken;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setPlatformToken(String platformToken) {
        this.platformToken = platformToken;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
