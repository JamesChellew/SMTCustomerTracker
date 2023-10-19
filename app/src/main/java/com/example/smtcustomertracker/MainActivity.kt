package com.example.smtcustomertracker

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Database starts fresh everytime the application is opened
        val db = DBHelper(this, null)
        db.resetDB()
        db.close()

        // Edit Text Regions
        val etName = findViewById<EditText>(R.id.editTextName)
        val etEmail = findViewById<EditText>(R.id.editTextEmail)
        val etMobile = findViewById<EditText>(R.id.editTextMobile)
        val btnAdd = findViewById<Button>(R.id.buttonAdd)
        val btnSearch = findViewById<Button>(R.id.buttonSearch)
        val btnDelete = findViewById<Button>(R.id.buttonDelete)
        val btnUpdate = findViewById<Button>(R.id.buttonUpdate)
        val btnReset = findViewById<Button>(R.id.buttonReset)

        // Adds the data from the edit texts into the database,
        // checking if the fields are empty first
        btnAdd.setOnClickListener {
            val db = DBHelper(this, null)
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val mobile = etMobile.text.toString()
            if (name == "" || email == "" || mobile == "") {
                Toast.makeText(this, "Please Enter All Fields", Toast.LENGTH_SHORT).show()
            } else {
                db.addCustomer(name, email, mobile)
                Toast.makeText(this, "$name was added to the database", Toast.LENGTH_SHORT).show()
                etName.text.clear()
                etEmail.text.clear()
                etMobile.text.clear()
                displayCustomers()
            }
        }
        // Uses a dialogBuilder to create a AlertDialog that allows name to be entered an searched
        btnSearch.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            val alertDialogView = this.layoutInflater.inflate(R.layout.search_dialog, null)
            dialogBuilder
                .setTitle("Search Customer")
                .setMessage("Enter name you want to search")
                .setView(alertDialogView)
                .setPositiveButton("Search") { dialog, which ->
                    val etSearchName = alertDialogView.findViewById<EditText>(R.id.editTextName)
                    val searchName = etSearchName.text.toString()

                    if (searchName == ""){
                        Snackbar.make(it, "Please enter a search term", Snackbar.LENGTH_LONG).show()
                        return@setPositiveButton
                    }

                    val cursor = db.searchCustomer(searchName)
                    var searchResults = ""
                    while (cursor!!.moveToNext()) {
                        searchResults += "${cursor.getString(0)} | " +
                                "${cursor.getString(1)} | " +
                                "${cursor.getString(2)} | " +
                                "${cursor.getString(3)}\n"
                    }
                    if (searchResults != "") {
                        // Generates a dialog to display search results
                        val resultsDialogBuilder = AlertDialog.Builder(this)
                        val resultsDialogView =
                            this.layoutInflater.inflate(R.layout.search_results_dialog, null)
                        var tvResults =
                            resultsDialogView.findViewById<TextView>(R.id.textViewSearchResults)
                        tvResults.append(searchResults)
                        resultsDialogBuilder.setView(resultsDialogView)
                            .setTitle("Search Results")
                            .show()
                    } else {
                        Snackbar.make(it, "No results found", Snackbar.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
        // shows alertdialog that allows id to be entered to be deleted
        btnDelete.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            val dialogView = this.layoutInflater.inflate(R.layout.delete_dialog, null)

            dialogBuilder.setTitle("Delete Customer")
            dialogBuilder.setMessage("Enter ID to delete")
            dialogBuilder.setView(dialogView)

            dialogBuilder.setPositiveButton("Confirm") { dialog, which ->
                val etDelete = dialogView.findViewById<EditText>(R.id.editTextDelete)
                val deleteId = etDelete.text.toString()
                val rows = db.deleteCustomer(deleteId)
                displayCustomers()
                if (rows <= 0) {
                    Toast.makeText(this, "Please enter a valid ID", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "ID: $deleteId Deleted", Toast.LENGTH_SHORT).show()
                }
            }
            dialogBuilder.setNegativeButton("Cancel", null)
            dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert)
            dialogBuilder.show()
        }
        // shows dialog that allows customer information to be edited at specified ID
        btnUpdate.setOnClickListener {
            updateDialog()
        }
        // Drops then remakes the database. Can only be executed if no. of records exceeds 5
        // SQL rawQuery returns a single column with a single record, the count of records
        // Uses Cursor to read that number and check if the no of records exceeds 5
        btnReset.setOnClickListener {
            val db = DBHelper(this, null)
            val cursor = db.checkNumberOfRecords()
            var counter = 0
            cursor!!.moveToNext()
            counter = cursor.getInt(0)
            if (counter < 5) {
                Toast.makeText(
                    this,
                    "$counter Add more records before resetting",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                db.resetDB()
                displayCustomers()
                Toast.makeText(this, "$counter Database Reset", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayCustomers() {
        // Uses the Cursor to read each column of the record and appends the info to the TextView
        val tvRecord = findViewById<TextView>(R.id.textViewScoreRecord)
        val db = DBHelper(this, null)
        val cursor = db.getAllCustomers()

        tvRecord.text = "Customers:\nId | Name | Email | Mobile\n"
        while (cursor!!.moveToNext()) {
            tvRecord.append(
                "${cursor.getString(0)} | " +
                        "${cursor.getString(1)} | " +
                        "${cursor.getString(2)} | " +
                        "${cursor.getString(3)}\n"
            )
        }
    }

    private fun updateDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val dialogView = this.layoutInflater.inflate(R.layout.update_dialog, null)

        dialogBuilder
            .setTitle("Edit Customer")
            .setMessage("Enter the ID of the customer you would like to edit")
            .setView(dialogView)
            .setPositiveButton("Update") { dialog, which ->
                val db = DBHelper(this, null)

                val etUpdateId = dialogView.findViewById<EditText>(R.id.editTextId)
                val etUpdateName = dialogView.findViewById<EditText>(R.id.editTextName)
                val etUpdateEmail = dialogView.findViewById<EditText>(R.id.editTextEmail)
                val etUpdateMobile = dialogView.findViewById<EditText>(R.id.editTextMobile)

                val updateId = etUpdateId.text.toString()
                val updateName = etUpdateName.text.toString()
                val updateEmail = etUpdateEmail.text.toString()
                val updateMobile = etUpdateMobile.text.toString()

                if (updateId != "" && updateName != "" && updateEmail != "" && updateMobile != "") {
                    val rows = db.updateCustomer(updateId, updateName, updateEmail, updateMobile)
                    if (rows == 0) {
                        Toast.makeText(this, "Please enter a valid Id", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Customer details updated", Toast.LENGTH_SHORT).show()
                    }
                    displayCustomers()
                } else {
                    val snackBarRetry = Snackbar.make(
                        findViewById(R.id.main),
                        "Please enter all fields",
                        Snackbar.LENGTH_LONG
                    )
                    snackBarRetry.setAction("Retry") {
                        updateDialog() // Recalls the dialog to try again.
                    }.show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}