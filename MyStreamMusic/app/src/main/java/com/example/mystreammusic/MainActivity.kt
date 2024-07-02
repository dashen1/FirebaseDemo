package com.example.mystreammusic

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.MenuProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mystreammusic.adapter.CategoryAdapter
import com.example.mystreammusic.adapter.SectionSongListAdapter
import com.example.mystreammusic.databinding.ActivityMainBinding
import com.example.mystreammusic.models.CategoryModel
import com.example.mystreammusic.models.SongModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObjects

class MainActivity : AppCompatActivity() {

    private lateinit var mainBing: ActivityMainBinding
    private lateinit var categoryAdapter: CategoryAdapter


    private fun logout() {
        MyExoPlayer.getInstance()?.release()
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBing = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBing.root)

        getCategories()
        setupSection(
            "section_1",
            mainBing.mainSection1Layout,
            mainBing.mainSection1Title,
            mainBing.mainSection1RecyclerView
        )
        setupSection(
            "section_2",
            mainBing.mainSection2Layout,
            mainBing.mainSection2Title,
            mainBing.mainSection2RecyclerView
        )
        setupSection(
            "section_3",
            mainBing.mainSection3Layout,
            mainBing.mainSection3Title,
            mainBing.mainSection3RecyclerView
        )
        setupMostlyPlayed(
            "mostly_played",
            mainBing.mainMostPlayLayout,
            mainBing.mainMostPlayTitle,
            mainBing.mainMostPlayRecyclerView
        )

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                logout()
                true
            }

            else -> false
        }
    }

    private fun setupMostlyPlayed(
        id: String,
        mainLayout: ConstraintLayout,
        titleView: TextView,
        recyclerView: RecyclerView
    ) {
        FirebaseFirestore.getInstance().collection("sections")
            .document(id)
            .get()
            .addOnSuccessListener {
                // Get most played songs.
                FirebaseFirestore.getInstance().collection("songs")
                    .orderBy("count", Query.Direction.DESCENDING)
                    .limit(5)
                    .get()
                    .addOnSuccessListener { songListSnapshot ->
                        val songsModelList = songListSnapshot.toObjects<SongModel>()
                        val songsIdList = songsModelList.map {
                            it.id
                        }.toList()
                        val section = it.toObject(CategoryModel::class.java)
                        section?.apply {
                            section.songs = songsIdList
                            mainLayout.visibility = View.VISIBLE
                            titleView.text = name
                            recyclerView.layoutManager = LinearLayoutManager(
                                this@MainActivity,
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                            recyclerView.adapter = SectionSongListAdapter(songs)
                            mainLayout.setOnClickListener {
                                SongsListActivity.category = section
                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        SongsListActivity::class.java
                                    )
                                )
                            }
                        }
                    }
            }
    }

    private fun setupSection(
        id: String,
        mainLayout: ConstraintLayout,
        titleView: TextView,
        recyclerView: RecyclerView
    ) {
        FirebaseFirestore.getInstance().collection("sections")
            .document(id)
            .get()
            .addOnSuccessListener {
                val section = it.toObject(CategoryModel::class.java)
                section?.apply {
                    mainLayout.visibility = View.VISIBLE
                    titleView.text = name
                    recyclerView.layoutManager = LinearLayoutManager(
                        this@MainActivity,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    recyclerView.adapter = SectionSongListAdapter(songs)
                    mainLayout.setOnClickListener {
                        SongsListActivity.category = section
                        startActivity(Intent(this@MainActivity, SongsListActivity::class.java))
                    }
                }
            }
    }

    private fun getCategories() {
        FirebaseFirestore.getInstance().collection("category")
            .get()
            .addOnSuccessListener {
                Log.d("MainActivity", "getCategories: ${it.documents.size}")
                val categoryList = it.toObjects(CategoryModel::class.java)
                Log.d("MainActivity", "categoryList: $categoryList")
                setupCategoryRecyclerView(categoryList)
            }
    }

    private fun setupCategoryRecyclerView(categoryList: List<CategoryModel>) {
        categoryAdapter = CategoryAdapter(categoryList)
        mainBing.categoriesRecyclerView.layoutManager =
            LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
        mainBing.categoriesRecyclerView.adapter = categoryAdapter
    }
}