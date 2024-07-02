package com.example.easychat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.easychat.utils.FirebaseUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var searchBtn:ImageButton



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val chatFragment = ChatFragment()
        val profileFragment = ProfileFragment()

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        searchBtn = findViewById(R.id.main_search_btn)

        searchBtn.setOnClickListener {
            startActivity(Intent(this, SearchUserActivity::class.java))
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.menu_chat -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, chatFragment).commit()
                }
                R.id.menu_profile -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, profileFragment).commit()
                }
            }
            true
        }

        bottomNavigationView.selectedItemId = R.id.menu_chat

        getFCMToken()
    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {task ->
            if(task.isSuccessful){
                val token = task.result

            }
        }
    }
}