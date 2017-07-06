package com.dinhcv.ticketmanagement.model;

import com.dinhcv.ticketmanagement.model.database.entities.m_log;
import com.dinhcv.ticketmanagement.model.database.entities.m_setting_block;
import com.dinhcv.ticketmanagement.model.database.entities.m_user;
import com.dinhcv.ticketmanagement.model.structure.LogInfo;
import com.dinhcv.ticketmanagement.model.structure.UserInfo;
import com.dinhcv.ticketmanagement.utils.Debug;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by dinhcv on 02/04/2017.
 */

public class LogModel {

    private LogInfo entity2LogInfo(m_log e) {

        LogInfo p = new LogInfo();

        p.setId(e.id);
        p.setAccount( e.account);
        p.setUserid( e.userid);
        p.setTimein(new Date(e.timein));
        p.setTimeout(new Date(e.timeout));

        return p;
    }



    public boolean updateLastLog(Date timout){

        List<m_log> m_logs = m_log.find(
                m_log.class,
                null,
                null,
                null,
                null,
                null
        );

        if (m_logs != null){
            Debug.normal("User Info list Size is: "+m_logs.size());
        }else {
            Debug.warn("User Info list is null...");
            return false;
        }

        if (m_logs.size() >1 ){
            Debug.warn("Found %d user of List", m_logs.size());
        }

        //convert to IInfo

        m_log log = m_logs.get(m_logs.size()-1);
        log.timeout = timout.getTime();

        long t = log.save();
        if (t < 0){
            Debug.error("Error. Can not save log");
            return false;
        }
        Debug.normal("Success. Save log success");
        return true;
    }



    public List<LogInfo> getLogList(Date fromDate, Date toDate, int pageIndex, int pageSize){

        String  condition = "timein > " + fromDate.getTime() + " AND timein < "+ toDate.getTime();
        //limit sql
        final String limitSql = String.format(Locale.US, "%d, %d", (pageSize * pageIndex), pageSize);

        List<m_log> logs = m_log.find(
                m_log.class,
                condition,
                null,
                null,
                "id DESC",
                limitSql
        );

        //check results
        if ((logs == null) || (logs.isEmpty())) {
            Debug.normal("logs is null");
            return null;
        }

        if (logs.size() >1 ){
            Debug.warn("Found %d logs of List", logs.size());
        }

        //convert to Info

        List<LogInfo> logInfos = new ArrayList<>();
        for (int i = 0; i<logs.size(); i++) {
            LogInfo logInfo = entity2LogInfo(logs.get(i));
            // add to list
            logInfos.add(logInfo);
        }

        return logInfos;
    }


    public boolean saveLog(LogInfo logInfo){

        m_log log = m_log.findById(m_log.class, logInfo.getId(), "id");
        if (log == null) {
            Debug.normal("LogInfo is null. And create new LogInfo");
            log = new m_log();
        }

        log.userid = logInfo.getUserid();
        log.account = logInfo.getAccount();
        log.timein   = logInfo.getTimein().getTime();
        log.timeout = logInfo.getTimeout().getTime();

        long t = log.save();
        if (t < 0){
            Debug.error("Error. Can not save log");
            return false;
        }
        Debug.normal("Success. Save log success");
        return true;
    }


    public boolean saveSettingBlock(m_setting_block setting_block){

        m_setting_block settingBlock = m_setting_block.findById(m_setting_block.class, setting_block.id, "id");
        if (settingBlock == null) {
            Debug.normal("setting Block is null. And create new settingBlock");
            settingBlock = new m_setting_block();
        }

        settingBlock.time1 = setting_block.time1;
        settingBlock.time2 = setting_block.time2;
        settingBlock.money1   = setting_block.money1;
        settingBlock.money2 = setting_block.money2;

        long t = settingBlock.save();
        if (t < 0){
            Debug.error("Error. Can not save setting Block");
            return false;
        }
        Debug.normal("Success. Save setting Block success");
        return true;
    }

    public  m_setting_block getSettingBlock(){


        List<m_setting_block> setting_blocks = m_setting_block.find(
                m_setting_block.class,
                null,
                null,
                null,
                null,
                null
        );

        if (setting_blocks != null){
            Debug.normal("setting_blocks list Size is: "+setting_blocks.size());
        }else {
            Debug.warn("setting_blocks list is null...");
            return null;
        }

        if (setting_blocks.size() >1 ){
            Debug.warn("Found %d user of List", setting_blocks.size());
        }

        m_setting_block settingBlock = setting_blocks.get(0);

        return settingBlock;
    }


}
