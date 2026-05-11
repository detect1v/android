package com.example.languagepicker

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var spinner: Spinner
    private lateinit var btnOk: Button
    private lateinit var btnCancel: Button
    private lateinit var tvResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinner   = findViewById(R.id.spinnerLanguages)
        btnOk     = findViewById(R.id.btnOk)
        btnCancel = findViewById(R.id.btnCancel)
        tvResult  = findViewById(R.id.tvResult)

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.programming_languages,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinner.adapter = adapter

        btnOk.setOnClickListener {
            if (spinner.selectedItemPosition == 0) {
                Toast.makeText(
                    this,
                    "Будь ласка, оберіть мову програмування зі списку!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val selected = spinner.selectedItem.toString()
                tvResult.text = "Обрано: $selected"
                tvResult.setTextColor(getColor(android.R.color.black))
            }
        }

        btnCancel.setOnClickListener {
            spinner.setSelection(0)
            tvResult.text = "Тут з'явиться обрана мова..."
            tvResult.setTextColor(0xFFBDBDBD.toInt())
            Toast.makeText(this, "Поле очищено", Toast.LENGTH_SHORT).show()
        }
    }
}