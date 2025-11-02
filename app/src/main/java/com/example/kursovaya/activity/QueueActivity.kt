package com.example.kursovaya.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kursovaya.R
import com.example.kursovaya.fragment.QueueFragment

class QueueActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_queue)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, QueueFragment())
                .commit()
        }
    }
}
