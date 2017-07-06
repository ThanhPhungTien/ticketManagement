package com.dinhcv.ticketmanagement.model.structure;

import java.util.Date;

public class LogInfo {

    private int id;
    private int userid;
    private String account;
    private Date timein;
    private Date timeout;


    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccount() {
        return account;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getUserid() {
        return userid;
    }


    public void setTimein(Date timein) {
        this.timein = timein;
    }

    public Date getTimein() {
        return timein;
    }

    public void setTimeout(Date timeout) {
        this.timeout = timeout;
    }

    public Date getTimeout() {
        return timeout;
    }



}
