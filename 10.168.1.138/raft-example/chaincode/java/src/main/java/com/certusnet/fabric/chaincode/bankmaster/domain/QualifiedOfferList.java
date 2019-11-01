package com.certusnet.fabric.chaincode.bankmaster.domain;

import java.io.Serializable;
import java.util.List;

public class QualifiedOfferList implements Serializable {
    private String id;
    private List<QualifiedOffer> qualifiedOfferList;
    private String flag;
    private String time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<QualifiedOffer> getQualifiedOfferList() {
        return qualifiedOfferList;
    }

    public void setQualifiedOfferList(List<QualifiedOffer> qualifiedOfferList) {
        this.qualifiedOfferList = qualifiedOfferList;
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
        return "QualifiedOfferList{" +
                "id='" + id + '\'' +
                ", qualifiedOfferList=" + qualifiedOfferList +
                ", flag='" + flag + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
