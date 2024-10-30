package com.example.expensetracker

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ViewExpensesActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var lvExpenses: ListView
    private lateinit var expensesList: ArrayList<String>
    private var expenseIds: ArrayList<Int> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_expenses)

        dbHelper = DatabaseHelper(this)
        lvExpenses = findViewById(R.id.listViewExpenses)
        expensesList = ArrayList()

        loadExpenses()

        lvExpenses.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val id = expenseIds[position]
            val intent = Intent(this, UpdateExpenseActivity::class.java)
            intent.putExtra("EXPENSE_ID", id)
            startActivity(intent)
        }
    }

    @SuppressLint("Range")
    private fun loadExpenses() {
        val cursor: Cursor = dbHelper.getAllExpenses()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID))
                val expenseName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EXPENSE_NAME))
                expensesList.add(expenseName)
                expenseIds.add(id)
            } while (cursor.moveToNext())
        } else {
            Toast.makeText(this, "No expenses found", Toast.LENGTH_SHORT).show()
        }
        cursor.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, expensesList)
        lvExpenses.adapter = adapter
    }
}
