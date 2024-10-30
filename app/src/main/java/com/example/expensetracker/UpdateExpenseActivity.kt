package com.example.expensetracker

import android.database.Cursor
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class UpdateExpenseActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var etExpenseName: EditText
    private lateinit var etAmount: EditText
    private lateinit var etCategory: EditText
    private lateinit var etDate: EditText
    private lateinit var etPaymentMethod: EditText
    private var expenseId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_expense)

        dbHelper = DatabaseHelper(this)
        etExpenseName = findViewById(R.id.etExpenseName)
        etAmount = findViewById(R.id.etAmount)
        etCategory = findViewById(R.id.etCategory)
        etDate = findViewById(R.id.etDate)
        etPaymentMethod = findViewById(R.id.etPaymentMethod)
        val btnUpdate: Button = findViewById(R.id.btnUpdate)
        val btnDelete: Button = findViewById(R.id.btnDelete)

        // Get the expense ID from the Intent
        expenseId = intent.getIntExtra("EXPENSE_ID", -1)
        loadExpenseDetails()

        btnUpdate.setOnClickListener { updateExpense() }
        btnDelete.setOnClickListener { deleteExpense() }
    }

    private fun loadExpenseDetails() {
        expenseId?.let {
            val cursor: Cursor = dbHelper.getExpense(it)
            if (cursor.moveToFirst()) {
                etExpenseName.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EXPENSE_NAME)))
                etAmount.setText(
                    cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT)).toString()
                )
                etCategory.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY)))
                etDate.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE)))
                etPaymentMethod.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PAYMENT_METHOD)))
            }
            cursor.close()
        }
    }

    private fun updateExpense() {
        val expenseName = etExpenseName.text.toString().trim()
        val amountString = etAmount.text.toString().trim()
        val category = etCategory.text.toString().trim()
        val date = etDate.text.toString().trim()
        val paymentMethod = etPaymentMethod.text.toString().trim()

        if (validateInputs(expenseName, amountString, category, date, paymentMethod)) {
            val amount = amountString.toDouble()
            if (expenseId != null) {
                val result = dbHelper.updateExpense(
                    expenseId!!,
                    expenseName,
                    amount,
                    category,
                    date,
                    paymentMethod
                )
                if (result > 0) {
                    Toast.makeText(this, "Expense updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to update expense", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteExpense() {
        expenseId?.let {
            val result = dbHelper.deleteExpense(it)
            if (result > 0) {
                Toast.makeText(this, "Expense deleted successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to delete expense", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInputs(
        expenseName: String,
        amountString: String,
        category: String,
        date: String,
        paymentMethod: String
    ): Boolean {
        if (expenseName.isEmpty() || amountString.isEmpty() || category.isEmpty() || date.isEmpty() || paymentMethod.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return false
        }

        val amount = amountString.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}