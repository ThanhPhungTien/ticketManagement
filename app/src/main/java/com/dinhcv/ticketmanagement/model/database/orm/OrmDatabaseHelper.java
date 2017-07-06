/*
 * OrmRecord.java
 * TODO Description of class here

 * Author  : tupn
 * Created : 2/16/2016
 * Modified: $Date: 2016-10-04 18:36:55 +0700 (Tue, 04 Oct 2016) $

 * Copyright © 2015 www.mdi-astec.vn
 **************************************************************************************************/

package com.dinhcv.ticketmanagement.model.database.orm;

import android.content.Context;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dinhcv.ticketmanagement.utils.Debug;


public class OrmDatabaseHelper extends SQLiteOpenHelper {

    private static final int        DATABASE_VERSION = 30;

    private static final String     DATABASE_NAME    = "ticket_manager_db_r" + DATABASE_VERSION + ".db";

    private static OrmDatabaseHelper mDbHelper = null;

    private static List<Class<? extends OrmRecord>>  mOrmRecordClasses = null;

    /**
     * Hidden constructor
     * @param context Device context
     */
    private OrmDatabaseHelper(Context context ) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        //make more salt by Device-ID
        // (this step to make sure each device has difference password for encrypting)
        final String deviceId = Settings.Secure.getString( context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

    }

    @SuppressWarnings("unchecked")
    public static void addOrmRecordClasses( Class<?>[] classes ){

        mOrmRecordClasses=new ArrayList<>();

        for( Class<?> cls: classes ) {
            mOrmRecordClasses.add( (Class<? extends OrmRecord>) cls);
        }
    }

    /**
     * Get singleton instance of database
     * @return singleton instance of database
     */
    public static OrmDatabaseHelper getInstance(){
        return mDbHelper;
    }

    /**
     * Init function, call before do anything
     * @param context Device context
     * @return itself, using as builder-pattern
     */
    public static OrmDatabaseHelper init(Context context){

        if( mDbHelper==null ){
            mDbHelper = new OrmDatabaseHelper( context );
        }

        return mDbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Debug.normal("[ORM] Create all tables");
        try {
            db.beginTransaction();
            for (Class<?> ormRecord : mOrmRecordClasses) {

                OrmRecord orm = (OrmRecord)ormRecord.newInstance();

                String sqlCmd = orm.onSQLCreateTable();

                Debug.dbg( "[ORM] Create  table: \n" + sqlCmd );

                db.execSQL( sqlCmd );
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        }catch (Exception e ){
            Debug.error("[ORM] ERROR: Cannot create Table database");
            Debug.printStackTrace( e );
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //The first version, do not care upgrade case
        if( 1 != i1 ){
            Debug.warn("Database has upgraded, Please check carefully");
        }
    }

    /**
     * Create a new readable instance of database
     * <b>note</b> Call SQLiteDatabase.close() after using ro cleanup data
     * @return Readable database instance
     */
    public  SQLiteDatabase getReadableDatabase() {

        SQLiteDatabase ret;

        long count;
        if( OrmRecord.PERFORMANCE_DEBUG ){
            count = new Date().getTime();
        }

        ret = super.getWritableDatabase();


        if( OrmRecord.PERFORMANCE_DEBUG ){
            long delta = new Date().getTime() - count;

            Debug.dbg("[########][Performance] Create writable instance %d ms", delta );
        }

        return ret;
    }

    /**
     * Create a new writable instance of database
     * <b>note</b> Call SQLiteDatabase.close() after using ro cleanup data
     * @return Readable database writable
     */
    public  SQLiteDatabase getWritableDatabase() {

        SQLiteDatabase ret;

        long count;
        if( OrmRecord.PERFORMANCE_DEBUG ){
            count = new Date().getTime();
        }

        ret = super.getWritableDatabase();

        if( OrmRecord.PERFORMANCE_DEBUG ){
            long delta = new Date().getTime() - count;

            Debug.dbg("[########][Performance] Create writable instance %d ms", delta );
        }

        return ret;
    }
}

/***************************************************************************************************
 * End of file
 **************************************************************************************************/
