package com.example.familymap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentFrameLayout)

        if (currentFragment == null) {
            val fragment = LoginFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentFrameLayout, fragment)
                .commit()
        }

    }
}



