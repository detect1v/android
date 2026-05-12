package com.example.lab3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class ResultFragment : Fragment() {

    private lateinit var tvResult: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvResult = view.findViewById(R.id.tvResult)

        view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            tvResult.text = "Тут з'явиться обрана мова..."
            tvResult.setTextColor(0xFFBDBDBD.toInt())
            (activity as MainActivity).resetInput()
        }
    }

    fun showResult(language: String) {
        tvResult.text = "Обрано: $language"
        tvResult.setTextColor(0xFF212121.toInt())
    }
}