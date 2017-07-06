package com.dinhcv.ticketmanagement.model.database.entities;

import android.content.ContentValues;
import android.database.Cursor;

import com.dinhcv.ticketmanagement.model.database.orm.OrmRecord;

/**
 * Created by dinhcv on 02/04/2017.
 */
public class m_user extends OrmRecord {

    @primary_key(autoincrement = true)
    public int id; //uid

    public String account;
    public String password;
    public String name;
    public int permission;
    public int working_shift;

    @Override
    protected ContentValues onSetContentValues() {
        ContentValues cv = new ContentValues();
        if (id != 0) {
            cv.put("id", id);
        }

        cv.put("account", account);
        cv.put("password", password);
        cv.put("name", name);
        cv.put("permission", permission);
        cv.put("working_shift", working_shift);

        return cv;
    }

    @Override
    protected void onSetValues(Cursor c) {
        this.id                         = c.getInt(c.getColumnIndex("id"));
        this.account                    = c.getString(c.getColumnIndex("account"));
        this.password                   = c.getString(c.getColumnIndex("password"));
        this.name                       = c.getString(c.getColumnIndex("name"));
        this.permission                 = c.getInt(c.getColumnIndex("permission"));
        this.working_shift              = c.getInt(c.getColumnIndex("working_shift"));
    }
}
