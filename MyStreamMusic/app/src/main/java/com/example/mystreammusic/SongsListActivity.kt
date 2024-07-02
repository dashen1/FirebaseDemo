package com.example.mystreammusic

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.mystreammusic.adapter.SongsListAdapter
import com.example.mystreammusic.databinding.ActivitySongsListBinding
import com.example.mystreammusic.models.CategoryModel

class SongsListActivity : AppCompatActivity() {


    companion object{
        lateinit var category: CategoryModel
    }

    private lateinit var binding:ActivitySongsListBinding
    private lateinit var songsListAdapter: SongsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.songsListNameTxt.text = category.name
        Glide.with(binding.songsListCoverImg)
            .load(category.coverUrl)
            .apply(
                RequestOptions().transform(RoundedCorners(32))
            )
            .into(binding.songsListCoverImg)

        setupSongsListRecyclerView()
    }

    private fun setupSongsListRecyclerView() {
        songsListAdapter = SongsListAdapter(category.songs)
        binding.songsListRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.songsListRecyclerView.adapter = songsListAdapter
    }
}