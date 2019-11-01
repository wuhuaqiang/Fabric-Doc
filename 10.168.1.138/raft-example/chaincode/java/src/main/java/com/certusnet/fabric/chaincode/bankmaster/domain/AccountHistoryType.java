package com.certusnet.fabric.chaincode.bankmaster.domain;

public enum AccountHistoryType {
    CREATE_ACCOUNT("开户"), DEPOSITE_MONEY("存款"), DRAWAL_MONEY("取款"), TRANSFER_IN("转入"), TRANSFER_OUT("转出");

    private String description;

    private AccountHistoryType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static AccountHistoryType getTransactionType(String name) {
        for(AccountHistoryType em : values()) {
            if(em.name().equals(name)) {
                return em;
            }
        }
        return null;
    }
}
