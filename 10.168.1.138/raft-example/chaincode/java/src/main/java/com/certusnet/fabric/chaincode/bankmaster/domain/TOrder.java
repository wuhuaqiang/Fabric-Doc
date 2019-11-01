package com.certusnet.fabric.chaincode.bankmaster.domain;

import java.io.Serializable;
import java.util.List;

public class TOrder implements Serializable {
    private Double buyerId;
    private Double buyerAccount;
    private String buyerName;
    private String buyerTel;
    private String buyerAddr;
    private String sellerId;
    private List<TElectricVehicleInfo> orderItem;
    private Double totalPrice;
    private String time;

    public Double getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Double buyerId) {
        this.buyerId = buyerId;
    }

    public Double getBuyerAccount() {
        return buyerAccount;
    }

    public void setBuyerAccount(Double buyerAccount) {
        this.buyerAccount = buyerAccount;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerTel() {
        return buyerTel;
    }

    public void setBuyerTel(String buyerTel) {
        this.buyerTel = buyerTel;
    }

    public String getBuyerAddr() {
        return buyerAddr;
    }

    public void setBuyerAddr(String buyerAddr) {
        this.buyerAddr = buyerAddr;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public List<TElectricVehicleInfo> getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(List<TElectricVehicleInfo> orderItem) {
        this.orderItem = orderItem;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "TOrder{" +
                "buyerId=" + buyerId +
                ", buyerAccount=" + buyerAccount +
                ", buyerName='" + buyerName + '\'' +
                ", buyerTel='" + buyerTel + '\'' +
                ", buyerAddr='" + buyerAddr + '\'' +
                ", sellerId='" + sellerId + '\'' +
                ", orderItem=" + orderItem +
                ", totalPrice=" + totalPrice +
                ", time='" + time + '\'' +
                '}';
    }
}
