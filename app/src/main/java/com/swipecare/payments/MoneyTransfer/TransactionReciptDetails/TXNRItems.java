package com.swipecare.payments.MoneyTransfer.TransactionReciptDetails;

public class TXNRItems {
    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatuscode() {
        return statuscode;
    }

    public void setStatuscode(String status) {
        this.statuscode = status;
    }

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    String amount;
    String message;
    String statuscode;
    String rrn;
}
