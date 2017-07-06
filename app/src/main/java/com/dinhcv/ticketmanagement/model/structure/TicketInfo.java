package com.dinhcv.ticketmanagement.model.structure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class TicketInfo implements Serializable{

    private int id;
    private int userId;
    private int ticketType;
    private String lisencePlate;
    private String lisenceCode;
    private int status;
    private String carInImagePath;
    private String carOutImagePath;
    private Date timeIn;
    private Date timeOut;
    private long fee;
    private int temp;


    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setTicketType(int ticketType) {
        this.ticketType = ticketType;
    }

    public int getTicketType() {
        return ticketType;
    }

    public void setLisencePlate(String lisencePlate) {
        this.lisencePlate = lisencePlate;
    }

    public String getLisencePlate() {
        return lisencePlate;
    }

    public void setLisenceCode(String lisenceCode) {
        this.lisenceCode = lisenceCode;
    }

    public String getLisenceCode() {
        return lisenceCode;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setCarInImagePath(String carInImagePath) {
        this.carInImagePath = carInImagePath;
    }

    public String getCarInImagePath() {
        return carInImagePath;
    }

    public void setCarOutImagePath(String carOutImagePath) {
        this.carOutImagePath = carOutImagePath;
    }

    public String getCarOutImagePath() {
        return carOutImagePath;
    }

    public void setTimeIn(Date timeIn) {
        this.timeIn = timeIn;
    }

    public Date getTimeIn() {
        return timeIn;
    }

    public void setTimeOut(Date timeOut) {
        this.timeOut = timeOut;
    }

    public Date getTimeOut() {
        return timeOut;
    }

    public void setFee(long fee) {
        this.fee = fee;
    }

    public long getFee() {
        return fee;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getTemp() {
        return temp;
    }

}
