/*
 * OrmRecord.java
 * TODO Description of class here

 * Author  : tupn
 * Created : 2/16/2016
 * Modified: $Date: 2016-10-19 14:13:19 +0700 (Wed, 19 Oct 2016) $

 * Copyright © 2015 www.mdi-astec.vn
 **************************************************************************************************/
package com.dinhcv.ticketmanagement.model.database.orm;

import android.content.ContentValues;
import android.database.Cursor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;

import com.dinhcv.ticketmanagement.utils.Debug;


@SuppressWarnings("unused")
public abstract class OrmRecord<T> {

    /**
     * Enable debug for check performance of database
     */
    public static final boolean PERFORMANCE_DEBUG = false;

    /**
     * Enable / Disable performance database<br>
     * default value = false
     */
    protected static boolean OPTIMIZE_PERFORMANCE_DB_DISABLE = false;


    @Retention(RetentionPolicy.RUNTIME)
    public @interface foreign_key {
        String table();

        String id();
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface primary_key {
        boolean autoincrement();
    }


    /* Just use one instance of Db for increasing performance <br>
     * This is very useful for SQLCipher
     */
    private static SQLiteDatabase mTransactionDb = null;
    private static SQLiteDatabase mReadableDb = null;
    private static SQLiteDatabase mWritableDb = null;


    /**
     * Table name
     *
     * @return Table name
     */
    private String getTableName() {
        return getClass().getSimpleName();
    }

    /**
     * Create a new writable instance of database
     * <b>note</b> Call SQLiteDatabase.close() after using ro cleanup data
     *
     * @return Readable database writable
     */
    protected static SQLiteDatabase getWritableDatabase() {

        if (mWritableDb == null || OPTIMIZE_PERFORMANCE_DB_DISABLE || !mWritableDb.isOpen()) {
            mWritableDb = OrmDatabaseHelper.getInstance().getWritableDatabase();
        }

        return mWritableDb;
    }

    /**
     * Create a new readable instance of database
     * <b>note</b> Call SQLiteDatabase.close() after using ro cleanup data
     *
     * @return Readable database instance
     */
    protected static SQLiteDatabase getReadableDatabase() {

        if (mReadableDb == null || OPTIMIZE_PERFORMANCE_DB_DISABLE || !mReadableDb.isOpen()) {
            mReadableDb = OrmDatabaseHelper.getInstance().getReadableDatabase();
        }
        return mReadableDb;
    }

    /**
     * Generate SQL command for creating table
     *
     * @return SQL creating-table
     */
    protected String onSQLCreateTable() {

        String tableName = getTableName();

        Debug.dbg("[ORM]Create table:" + tableName);

        //Add fields
        int nPrimaryCount = 0;
        Field[] fieldsRaw = this.getClass().getDeclaredFields();

        //sort
        List<Field> fields = new ArrayList<>();
        List<Field> ending = new ArrayList<>();
        for (Field f : fieldsRaw) {
            if (f.getAnnotation(primary_key.class) != null) {
                fields.add(0, f);
            } else if (f.getAnnotation(foreign_key.class) != null) {
                ending.add(f);
            } else {
                fields.add(f);
            }
        }
        fields.addAll(ending);

        //create SQL table query
        String sqlCreateTbl = "";
        String sqlForeignKey = "";
        sqlCreateTbl += "CREATE TABLE IF NOT EXISTS " + tableName + "(";

        for (Field field : fields) {

            String fieldName = field.getName();

            //attributes
            final primary_key primaryKey = field.getAnnotation(primary_key.class);
            if (primaryKey != null) {
                nPrimaryCount++;

                if (nPrimaryCount > 1) {
                    Debug.error("ERROR: Have only one Primary key in each table [%s]", tableName);
                    return null;
                }
            }

            String type = classToSQLiteDataType(field);
            if (type != null) {
                sqlCreateTbl += "\n";

                final foreign_key foreignKey = field.getAnnotation(foreign_key.class);
                if (foreignKey != null) {
                    sqlCreateTbl += "    '" + fieldName + "' " + type;
                    sqlForeignKey += "        FOREIGN KEY (" + fieldName + ") REFERENCES " + foreignKey.table() + " (" + foreignKey.id() + ")\n";
                } else {
                    sqlCreateTbl += "    '" + fieldName + "' " + type;
                    if (primaryKey != null) {
                        sqlCreateTbl += " PRIMARY KEY " + ((type.compareTo("INTEGER") == 0 && primaryKey.autoincrement()) ? "AUTOINCREMENT" : "");
                    }
                }

                sqlCreateTbl += ",";
            }
        }

        //remove ',' character at end of command
        if (sqlCreateTbl.endsWith(",")) {
            sqlCreateTbl = sqlCreateTbl.substring(0, sqlCreateTbl.length() - 1);
        }

        //End
        if (!sqlForeignKey.isEmpty()) {
            sqlCreateTbl += ",\n";
            sqlCreateTbl += sqlForeignKey;
        }
        sqlCreateTbl += ");";

        //Debug
        //Debug.dbg( sqlCreateTbl );
        return sqlCreateTbl;
    }

