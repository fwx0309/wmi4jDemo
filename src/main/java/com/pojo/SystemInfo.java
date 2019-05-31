package com.pojo;

import java.io.Serializable;

public class SystemInfo implements Serializable {
    private String sysName;     //主机名
    private String osName;     //OS 名称
    private String installTime;     //初始安装日期
    private String sysStartTime;     //系统启动时间
    private String manufacturer;     //系统制造商
    private String totalPhysicalMemory;     //物理内存总量
    private String availablePhysicalMemory;     //可用的物理内存
    private String domain;     //域

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getInstallTime() {
        return installTime;
    }

    public void setInstallTime(String installTime) {
        this.installTime = installTime;
    }

    public String getSysStartTime() {
        return sysStartTime;
    }

    public void setSysStartTime(String sysStartTime) {
        this.sysStartTime = sysStartTime;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getTotalPhysicalMemory() {
        return totalPhysicalMemory;
    }

    public void setTotalPhysicalMemory(String totalPhysicalMemory) {
        this.totalPhysicalMemory = totalPhysicalMemory;
    }

    public String getAvailablePhysicalMemory() {
        return availablePhysicalMemory;
    }

    public void setAvailablePhysicalMemory(String availablePhysicalMemory) {
        this.availablePhysicalMemory = availablePhysicalMemory;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String toString() {
        return "SystemInfo{" +
                "sysName='" + sysName + '\'' +
                ", osName='" + osName + '\'' +
                ", installTime='" + installTime + '\'' +
                ", sysStartTime='" + sysStartTime + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", totalPhysicalMemory='" + totalPhysicalMemory + '\'' +
                ", availablePhysicalMemory='" + availablePhysicalMemory + '\'' +
                ", domain='" + domain + '\'' +
                '}';
    }
}
