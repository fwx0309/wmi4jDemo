package com.pojo;

import java.io.Serializable;

public class ProcessEntity implements Serializable {
    private String pName;           //映像名称:     knbhm.exe
    private String pid;             //PID:          2204
    private String sessionName;     //会话名      :
    private String session;         //会话#   :     1
    private String usedMemory;      //内存使用 :    7,092 K
    private String userName;        //用户名   :    CAD22-WIN7\cad22-win7-u
    private String cpuTime;         //CPU 时间:     0:00:00

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getUsedMemory() {
        return usedMemory;
    }

    public void setUsedMemory(String usedMemory) {
        this.usedMemory = usedMemory;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCpuTime() {
        return cpuTime;
    }

    public void setCpuTime(String cpuTime) {
        this.cpuTime = cpuTime;
    }

    @Override
    public String toString() {
        return "ProcessEntity{" +
                "pName='" + pName + '\'' +
                ", pid='" + pid + '\'' +
                ", sessionName='" + sessionName + '\'' +
                ", session='" + session + '\'' +
                ", usedMemory='" + usedMemory + '\'' +
                ", userName='" + userName + '\'' +
                ", cpuTime='" + cpuTime + '\'' +
                '}';
    }
}
