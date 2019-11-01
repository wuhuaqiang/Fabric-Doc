package com.certusnet.fabric.chaincode.bankmaster.domain;

import java.io.Serializable;
import java.util.List;

public class AuthenticOfferList implements Serializable {
    private String id;
    private List<AuthenticOffer> authenticOfferList;
    private String flag;
    private String time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<AuthenticOffer> getAuthenticOfferList() {
        return authenticOfferList;
    }

    public void setAuthenticOfferList(List<AuthenticOffer> authenticOfferList) {
        this.authenticOfferList = authenticOfferList;
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
        return "AuthenticOfferList{" +
                "id='" + id + '\'' +
                ", authenticOfferList=" + authenticOfferList +
                ", flag='" + flag + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
