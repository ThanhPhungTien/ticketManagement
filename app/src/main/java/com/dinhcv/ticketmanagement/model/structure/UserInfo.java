package com.dinhcv.ticketmanagement.model.structure;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserInfo {

    private int id;
    private String account;
    private String password;
    private String name;
    private int permission;
    private int workingShift;


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

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public int getPermission() {
        return permission;
    }

    public void setWorkingShift(int workingShift) {
        this.workingShift = workingShift;
    }

    public int getWorkingShift() {
        return workingShift;
    }



}
