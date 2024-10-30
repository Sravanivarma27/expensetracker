package com.example.expensetracker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class ViewExpensesActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var lvExpenses: ListView
    private lateinit var expensesList: ArrayList<String>
    private lateinit var expenseIds: ArrayList<String> // Use String for Firebase keys

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_expenses)

        database = FirebaseDatabase.getInstance().reference.child("Expenses")
        lvExpenses = findViewById(R.id.listViewExpenses)
        expensesList = ArrayList()
        expenseIds = ArrayList()

        loadExpenses()

        lvExpenses.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val id = expenseIds[position]
            val intent = Intent(this, UpdateExpenseActivity::class.java)
            intent.putExtra("EXPENSE_ID", id)
            startActivity(intent)
        }
    }

    private fun loadExpenses() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                expensesList.clear()
                expenseIds.clear()

                if (snapshot.exists()) {
                    for (expenseSnapshot in snapshot.children) {
                        val expenseName = expenseSnapshot.child("expenseName").value.toString()
                        expensesList.add(expenseName)
                        expenseIds.add(expenseSnapshot.key.toString()) // Use Firebase key as ID
                    }
                } else {
                    Toast.makeText(this@ViewExpensesActivity, "No expenses found", Toast.LENGTH_SHORT).show()
                }

                val adapter = ArrayAdapter(this@ViewExpensesActivity, android.R.layout.simple_list_item_1, expensesList)
                lvExpenses.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ViewExpensesActivity", "Failed to load expenses: ${error.message}")
                Toast.makeText(this@ViewExpensesActivity, "Error loading expenses", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
