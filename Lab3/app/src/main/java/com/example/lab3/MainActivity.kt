package com.example.lab3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var inputFragment: InputFragment
    private lateinit var resultFragment: ResultFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputFragment = InputFragment()
        resultFragment = ResultFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentInput, inputFragment)
            .replace(R.id.fragmentResult, resultFragment)
            .commit()
    }

    fun showResult(language: String) {
        resultFragment.showResult(language)
    }

    fun resetInput() {
        inputFragment.resetSpinner()
    }
}