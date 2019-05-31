package com.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * 扫描软件信息
 */
public class SoftwareInfo implements Serializable {
    private String displayName;
    private String displayVersion;
    private String publisher;
    private String installDate;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayVersion() {
        return displayVersion;
    }

    public void setDisplayVersion(String displayVersion) {
        this.displayVersion = displayVersion;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getInstallDate() {
        return installDate;
    }

    public void setInstallDate(String installDate) {
        this.installDate = installDate;
    }

    @Override
    public String toString() {
        return "SoftwareInfo{" +
                "displayName='" + displayName + '\'' +
                ", displayVersion='" + displayVersion + '\'' +
                ", publisher='" + publisher + '\'' +
                ", installDate=" + installDate +
                '}';
    }
}