    /**
     * Get a SQLite type-description of data type
     * int --> INTEGER,...
     *
     * @param field Common filed (int, boolean,...)
     * @return description of type by SQLite syntax
     */
    private String classToSQLiteDataType(Field field) {

        String cls = field.getType().toString();

        if (cls.equals(Boolean.class.toString()) || cls.equals("boolean")) {
            return "INTEGER";
        } else if (cls.equals(Long.class.toString()) || cls.equals("long")) {
            return "INTEGER";
        } else if (cls.equals(Integer.class.toString()) || cls.equals("int")) {
            return "INTEGER";
        } else if (cls.equals(Double.class.toString()) || cls.equals("double")) {
            return "REAL";
        } else if (cls.equals(Float.class.toString()) || cls.equals("float")) {
            return "REAL";
        } else if (cls.equals(String.class.toString())) {
            return "TEXT";
        } else if (cls.endsWith("IncrementalChange")) {
            //Skip runtime field of Instant Run function
            return null;
        } else {
            Debug.error("[ORM]ERROR: Not support type of field:" + field.getName()
                    + "\n    type:" + cls
                    + "\n    in class:" + this.getClass().getName());
            return null;
        }
    }

    /**
     * Insert row to database table,
     *
     * @param nullColumnHack nullColumnHack
     * @param values         Content values
     * @return primary id of inserted record<br>
     *     -1 for error
     */
    private long insert(String nullColumnHack, ContentValues values) {
        Debug.dbg("[ORM] Insert to database table:" + getTableName());
        final SQLiteDatabase db;

        long startTime = new Date().getTime();

        if (mTransactionDb != null) {
            db = mTransactionDb;
        } else {
            db = getWritableDatabase();
        }

        //TUPN#WORK_AROUND#Bug in SQLCipher # Start
        if (!db.isOpen()) {
            Debug.dbg("############################################# DATABASE WAS CLOSED ########");
        }
        //TUPN#WORK_AROUND#Bug in SQLCipher # End


        long ret = db.insertWithOnConflict(getTableName(), nullColumnHack, values, SQLiteDatabase.CONFLICT_REPLACE);
        if (ret < 0) {
            Debug.error("ERROR: Error when inserting database in table:" + getTableName());
        }

        if (mTransactionDb == null && OPTIMIZE_PERFORMANCE_DB_DISABLE) {
            db.close();
        }


        if (PERFORMANCE_DEBUG) {
            long delta = new Date().getTime() - startTime;

            Debug.dbg("[########][Performance] Insert %d ms in table %s",
                    delta,
                    getTableName());
        }
        return ret;
    }

    /**
     * Delete a record
     *
     * @param whereClause where-condition SQLite
     * @param whereArgs   where-arguments SQLite
     * @return number of items, those was deleted successfully
     */
    protected int delete(String whereClause, String... whereArgs) {

        long startTime = 0;
        if (PERFORMANCE_DEBUG) {
            long delta = new Date().getTime();
        }

        final SQLiteDatabase db;
        if (mTransactionDb != null) {
            db = mTransactionDb;
        } else {
            db = getWritableDatabase();
        }

        Debug.dbg("[ORM] Delete in table:" + this.getClass().getSimpleName());
        int ret = db.delete(getTableName(), whereClause, whereArgs);

        if (mTransactionDb == null && OPTIMIZE_PERFORMANCE_DB_DISABLE) {
            db.close();
        }

        if (PERFORMANCE_DEBUG) {
            final long delta = new Date().getTime() - startTime;

            Debug.dbg("[########][Performance] Delete %d ms in table %s : whereClause=%s",
                    delta,
                    getTableName(),
                    whereClause);
        }
        return ret;
    }

    /**
     * Start a transaction database<br/>
     * <li>Call setTransactionSuccessful() before accept transaction
     * <li>Call endTransaction() before finishing
     */
    public static void beginTransaction() {
        invalidateDb();

        mTransactionDb = getWritableDatabase();
        mTransactionDb.beginTransaction();
    }

