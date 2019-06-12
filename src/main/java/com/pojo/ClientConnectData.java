package com.pojo;

import java.io.Serializable;

public class ClientConnectData implements Serializable {
    private String address;
    private String userName;
    private String password;
    private String version;

    public ClientConnectData(){}
    public ClientConnectData(String address,String userName,String password){
        this.address = address;
        this.userName = userName;
        this.password = password;
    }

    public ClientConnectData(String address,String userName,String password,String version){
        this.address = address;
        this.userName = userName;
        this.password = password;
        this.version = version;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "ClientConnectData{" +
                "address='" + address + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
