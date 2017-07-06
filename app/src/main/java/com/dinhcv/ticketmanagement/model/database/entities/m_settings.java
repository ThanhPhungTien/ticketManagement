package com.dinhcv.ticketmanagement.model.database.entities;

import android.content.ContentValues;
import android.database.Cursor;

import com.dinhcv.ticketmanagement.model.database.orm.OrmRecord;


/**
 * Created by dinhcv on 02/04/2017.
 */
public class m_settings extends OrmRecord<m_settings> {

    @primary_key(autoincrement = false)
    public String key_name;
    public String value;

    @SuppressWarnings("unused")
    public m_settings() {
        //Don't delete this function
    }

    public m_settings(String key_name, String value) {
        this.key_name = key_name;
        this.value = value;
    }

    @Override
    protected ContentValues onSetContentValues() {
        ContentValues cv = new ContentValues();

        cv.put("key_name", key_name);
        cv.put("value", value);

        return cv;
    }

    @Override
    protected void onSetValues(Cursor c) {
        this.key_name = c.getString(c.getColumnIndex("key_name"));
        this.value = c.getString(c.getColumnIndex("value"));
    }
}
