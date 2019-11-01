package com.certusnet.fabric.chaincode.bankmaster.domain;

import java.io.Serializable;

public class AuthenticOffer implements Serializable {
    private String id;
    private String number;
    private String price;
    private String status;
    private String salt;

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

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public String toString() {
        return "AuthenticOffer{" +
                "id='" + id + '\'' +
                ", number='" + number + '\'' +
                ", price='" + price + '\'' +
                ", status='" + status + '\'' +
                ", salt='" + salt + '\'' +
                '}';
    }
}