    /**
     * Accept a transaction<br>
     * If don't call this function, your transaction will be canceled
     */
    public static void setTransactionSuccessful() {
        mTransactionDb.setTransactionSuccessful();
    }

    public static void endTransaction() {

        invalidateDb();

        mTransactionDb.endTransaction();
        mTransactionDb.close();
        mTransactionDb = null;
    }

    @SuppressWarnings("unused")
    public static <T> int deleteAll(Class<T> clazz) {

        long startTime = new Date().getTime();

        final SQLiteDatabase db;
        if (mTransactionDb != null) {
            db = mTransactionDb;
        } else {
            db = getWritableDatabase();
        }

        Debug.dbg("[ORM] Delete in table:" + clazz.getSimpleName());

        int ret = db.delete(clazz.getSimpleName(), null, null);

        if (mTransactionDb == null && OPTIMIZE_PERFORMANCE_DB_DISABLE) {
            db.close();
        }

        if (PERFORMANCE_DEBUG) {
            final long delta = new Date().getTime() - startTime;

            Debug.dbg("[########][Performance] Delete all %d ms in table %s",
                    delta,
                    clazz.getSimpleName());
        }

        return ret;
    }


    /**
     * Delete record
     *
     * @param clazz       class
     * @param whereClause where-clause
     * @param whereArgs   where-arrguments
     * @param <T>         Class template
     * @return number of items, those was deleted successfully
     */
    public static <T> int delete(Class<T> clazz, String whereClause, String[] whereArgs) {

        final String tableName = clazz.getSimpleName();

        long startTime = new Date().getTime();

        Debug.dbg("[ORM] Delete data of table:" + tableName);
        final SQLiteDatabase db;

        if (mTransactionDb != null) {
            db = mTransactionDb;
        } else {
            db = getWritableDatabase();
        }

        int ret = db.delete(tableName, whereClause, whereArgs);
        if (ret < 0) {
            Debug.error("ERROR: Error when inserting database in table:" + tableName);
        }

        if (mTransactionDb == null && OPTIMIZE_PERFORMANCE_DB_DISABLE) {
            db.close();
        }

        if (PERFORMANCE_DEBUG) {
            final long delta = new Date().getTime() - startTime;

            Debug.dbg("[########][Performance] Delete %d ms in table %s",
                    delta,
                    clazz.getSimpleName());
        }

        return ret;
    }

    protected static <T> Cursor query(SQLiteDatabase db, Class<T> cls, String whereClause, String[] whereArgs, String groupBy, String orderBy, String limit) {

        final Cursor cursor;

        long startTime = new Date().getTime();

        cursor = db.query(cls.getSimpleName(), null, whereClause, whereArgs,
                groupBy, null, orderBy, limit);

        if (PERFORMANCE_DEBUG) {
            final long delta = new Date().getTime() - startTime;

            Debug.dbg("[########][Performance] Query %d ms in table %s : whereClause=%s",
                    delta,
                    cls.getSimpleName(),
                    whereClause);
        }

        return cursor;
    }

    public static <T> Cursor rawQuery(SQLiteDatabase db, String sqlClause, String[] whereArgs) {

        final Cursor cursor;

        long startTime = new Date().getTime();

        cursor = db.rawQuery(sqlClause, whereArgs);

        if (PERFORMANCE_DEBUG) {
            final long delta = new Date().getTime() - startTime;

            Debug.dbg("[########][Performance] Raw Query %d ms  with sqlClause:\n%s", delta, sqlClause);
        }

        return cursor;
    }

    /**
     * Save current record
     * @return id of record, these was saved
     */
    public long save() {
        ContentValues cv = this.onSetContentValues();
        long ret;

        long startTime = new Date().getTime();

        ret = insert(null, cv);

        if (PERFORMANCE_DEBUG) {
            final long delta = new Date().getTime() - startTime;

            Debug.dbg("[########][Performance] Save %d ms in table %s",
                    delta,
                    getTableName());
        }

        return ret;
    }

