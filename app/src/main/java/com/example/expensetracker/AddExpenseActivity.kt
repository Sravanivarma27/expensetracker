package com.example.expensetracker

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var etExpenseName: EditText
    private lateinit var etAmount: EditText
    private lateinit var etCategory: EditText
    private lateinit var etDate: EditText
    private lateinit var etPaymentMethod: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        dbHelper = DatabaseHelper(this)
        etExpenseName = findViewById(R.id.etExpenseName)
        etAmount = findViewById(R.id.etAmount) // Ensure this ID matches your XML
        etCategory = findViewById(R.id.etCategory)
        etDate = findViewById(R.id.etDate)
        etPaymentMethod = findViewById(R.id.etPaymentMethod)
        val btnAdd: Button = findViewById(R.id.btnAdd)

        btnAdd.setOnClickListener { addExpense() }
    }

    private fun addExpense() {
        val expenseName = etExpenseName.text.toString().trim()
        val amountString = etAmount.text.toString().trim()
        val category = etCategory.text.toString().trim()
        val date = etDate.text.toString().trim()
        val paymentMethod = etPaymentMethod.text.toString().trim()

        if (validateInputs(expenseName, amountString, category, date, paymentMethod)) {
            val amount = amountString.toDouble()
            val result = dbHelper.insertExpense(expenseName, amount, category, date, paymentMethod)
            if (result != -1L) {
                Toast.makeText(this, "Expense added successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to add expense", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInputs(expenseName: String, amountString: String, category: String, date: String, paymentMethod: String): Boolean {
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
