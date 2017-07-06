package com.dinhcv.ticketmanagement.model;

import com.dinhcv.ticketmanagement.model.database.entities.m_user;
import com.dinhcv.ticketmanagement.model.structure.UserInfo;
import com.dinhcv.ticketmanagement.utils.Debug;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dinhcv on 02/04/2017.
 */

public class UserModel {

    private UserInfo entity2UserInfo(m_user e) {

        UserInfo p = new UserInfo();

        p.setId(e.id);
        p.setAccount( e.account);
        p.setPassword( e.password);
        p.setName(e.name);
        p.setPermission(e.permission);
        p.setWorkingShift(e.working_shift);

        return p;
    }



    public  List<UserInfo> getUserList(){


        List<m_user> users = m_user.find(
                m_user.class,
                null,
                null,
                null,
                null,
                null
        );

        if (users != null){
            Debug.normal("User Info list Size is: "+users.size());
        }else {
            Debug.warn("User Info list is null...");
        }

        if (users.size() >1 ){
            Debug.warn("Found %d user of List", users.size());
        }

        //convert to IInfo

        List<UserInfo> userInfos = new ArrayList<>();

        for (int i = 0; i<users.size(); i++) {
            if (i != 0) {
                UserInfo userInfo = entity2UserInfo(users.get(i));
                // add to list
                userInfos.add(userInfo);
            }
        }


        return userInfos;
    }


    public UserInfo getUserByID(int id){

        m_user user = m_user.findById(
                m_user.class,
                id, "id"
        );

        //check results
        if (user == null) {
            Debug.normal("Food is null");
            return null;
        }

        //convert to food info

        UserInfo userInfo  = entity2UserInfo(user);
        Debug.normal("User name: "+userInfo.getName() +" And id = "+userInfo.getId());

        return userInfo;
    }


    public UserInfo login(String account, String password){
        Debug.normal("Login with: "+account);
        String whereStr = "account = ? AND password = ?";
        List<m_user> users = m_user.find(m_user.class,
                whereStr,
                new String[] {account, password},
                null,
                null,
                null);
        if (users == null) {
            Debug.normal("User is null. Login fail "+account);
            return null;
        }


        UserInfo userInfo = entity2UserInfo(users.get(0));
        Debug.normal("Login Success");

        return userInfo;

    }


    public boolean saveUser(UserInfo userInfo){

        m_user user = m_user.findById(m_user.class, userInfo.getId(), "id");
        if (user == null) {
            Debug.normal("User is null. And create new user");
            user = new m_user();
        }

        user.account = userInfo.getAccount();
        user.name = userInfo.getName();
        user.password   = userInfo.getPassword();
        user.permission = userInfo.getPermission();
        user.working_shift = userInfo.getWorkingShift();

        long t = user.save();
        if (t < 0){
            Debug.error("Error. Can not save account");
            return false;
        }
        Debug.normal("Success. Save account success");
        return true;
    }

    public boolean deleteAccount(int uid){
        String whereStr = "id = ?";
        int status = m_user.delete(m_user.class,
                whereStr,
                new String[] {String.valueOf(uid)});
        if (status < 0) {
            Debug.error("Error. Can delete account");
            return false;
        }
        Debug.normal("Delete account success");

        return true;
    }


}
