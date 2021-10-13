package domain;

import java.io.Serializable;

public class User implements Serializable {
    private String userId;
    private String name;
    private String idCard;
    private String location;
    private String applicationTitle;
    private String userInfo;
    private String SCDMedicalRecord;
    private String VCLAsset;
    private String receiveAccount;
    private String time;
    private String target;

    public User(String userId, String name, String idCard, String location, String applicationTitle, String userInfo, String SCDMedicalRecord, String VCLAsset, String receiveAccount, String time, String target) {
        this.userId = userId;
        this.name = name;
        this.idCard = idCard;
        this.location = location;
        this.applicationTitle = applicationTitle;
        this.userInfo = userInfo;
        this.SCDMedicalRecord = SCDMedicalRecord;
        this.VCLAsset = VCLAsset;
        this.receiveAccount = receiveAccount;
        this.time = time;
        this.target = target;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getApplicationTitle() {
        return applicationTitle;
    }

    public void setApplicationTitle(String applicationTitle) {
        this.applicationTitle = applicationTitle;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    public String getSCDMedicalRecord() {
        return SCDMedicalRecord;
    }

    public void setSCDMedicalRecord(String SCDMedicalRecord) {
        this.SCDMedicalRecord = SCDMedicalRecord;
    }

    public String getVCLAsset() {
        return VCLAsset;
    }

    public void setVCLAsset(String VCLAsset) {
        this.VCLAsset = VCLAsset;
    }

    public String getReceiveAccount() {
        return receiveAccount;
    }

    public void setReceiveAccount(String receiveAccount) {
        this.receiveAccount = receiveAccount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", idCard='" + idCard + '\'' +
                ", location='" + location + '\'' +
                ", applicationTitle='" + applicationTitle + '\'' +
                ", userInfo='" + userInfo + '\'' +
                ", SCDMedicalRecord='" + SCDMedicalRecord + '\'' +
                ", VCLAsset='" + VCLAsset + '\'' +
                ", receiveAccount='" + receiveAccount + '\'' +
                ", time='" + time + '\'' +
                ", target='" + target + '\'' +
                '}';
    }
}
