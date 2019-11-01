package com.certusnet.fabric.chaincode.bankmaster.domain;

import java.io.Serializable;

public class ElectricityTradingRecord implements Serializable {
    private String oldSoc;
    private String addSoc;
    private String money;
    private String buyerId;
    private String sellerId;
    private String startTime;
    private String chargingTime;

    public String getOldSoc() {
        return oldSoc;
    }

    public void setOldSoc(String oldSoc) {
        this.oldSoc = oldSoc;
    }

    public String getAddSoc() {
        return addSoc;
    }

    public void setAddSoc(String addSoc) {
        this.addSoc = addSoc;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getChargingTime() {
        return chargingTime;
    }

    public void setChargingTime(String chargingTime) {
        this.chargingTime = chargingTime;
    }

    @Override
    public String toString() {
        return "ElectricityTradingRecord{" +
                "oldSoc='" + oldSoc + '\'' +
                ", addSoc='" + addSoc + '\'' +
                ", money='" + money + '\'' +
                ", buyerId='" + buyerId + '\'' +
                ", sellerId='" + sellerId + '\'' +
                ", startTime='" + startTime + '\'' +
                ", chargingTime='" + chargingTime + '\'' +
                '}';
    }
}
