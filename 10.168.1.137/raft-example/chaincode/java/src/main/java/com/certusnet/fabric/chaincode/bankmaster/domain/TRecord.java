package com.certusnet.fabric.chaincode.bankmaster.domain;

import java.io.Serializable;

public class TRecord implements Serializable {
    private Double power;
    private Double price;
    private String evId;
    private String userId;
    private String csId;
    private String time;

    public Double getPower() {
        return power;
    }

    public void setPower(Double power) {
        this.power = power;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getEvId() {
        return evId;
    }

    public void setEvId(String evId) {
        this.evId = evId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCsId() {
        return csId;
    }

    public void setCsId(String csId) {
        this.csId = csId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "TRecord{" +
                "power=" + power +
                ", price=" + price +
                ", evId='" + evId + '\'' +
                ", userId='" + userId + '\'' +
                ", csId='" + csId + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
