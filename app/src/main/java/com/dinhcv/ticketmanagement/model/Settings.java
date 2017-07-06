package com.dinhcv.ticketmanagement.model;

import com.dinhcv.ticketmanagement.model.database.entities.m_settings;
import com.dinhcv.ticketmanagement.utils.Debug;

public class Settings {


    private static final String USERID = "current_id";
    private static final String PERMISSON = "permission";
    private static final String PASSWORD = "current_password";
    private static final String USERNAME = "current_username";


    private static final String PARKING = "current_parking";
    private static final String ADDRESS = "current_address";
    private static final String HOTLINE = "current_hotline";
    private static final String WEBSITE = "current_website";

    private Settings() {
        //Hide constructor
    }


    static public void setUsername(String adminUsername) {
        //update new data
        final m_settings settings = new m_settings(USERNAME, adminUsername);

        //update database
        settings.save();
    }

    static public String getUsername() {
        // get admin username from database
        m_settings adminUsername = m_settings.findById(m_settings.class, USERNAME);
        if (adminUsername == null) {
            Debug.normal("Not exist password");
            return null;
        }

        return adminUsername.value;
    }

    static public void setPassword(String adminPassword) {
        //update new data
        final m_settings settings = new m_settings(PASSWORD, adminPassword);

        //update database
        settings.save();
    }

    static public String getPassword() {
        // get admin password from database
        m_settings adminPassword = m_settings.findById(m_settings.class, PASSWORD);
        if (adminPassword == null) {
            Debug.normal("Not exist password");
            return null;
        }

        return adminPassword.value;
    }

    static public void setCurrentUserid(String userid) {
        //update new data
        final m_settings settings = new m_settings(USERID, userid);

        //update database
        settings.save();
    }

    static public int getCurrentUserid() {
        // get admin password from database
        m_settings user = m_settings.findById(m_settings.class, USERID);
        if (user == null) {
            Debug.normal("Not exist id");
            return 0;
        }

        int userId = Integer.valueOf(user.value);

        return userId;
    }


    static public void settingPermission(int permission) {
        //update new data
        final m_settings settings = new m_settings(PERMISSON, String.valueOf(permission));

        //update database
        settings.save();
    }

    static public int getPermission() {
        // get admin permission from database
        m_settings permission = m_settings.findById(m_settings.class, PERMISSON);
        if (permission == null) {
            Debug.normal("Not exist PERMISSON");
            return 0;
        }

        int permissionInt = Integer.valueOf(permission.value);

        return permissionInt;
    }





    static public void setParking(String parking) {
        //update new data
        final m_settings settings = new m_settings(PARKING, parking);

        //update database
        settings.save();
    }

    static public String getParking() {
        // get admin username from database
        m_settings parking = m_settings.findById(m_settings.class, PARKING);
        if (parking == null) {
            Debug.normal("Not exist Parking");
            return null;
        }

        return parking.value;
    }

    static public void setAddress(String address) {
        //update new data
        final m_settings settings = new m_settings(ADDRESS, address);

        //update database
        settings.save();
    }

    static public String getAddress() {
        // get admin address
        m_settings address = m_settings.findById(m_settings.class, ADDRESS);
        if (address == null) {
            Debug.normal("Not exist address");
            return null;
        }

        return address.value;
    }

    static public void setHotline(String hotline) {
        //update new data
        final m_settings settings = new m_settings(HOTLINE, hotline);

        //update database
        settings.save();
    }

    static public String getHotline() {
        // get admin username from database
        m_settings hotline = m_settings.findById(m_settings.class, HOTLINE);
        if (hotline == null) {
            Debug.normal("Not exist hotline");
            return null;
        }

        return hotline.value;
    }

    static public void setWebsite(String website) {
        //update new data
        final m_settings settings = new m_settings(WEBSITE, website);

        //update database
        settings.save();
    }

    static public String getWebsite() {
        // get admin website
        m_settings website = m_settings.findById(m_settings.class, WEBSITE);
        if (website == null) {
            Debug.normal("Not exist website");
            return null;
        }

        return website.value;
    }

}