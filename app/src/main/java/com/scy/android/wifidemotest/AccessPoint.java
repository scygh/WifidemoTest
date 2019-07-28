package com.scy.android.wifidemotest;

/**
 * created by scy on 2019/7/27 19:00
 * gmail：cherseey@gmail.com
 */
public class AccessPoint {

    private String ssid;
    private String password;
    private String encryptionType;

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEncryptionType() {
        return encryptionType;
    }

    public void setEncryptionType(String encryptionType) {
        this.encryptionType = encryptionType;
    }
}
