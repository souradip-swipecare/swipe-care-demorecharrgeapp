package com.swipecare.payments.MoneyTransfer;

import java.io.Serializable;

/**
 * Created by Basant on 6/10/2017.
 */

public class Beneficiary_Items implements Serializable{

    private String beneid;
    private String benename;
    private String beneaccount;
    private String benebank;
    private String beneifsc;
    private String sendername;
    private String sendernumber;

    public String getBeneid() {
        return beneid;
    }

    public void setBeneid(String beneid) {
        this.beneid = beneid;
    }

    public String getBenename() {
        return benename;
    }

    public void setBenename(String benename) {
        this.benename = benename;
    }

    public String getBeneaccount() {
        return beneaccount;
    }

    public void setBeneaccount(String beneaccount) {
        this.beneaccount = beneaccount;
    }

    public String getBenebank() { return benebank; }

    public void setBenebank(String benebank) {
        this.benebank = benebank;
    }

    public String getBeneifsc() {
        return beneifsc;
    }

    public void setBeneifsc(String beneifsc) {
        this.beneifsc = beneifsc;
    }

    public String getSendernumber() {
        return sendernumber;
    }

    public void setSendernumber(String sendernumber) {
        this.sendernumber = sendernumber;
    }

    public String getSendername() { return sendername; }

    public void setSendername(String sendername) {
        this.sendername = sendername;
    }
}
