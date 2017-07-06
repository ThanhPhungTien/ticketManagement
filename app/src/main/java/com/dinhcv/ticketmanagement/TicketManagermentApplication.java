package com.dinhcv.ticketmanagement;

import android.app.Application;

import com.android.print.sdk.PrinterInstance;
import com.dinhcv.ticketmanagement.model.database.entities.m_log;
import com.dinhcv.ticketmanagement.model.database.entities.m_setting_block;
import com.dinhcv.ticketmanagement.model.database.entities.m_settings;
import com.dinhcv.ticketmanagement.model.database.entities.m_user;
import com.dinhcv.ticketmanagement.model.database.entities.m_ticket;
import com.dinhcv.ticketmanagement.model.database.orm.OrmDatabaseHelper;
import com.dinhcv.ticketmanagement.printer.IPrinterOpertion;

import java.net.CookieHandler;
import java.net.CookieManager;


/**
 * Created by dinhcv on 02/04/2017.
 */
public class TicketManagermentApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Save cookies of HttpConnection between each time of connection
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        // DB
        OrmDatabaseHelper.addOrmRecordClasses(new Class[]{
                m_ticket.class,
                m_user.class,
                m_settings.class,
                m_log.class,
                m_setting_block.class,
        });


        OrmDatabaseHelper.init(this);
    }

    private PrinterInstance printerInstance;
    public void setIPinter(PrinterInstance printer){
        printerInstance = printer;
    }

    public PrinterInstance getIPrinter(){
        if (printerInstance == null){
            return null;
        }
        return printerInstance;
    }


    private IPrinterOpertion iPrinterOpertion;
    public void setIPrinterOpertion(IPrinterOpertion printer){
        iPrinterOpertion = printer;
    }

    public IPrinterOpertion getIPrinterOpertion(){
        if (iPrinterOpertion == null){
            return null;
        }
        return iPrinterOpertion;
    }




}
