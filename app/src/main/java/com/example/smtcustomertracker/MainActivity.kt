package com.example.smtcustomertracker

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Edit Text Regions
        val etName = findViewById<EditText>(R.id.editTextName)
        val etEmail = findViewById<EditText>(R.id.editTextEmail)
        val etMobile = findViewById<EditText>(R.id.editTextMobile)
        val btnAdd = findViewById<Button>(R.id.buttonAdd)
        val btnDelete = findViewById<Button>(R.id.buttonDelete)
        val btnUpdate = findViewById<Button>(R.id.buttonUpdate)
        val btnReset = findViewById<Button>(R.id.buttonReset)

        btnAdd.setOnClickListener {
            val db = DBHelper(this, null)
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val mobile = etMobile.text.toString()
            db.addCustomer(name, email, mobile)
            Toast.makeText(this, "$name was added to the database", Toast.LENGTH_SHORT).show()
            etName.text.clear()
            etEmail.text.clear()
            etMobile.text.clear()
            displayCustomers()
        }

        btnDelete.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            val dialogView = this.layoutInflater.inflate(R.layout.delete_dialog, null)
            dialogBuilder.setTitle("Delete User")
            dialogBuilder.setMessage("Enter ID to delete")
            dialogBuilder.setView(dialogView)
            dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert)
            dialogBuilder.setPositiveButton("Confirm", DialogInterface.OnClickListener { dialog, id ->
                val db = DBHelper(this, null)
                val etDelete = findViewById<EditText>(R.id.editTextDelete)
                val deleteId = etDelete.text.toString().toIntOrNull() ?: -1
                if (deleteId != -1){
                    db.deleteCustomer(deleteId)
                }
                else{
                    Toast.makeText(this, "Please enter a valid ID", Toast.LENGTH_LONG).show()
                }
            })
            dialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->

            })
            dialogBuilder.show()
        }
        btnUpdate.setOnClickListener {

            val db = DBHelper(this, null)
            val subject = etName.text.toString()
            val score = etEmail.text.toString()
            Toast.makeText(this, "rows affected by changes", Toast.LENGTH_SHORT).show()
            displayCustomers()
        }

        btnReset.setOnClickListener {
            val db = DBHelper(this, null)
            db.resetDB()
            displayCustomers()
            Toast.makeText(this, "Database Reset", Toast.LENGTH_SHORT).show()
        }

    }

    private fun displayCustomers() {
        val tvRecord = findViewById<TextView>(R.id.textViewScoreRecord)
        val db = DBHelper(this, null)
        val cursor = db.getAllCustomers()

        tvRecord.text = "Customers:\nId | Name | Email | Mobile\n"
        if (cursor!!.moveToNext()) {
            tvRecord.append("${cursor.getString(0)} | ${cursor.getString(1)} | ${cursor.getString(2)} | ${cursor.getString(3)}")
        }
        while (cursor.moveToNext()) {
            tvRecord.append("${cursor.getString(0)} | ${cursor.getString(1)} | ${cursor.getString(2)} | ${cursor.getString(3)}")
        }
    }
}