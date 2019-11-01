package com.certusnet.fabric.chaincode.bankmaster.domain;

import java.io.Serializable;

public class QualifiedOffer implements Serializable {
    private String id;
    private String number;
    private String price;
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "QualifiedOffer{" +
                "id='" + id + '\'' +
                ", number='" + number + '\'' +
                ", price='" + price + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