    /**
     * Find with query
     * @param clazz table class
     * @param whereClause where clause
     * @param whereArgs arguments of Where-clause
     * @param groupBy group by
     * @param orderBy order by
     * @param limit number of record, need to get
     * @return List of record, <b>null</b> for not found
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> find(Class<T> clazz, String whereClause, String[] whereArgs, String groupBy, String orderBy, String limit) {

        Debug.dbg("[ORM] Query in table:" + clazz.getSimpleName());

        long startTime = new Date().getTime();

        final SQLiteDatabase db;
        if (mTransactionDb != null) {
            db = mTransactionDb;
        } else {
            db = getReadableDatabase();
        }

        Cursor c = OrmRecord.query(db, clazz, whereClause, whereArgs, groupBy, orderBy, limit);
        if (!c.moveToFirst()) {
            Debug.normal("[ORM] Not found any matching data in table (%s)", clazz.getSimpleName());
            c.close();
            if (mTransactionDb == null && OPTIMIZE_PERFORMANCE_DB_DISABLE) {
                db.close();
            }
            return null;
        } else {

            try {
                List<T> results = new ArrayList<>();
                do {
                    OrmRecord log = (OrmRecord) clazz.newInstance();

                    log.onSetValues(c);

                    results.add((T) log);

                } while (c.moveToNext());

                if (results.isEmpty()) {
                    c.close();
                    if (mTransactionDb == null && OPTIMIZE_PERFORMANCE_DB_DISABLE) {
                        db.close();
                    }
                    return null;
                }
                c.close();
                if (mTransactionDb == null && OPTIMIZE_PERFORMANCE_DB_DISABLE) {
                    db.close();
                }
                return results;
            } catch (Exception e) {
                Debug.error("ERROR:" + e.getMessage());
                Debug.printStackTrace(e);
            }
        }
        c.close();
        if (mTransactionDb == null && OPTIMIZE_PERFORMANCE_DB_DISABLE) {
            db.close();
        }

        if (PERFORMANCE_DEBUG) {
            final long delta = new Date().getTime() - startTime;

            Debug.dbg("[########][Performance] Find %d ms in table %s",
                    delta,
                    clazz.getSimpleName());
        }


        return null;
    }


    public static <T, K> List<OrmRecord[]> join(Class<T> class1, String key1, Class<K> class2, String key2,
                                                String whereClause, String[] whereArgs, String orderby, String limitSql) {

        long startTime = new Date().getTime();

        String table1 = class1.getSimpleName();
        String table2 = class2.getSimpleName();

        Debug.dbg("[ORM] Join 2 table: %s,%s", table1, table2);
        final SQLiteDatabase db;

        if (mTransactionDb != null) {
            db = mTransactionDb;
        } else {
            db = getWritableDatabase();
        }

        String query = "";

        query += "SELECT * FROM " + table1;
        query += " INNER JOIN " + table2;
        query += "  ON " + table1 + "." + key1 + "=" + table2 + "." + key2;

        if (whereClause != null) {
            query += " WHERE " + whereClause;
        }

        if (orderby != null && !orderby.trim().isEmpty()) {
            query += " ORDER BY " + orderby;
        }

        if (limitSql != null) {
            query += " LIMIT " + limitSql;
        }

        Debug.normal("Join query: " + query);
        Cursor c = db.rawQuery(query, whereArgs);
        if (!c.moveToFirst()) {
            Debug.normal("[ORM]Cannot join tables");
            c.close();
            if (mTransactionDb == null && OPTIMIZE_PERFORMANCE_DB_DISABLE) {
                db.close();
            }

            if (PERFORMANCE_DEBUG) {
                final long delta = new Date().getTime() - startTime;

                Debug.dbg("[########][Performance] Join %d ms in table %s",
                        delta,
                        class1.getSimpleName());
            }

            return null;
        } else {
            try {

                List<OrmRecord[]> results = new ArrayList<>();
                do {
                    OrmRecord[] record = new OrmRecord[2];

                    record[0] = (OrmRecord) class1.newInstance();
                    record[1] = (OrmRecord) class2.newInstance();

                    record[0].onSetValues(c);
                    record[1].onSetValues(c);

                    results.add(record);
                } while (c.moveToNext());

                c.close();
                if (mTransactionDb == null && OPTIMIZE_PERFORMANCE_DB_DISABLE) {
                    db.close();
                }

                if (PERFORMANCE_DEBUG) {
                    final long delta = new Date().getTime() - startTime;

                    Debug.dbg("[########][Performance] Join %d ms in table %s",
                            delta,
                            class1.getSimpleName());
                }

                return results;
            } catch (Exception e) {
                Debug.error("ERROR:" + e.getMessage());
                Debug.printStackTrace(e);
            }
        }
        c.close();

        if (mTransactionDb == null && OPTIMIZE_PERFORMANCE_DB_DISABLE) {
            db.close();
        }

        if (PERFORMANCE_DEBUG) {
            final long delta = new Date().getTime() - startTime;

            Debug.dbg("[########][Performance] Join %d ms in table %s",
                    delta,
                    class1.getSimpleName());
        }

        return null;
    }

    public static <T> int count(Class<T> clazz, String... whereCause) {

        int ret = 0;
        final String table = clazz.getSimpleName();
        String whereSql = "";

        long startTime = new Date().getTime();

        final String whereField;
        //lockup primary key in fields
        final String primaryKey = getPrimaryKeyName(clazz);

        //user where sql
        if ((whereCause != null) && (whereCause.length > 0)) {
            whereSql = " WHERE " + whereCause[0];
        }

        //query
        final SQLiteDatabase db;
        if (mTransactionDb != null) {
            db = mTransactionDb;
        } else {
            db = getReadableDatabase();
        }

        final String sql = "SELECT COUNT(" + primaryKey + ") FROM " + table + whereSql;

        Cursor c = db.rawQuery(sql, null);
        if (!c.moveToFirst()) {
            Debug.dbg("[ORM] Cannot get count of table:" + table);
        } else {
            ret = c.getInt(0);
        }
        c.close();
        if (mTransactionDb == null && OPTIMIZE_PERFORMANCE_DB_DISABLE) {
            db.close();
        }

        if (PERFORMANCE_DEBUG) {
            final long delta = new Date().getTime() - startTime;

            Debug.dbg("[########][Performance] Count %d ms in table %s",
                    delta,
                    clazz.getSimpleName());
        }

        return ret;
    }

    public static <T> List<T> listAll(Class<T> clazz) {
        return OrmRecord.find(clazz, null, null, null, null, null);
    }


    /**
     * Get all items with ordering
     *
     * @param clazz   class of table
     * @param orderBy order by
     * @param <T>     Class template
     * @return List of items
     */
    public static <T> List<T> listAll(Class<T> clazz, String orderBy) {
        return find(clazz, null, null, null, orderBy, null);
    }

