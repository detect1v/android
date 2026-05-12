package com.example.lab3

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HistoryActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listView: ListView
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: ArrayAdapter<String>
    private val dataList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        dbHelper = DatabaseHelper(this)
        listView = findViewById(R.id.listView)
        tvEmpty = findViewById(R.id.tvEmpty)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dataList)
        listView.adapter = adapter

        loadData()

        findViewById<Button>(R.id.btnClearAll).setOnClickListener {
            dbHelper.clearAll()
            dataList.clear()
            adapter.notifyDataSetChanged()
            checkEmpty()
            Toast.makeText(this, "Історію очищено", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadData() {
        dataList.clear()
        dataList.addAll(dbHelper.getAllLanguages())
        adapter.notifyDataSetChanged()
        checkEmpty()
    }

    private fun checkEmpty() {
        if (dataList.isEmpty()) {
            tvEmpty.visibility = TextView.VISIBLE
            listView.visibility = ListView.GONE
        } else {
            tvEmpty.visibility = TextView.GONE
            listView.visibility = ListView.VISIBLE
        }
    }
}