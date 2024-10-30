package com.example.expensetracker

import Expense
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var etExpenseName: EditText
    private lateinit var etAmount: EditText
    private lateinit var etCategory: EditText
    private lateinit var etDate: EditText
    private lateinit var etPaymentMethod: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference.child("Expenses")

        etExpenseName = findViewById(R.id.etExpenseName)
        etAmount = findViewById(R.id.etAmount)
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

            // Create a unique ID for each expense entry
            val expenseId = database.push().key
            if (expenseId == null) {
                Toast.makeText(this, "Failed to generate expense ID", Toast.LENGTH_SHORT).show()
                Log.e("AddExpenseActivity", "Expense ID generation failed")
                return
            }

            // Create an Expense object
            val expense = Expense(expenseId, expenseName, amount, category, date, paymentMethod)

            // Log the expense object for debugging
            Log.d("AddExpenseActivity", "Adding Expense: $expense")

            // Save the expense to Firebase
            database.child(expenseId).setValue(expense)
                .addOnSuccessListener {
                    Toast.makeText(this, "Expense added successfully", Toast.LENGTH_SHORT).show()
                    Log.d("AddExpenseActivity", "Expense added successfully")

                    // Finish the activity to go back to the previous screen
                    finish()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to add expense", Toast.LENGTH_SHORT).show()
                    Log.e("AddExpenseActivity", "Failed to add expense: ${exception.message}")
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
