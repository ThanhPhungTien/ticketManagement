package com.dinhcv.ticketmanagement.model.database.entities;

import android.content.ContentValues;
import android.database.Cursor;

import com.dinhcv.ticketmanagement.model.database.orm.OrmRecord;

/**
 * Created by dinhcv on 02/04/2017.
 */
public class m_ticket extends OrmRecord {

    @primary_key(autoincrement = true)
    public int id;

    @foreign_key( table = "m_user", id = "id")
    public int user_id;

    public int ticket_type;
    public String lisence_plate;
    public String lisence_code;
    public int status;
    public String car_in_image_path;
    public String car_out_image_path;
    public long time_in;
    public long time_out;
    public long fee;
    public int temp;

    @Override
    protected ContentValues onSetContentValues() {
        ContentValues cv = new ContentValues();
        if (id != 0) {
            cv.put("id", id);
        }

        cv.put("user_id", user_id);
        cv.put("ticket_type", ticket_type);
        cv.put("lisence_plate", lisence_plate);
        cv.put("lisence_code", lisence_code);
        cv.put("status", status);
        cv.put("car_in_image_path", car_in_image_path);
        cv.put("car_out_image_path", car_out_image_path);
        cv.put("time_in", time_in);
        cv.put("time_out", time_out);
        cv.put("fee", fee);
        cv.put("temp", temp);

        return cv;
    }

    @Override
    protected void onSetValues(Cursor c) {
        this.id                       = c.getInt(c.getColumnIndex("id"));
        this.user_id                  = c.getInt(c.getColumnIndex("user_id"));
        this.ticket_type              = c.getInt(c.getColumnIndex("ticket_type"));
        this.lisence_plate            = c.getString(c.getColumnIndex("lisence_plate"));
        this.lisence_code             = c.getString(c.getColumnIndex("lisence_code"));
        this.status                   = c.getInt(c.getColumnIndex("status"));
        this.car_in_image_path        = c.getString(c.getColumnIndex("car_in_image_path"));
        this.car_out_image_path       = c.getString(c.getColumnIndex("car_out_image_path"));
        this.time_in                  = c.getLong(c.getColumnIndex("time_in"));
        this.time_out                 = c.getLong(c.getColumnIndex("time_out"));
        this.fee                      = c.getLong(c.getColumnIndex("fee"));
        this.temp                     = c.getInt(c.getColumnIndex("temp"));
    }
}