    /**
     * Find a item by id
     *
     * @param clazz       Class of table
     * @param id          ID (by string)
     * @param primaryKeys a custom primary key
     * @param <T>         class template
     * @return T object if found, null for not found
     */
    public static <T> T findById(Class<T> clazz, String id, String... primaryKeys) {

        final String whereField;
        if ((primaryKeys == null) || (primaryKeys.length == 0)) {
            //lockup primary key in fields
            whereField = getPrimaryKeyName(clazz);
        } else {
            whereField = primaryKeys[0];
        }

        //find with id
        List<T> found = OrmRecord.find(clazz, whereField + "='" + id + "'", null, null, null, null);
        if ((found == null) || (found.isEmpty())) {
            return null;
        }

        return found.get(0);
    }

    private static <T> String getPrimaryKeyName(Class<T> clazz) {
        //lockup primary key in fields
        Field[] fields = clazz.getDeclaredFields();
        if ((fields == null) || (fields.length == 0)) {
            Debug.error("ERROR: Class (%s) has not any fields", clazz.getName());
            return null;
        }

        for (Field f : fields) {
            if (f.getAnnotation(primary_key.class) != null) {
                return f.getName();
            }
        }

        Debug.error("ERROR: Class have not primary_key. Use the first field as primary_key");
        return fields[0].getName();
    }

    /**
     * Find by Id number
     *
     * @param clazz       class table
     * @param id          id of items
     * @param primary_key a custom primary key
     * @param <T>         class template
     * @return T object if found
     */
    public static <T> T findById(Class<T> clazz, long id, String... primary_key) {
        return findById(clazz, String.valueOf(id), primary_key);
    }

    /**
     * Invalidate current instance of Db
     */
    private static void invalidateDb() {
        mReadableDb = null;
        mWritableDb = null;
    }

    protected Double getDoubleOrNull( Cursor c, String column ){
        int i = c.getColumnIndex( column );
        if( c.isNull( i ) ){
            return null;
        }
        return c.getDouble( i );
    }

    protected Integer getIntegerOrNull( Cursor c, String column ){
        int i = c.getColumnIndex( column );
        if( c.isNull( i ) ){
            return null;
        }
        return c.getInt( i );
    }

    /**
     * Use to set fields to ContentValues of database
     *
     * @return All values
     */
    abstract protected ContentValues onSetContentValues();

    /**
     * Override to set fields of object by Database values
     *
     * @param c cursor of current row in database
     */
    abstract protected void onSetValues(Cursor c);

    public static SQLiteDatabase getTransactionDb() {
        return mTransactionDb;
    }

    public static SQLiteDatabase getReadableDb() {
        return mReadableDb;
    }

    public static SQLiteDatabase getWritableDb() {
        return mWritableDb;
    }
}
/***************************************************************************************************
 * End of file
 **************************************************************************************************/