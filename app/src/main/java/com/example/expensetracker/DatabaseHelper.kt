
package com.example.expensetracker

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ExpenseTracker.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_EXPENSES = "expenses"
        const val COLUMN_ID = "id"
        const val COLUMN_EXPENSE_NAME = "expense_name"
        const val COLUMN_AMOUNT = "amount"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_DATE = "date"
        const val COLUMN_PAYMENT_METHOD = "payment_method"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE $TABLE_EXPENSES (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_EXPENSE_NAME TEXT NOT NULL," +
                "$COLUMN_AMOUNT REAL NOT NULL," +
                "$COLUMN_CATEGORY TEXT," +
                "$COLUMN_DATE TEXT," +
                "$COLUMN_PAYMENT_METHOD TEXT)")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSES")
        onCreate(db)
    }

    fun insertExpense(expenseName: String, amount: Double, category: String, date: String, paymentMethod: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_EXPENSE_NAME, expenseName)
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_CATEGORY, category)
            put(COLUMN_DATE, date)
            put(COLUMN_PAYMENT_METHOD, paymentMethod)
        }
        return db.insert(TABLE_EXPENSES, null, contentValues).also {
            db.close()
        }
    }

    fun updateExpense(id: Int, expenseName: String, amount: Double, category: String, date: String, paymentMethod: String): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_EXPENSE_NAME, expenseName)
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_CATEGORY, category)
            put(COLUMN_DATE, date)
            put(COLUMN_PAYMENT_METHOD, paymentMethod)
        }
        return db.update(TABLE_EXPENSES, contentValues, "$COLUMN_ID = ?", arrayOf(id.toString())).also {
            db.close()
        }
    }

    fun getAllExpenses(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_EXPENSES", null)
    }

    fun getExpense(id: Int): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_EXPENSES WHERE $COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun deleteExpense(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_EXPENSES, "$COLUMN_ID = ?", arrayOf(id.toString())).also {
            db.close()
        }
    }
}