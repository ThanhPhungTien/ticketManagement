package com.dinhcv.ticketmanagement.model.database.entities;

import android.content.ContentValues;
import android.database.Cursor;

import com.dinhcv.ticketmanagement.model.database.orm.OrmRecord;


/**
 * Created by dinhcv on 02/04/2017.
 */
public class m_log extends OrmRecord<m_log> {

    @primary_key(autoincrement = true)
    public int id;

    public int userid;
    public String account;
    public long timein;
    public long timeout;

    @Override
    protected ContentValues onSetContentValues() {
        ContentValues cv = new ContentValues();
        if (id != 0) {
            cv.put("id", id);
        }

        cv.put("userid", userid);
        cv.put("account", account);
        cv.put("timein", timein);
        cv.put("timeout", timeout);

        return cv;
    }

    @Override
    protected void onSetValues(Cursor c) {
        this.id                         = c.getInt(c.getColumnIndex("id"));
        this.userid                     = c.getInt(c.getColumnIndex("userid"));
        this.account                    = c.getString(c.getColumnIndex("account"));
        this.timein                     = c.getLong(c.getColumnIndex("timein"));
        this.timeout                    = c.getLong(c.getColumnIndex("timeout"));
    }
}
