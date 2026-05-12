package com.example.lab3

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment

class InputFragment : Fragment() {

    private lateinit var spinner: Spinner
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spinner = view.findViewById(R.id.spinnerLanguages)
        dbHelper = DatabaseHelper(requireContext())

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.programming_languages,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinner.adapter = adapter

        view.findViewById<Button>(R.id.btnOk).setOnClickListener {
            if (spinner.selectedItemPosition == 0) {
                Toast.makeText(
                    requireContext(),
                    "Будь ласка, оберіть мову програмування зі списку!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val selected = spinner.selectedItem.toString()
                val success = dbHelper.insertLanguage(selected)
                if (success) {
                    Toast.makeText(
                        requireContext(),
                        "Збережено: $selected",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Помилка збереження!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                (activity as MainActivity).showResult(selected)
            }
        }

        view.findViewById<Button>(R.id.btnOpen).setOnClickListener {
            val intent = Intent(requireContext(), HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    fun resetSpinner() {
        spinner.setSelection(0)
    }
}