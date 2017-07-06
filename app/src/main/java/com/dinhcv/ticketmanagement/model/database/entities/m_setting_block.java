package com.dinhcv.ticketmanagement.model.database.entities;

import android.content.ContentValues;
import android.database.Cursor;

import com.dinhcv.ticketmanagement.model.database.orm.OrmRecord;


/**
 * Created by dinhcv on 02/04/2017.
 */
public class m_setting_block extends OrmRecord<m_setting_block> {

    @primary_key(autoincrement = true)
    public int id;

    public int time1;
    public int money1;
    public int time2;
    public int money2;

    @Override
    protected ContentValues onSetContentValues() {
        ContentValues cv = new ContentValues();
        if (id != 0) {
            cv.put("id", id);
        }

        cv.put("time1", time1);
        cv.put("money1", money1);
        cv.put("time2", time2);
        cv.put("money2", money2);

        return cv;
    }

    @Override
    protected void onSetValues(Cursor c) {
        this.id                         = c.getInt(c.getColumnIndex("id"));
        this.time1                      = c.getInt(c.getColumnIndex("time1"));
        this.time2                      = c.getInt(c.getColumnIndex("time2"));
        this.money1                     = c.getInt(c.getColumnIndex("money1"));
        this.money2                     = c.getInt(c.getColumnIndex("money2"));
    }
}
