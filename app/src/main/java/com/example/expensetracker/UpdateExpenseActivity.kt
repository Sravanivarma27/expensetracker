package com.example.expensetracker

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UpdateExpenseActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var etExpenseName: EditText
    private lateinit var etAmount: EditText
    private lateinit var etCategory: EditText
    private lateinit var etDate: EditText
    private lateinit var etPaymentMethod: EditText
    private var expenseId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_expense)

        database = FirebaseDatabase.getInstance().reference.child("Expenses")
        etExpenseName = findViewById(R.id.etExpenseName)
        etAmount = findViewById(R.id.etAmount)
        etCategory = findViewById(R.id.etCategory)
        etDate = findViewById(R.id.etDate)
        etPaymentMethod = findViewById(R.id.etPaymentMethod)
        val btnUpdate: Button = findViewById(R.id.btnUpdate)
        val btnDelete: Button = findViewById(R.id.btnDelete)

        // Get the expense ID from the Intent
        expenseId = intent.getStringExtra("EXPENSE_ID")
        Log.d("UpdateExpenseActivity", "Expense ID received: $expenseId") // Log the received ID
        loadExpenseDetails()

        btnUpdate.setOnClickListener { updateExpense() }
        btnDelete.setOnClickListener { deleteExpense() }
    }

    private fun loadExpenseDetails() {
        expenseId?.let { id ->
            Log.d("UpdateExpenseActivity", "Loading expense with ID: $id")
            database.child(id).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    etExpenseName.setText(snapshot.child("expenseName").value.toString())
                    etAmount.setText(snapshot.child("amount").value.toString())
                    etCategory.setText(snapshot.child("category").value.toString())
                    etDate.setText(snapshot.child("date").value.toString())
                    etPaymentMethod.setText(snapshot.child("paymentMethod").value.toString())
                    Log.d("UpdateExpenseActivity", "Expense loaded: ${snapshot.value}")
                } else {
                    Toast.makeText(this, "Expense not found", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Log.e("UpdateExpenseActivity", "Failed to load expense: ${exception.message}")
                Toast.makeText(this, "Error loading expense", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "Invalid expense ID", Toast.LENGTH_SHORT).show()
            Log.e("UpdateExpenseActivity", "Expense ID is null or invalid")
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
            val updatedExpense = mapOf(
                "expenseName" to expenseName,
                "amount" to amount,
                "category" to category,
                "date" to date,
                "paymentMethod" to paymentMethod
            )

            expenseId?.let { id ->
                database.child(id).updateChildren(updatedExpense).addOnSuccessListener {
                    Toast.makeText(this, "Expense updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }.addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to update expense", Toast.LENGTH_SHORT).show()
                    Log.e("UpdateExpenseActivity", "Failed to update expense: ${exception.message}")
                }
            }
        }
    }

    private fun deleteExpense() {
        expenseId?.let { id ->
            database.child(id).removeValue().addOnSuccessListener {
                Toast.makeText(this, "Expense deleted successfully", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to delete expense", Toast.LENGTH_SHORT).show()
                Log.e("UpdateExpenseActivity", "Failed to delete expense: ${exception.message}")
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
