package com.example.morsandflash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView


class MainActivity : AppCompatActivity() {

    private lateinit var appname : TextView
    private lateinit var coder : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appname = findViewById(R.id.appname)
        coder = findViewById(R.id.coder)

        appname.animate()
            .translationY(1400f)
            .setDuration(2700)
            .setStartDelay(0)

        coder.animate()
            .translationY(-1400f)
            .setDuration(2700)
            .setStartDelay(0)

        val timer = object : CountDownTimer(2700,1000){
            override fun onTick(p0: Long) {}

            override fun onFinish() {
                val intent = Intent(this@MainActivity,TextActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
            timer.start()
    }
}