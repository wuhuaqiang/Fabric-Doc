package com.certusnet.fabric.chaincode.bankmaster.domain;

import java.io.Serializable;
import java.util.List;

public class EncryptedOfferList implements Serializable {
    private String id;
    private List<EncryptedOffer> encryptedOfferList;
    private String flag;
    private String time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<EncryptedOffer> getEncryptedOfferList() {
        return encryptedOfferList;
    }

    public void setEncryptedOfferList(List<EncryptedOffer> encryptedOfferList) {
        this.encryptedOfferList = encryptedOfferList;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "EncryptedOfferList{" +
                "id='" + id + '\'' +
                ", encryptedOfferList=" + encryptedOfferList +
                ", flag='" + flag + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
