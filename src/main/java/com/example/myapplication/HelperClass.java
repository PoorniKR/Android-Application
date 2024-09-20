package com.example.myapplication;

public class HelperClass {
    String username,password,confirmPassword,mailId;

    public HelperClass(String username, String password, String confirmPassword, String mailId) {
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.mailId = mailId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getMailId() {
        return mailId;
    }

    public void setMailId(String mailId) {
        this.mailId = mailId;
    }
}
