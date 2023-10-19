package com.example.smtcustomertracker

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        // "Static" variables
        private const val DB_NAME = "smtbiz"
        private const val DB_VERSION = 1
        const val TABLE_NAME = "Customer"
        const val ID = "Id"
        const val NAME = "Name"
        const val EMAIL = "Email"
        const val MOBILE = "Mobile"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val query = (
                "CREATE TABLE $TABLE_NAME ($ID INTEGER PRIMARY KEY, $NAME TEXT, $EMAIL TEXT, $MOBILE TEXT)"
                )
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
    fun getAllCustomers(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    fun addCustomer(name: String, email: String, mobile: String) {
        val values = ContentValues()
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            values.put(NAME, name)
            values.put(EMAIL, email)
            values.put(MOBILE, mobile)
            db.insert(TABLE_NAME, null, values)
            db.setTransactionSuccessful()
        }finally {
            db.endTransaction()
        }
        db.close()
    }

    fun searchCustomer(name: String): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $NAME=\"$name\"", null)
    }
    fun deleteCustomer(id: String): Int {

        val db = this.writableDatabase
        var deletedRows = 0
        db.beginTransaction()
        try {
            deletedRows = db.delete(TABLE_NAME, "$ID=?", arrayOf(id))
            db.setTransactionSuccessful()
        }finally {
            db.endTransaction()
        }
        db.close()
        return deletedRows
    }

    fun updateCustomer(id: String, name: String, email: String, mobile: String): Int {
        val db = writableDatabase
        val values = ContentValues()
        var rows = 0
        db.beginTransaction()
        try {
            values.put(NAME, name)
            values.put(EMAIL, email)
            values.put(MOBILE, mobile)
            rows = db.update(TABLE_NAME, values, "Id=?", arrayOf(id))
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        db.close()
        return rows
    }

    fun checkNumberOfRecords(): Cursor? {
        val db = readableDatabase
        val records = db.rawQuery("SELECT COUNT(*) FROM $TABLE_NAME",null)
        return  records
    }

    fun resetDB() {
        val db = writableDatabase
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db.execSQL("CREATE TABLE $TABLE_NAME ($ID INTEGER PRIMARY KEY, $NAME TEXT, $EMAIL TEXT, $MOBILE TEXT)")
        db.close()
    }
}