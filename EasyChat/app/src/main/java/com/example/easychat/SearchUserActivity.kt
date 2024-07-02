package com.example.easychat

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.easychat.adapter.SearchUserRecyclerAdapter
import com.example.easychat.utils.AndroidUtil
import com.example.easychat.utils.FirebaseUtil
import com.google.firebase.FirebaseOptions


class SearchUserActivity : AppCompatActivity() {

    private lateinit var searchInput:EditText
    private lateinit var searchBtn:ImageButton
    private lateinit var backBtn:ImageButton
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_user)

        searchInput = findViewById(R.id.search_username_input)
        searchBtn = findViewById(R.id.search_user_btn)
        backBtn = findViewById(R.id.search_user_btn)
        recyclerView = findViewById(R.id.search_user_recycler_view)

        searchInput.requestFocus()

        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }



        searchInput.setOnClickListener {
            val searchTerm = searchInput.text.toString().trim()
            if(searchTerm.isEmpty()||searchTerm.length<3){
                searchInput.error = "Invalid Username."
                return@setOnClickListener
            }
            setupSearchRecyclerView(searchTerm)
        }
    }

    private fun setupSearchRecyclerView(searchTerm: String) {

        val query = FirebaseUtil.allUserCollectionReference()
            .whereGreaterThanOrEqualTo("username", searchTerm)
            .whereLessThanOrEqualTo("username", "$(searchTerm)utf8ff")



        val adapter = SearchUserRecyclerAdapter(applicationContext)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

}