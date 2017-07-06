package com.dinhcv.ticketmanagement.model;

import android.content.Context;

import com.dinhcv.ticketmanagement.model.database.entities.m_setting_block;
import com.dinhcv.ticketmanagement.model.database.entities.m_user;
import com.dinhcv.ticketmanagement.utils.Debug;

import java.util.List;

/**
 * Created by dinhcv on 02/04/2017.
 */

public class DatabaseDummy {

    public static void generateDummy(Context cnt) {

        List<m_user> list = m_user.listAll(m_user.class);

        if (list == null) {

            Debug.warn("WRN: Make dummy user");

            makeDummyData();

            //set defaut ticket info

            Settings.setParking("Công ty Cổ Phần Hitech Việt Nam");
            Settings.setAddress("Địa chỉ: Trần Bình, Cầu Giấy, HN");
            Settings.setHotline("Hotline: (84-4) 934 466 269");
            Settings.setWebsite("Website: www.hitechviet.com");

        }else {
            Debug.normal("User had exist");
        }


        List<m_setting_block> setting_blocks = m_setting_block.listAll(m_setting_block.class);

        if (setting_blocks == null) {

            Debug.warn("WRN: Make dummy block setting");

            makeDummyBlockData();
        }else {
            Debug.normal("block setting had exist");
        }

    }

    private static void makeDummyData() {

        m_user tbl = new m_user();

        tbl.account = "sysadmin";
        tbl.name = "Administrator";
        tbl.password = "12345";
        tbl.permission = 2;
        tbl.working_shift = 0;

        tbl.save();
    }

    private static void makeDummyBlockData() {

        m_setting_block tbl = new m_setting_block();

        tbl.time1 = 2;
        tbl.money1 = 30000;
        tbl.time2 = 1;
        tbl.money2 = 10000;

        tbl.save();
    }

}
