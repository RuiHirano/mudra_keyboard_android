package com.example.mudrakeyboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    var testValue: Int = 5
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // ボタンを設定
        val button1 = findViewById<Button>(R.id.button1) as Button
        val button2 = findViewById<Button>(R.id.button2) as Button
        val button3 = findViewById<Button>(R.id.button3) as Button
        // TextView の設定
        val textView = findViewById<TextView>(R.id.text) as TextView
        button1.setOnClickListener {
            textView.setText("pushed Button1!")
        }
        button2.setOnClickListener {
            textView.setText("pushed Button2!")
        }
        button3.setOnClickListener {
            textView.setText("pushed Button3!")
        }

        test()
    }

    fun test(){
        Log.d("MainActivity", "現在のカウント値: ${testValue}")
    }
}
