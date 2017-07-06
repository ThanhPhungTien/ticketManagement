package com.dinhcv.ticketmanagement.model.structure;

import java.util.Date;

public class StatisticInfo {

    private Date date;
    private int countIn;
    private int countOut;
    private long revenue;


    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setCountIn(int countIn) {
        this.countIn = countIn;
    }

    public int getCountIn() {
        return countIn;
    }

    public void setCountOut(int countOut) {
        this.countOut = countOut;
    }

    public int getCountOut() {
        return countOut;
    }

    public void setRevenue(long revenue) {
        this.revenue = revenue;
    }

    public long getRevenue() {
        return revenue;
    }


}
