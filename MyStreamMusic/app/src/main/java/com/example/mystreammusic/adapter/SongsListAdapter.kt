package com.example.mystreammusic.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.mystreammusic.MyExoPlayer
import com.example.mystreammusic.PlayerActivity
import com.example.mystreammusic.databinding.SongListItemRowBinding
import com.example.mystreammusic.models.SongModel
import com.google.firebase.firestore.FirebaseFirestore

class SongsListAdapter(private val songIdList: List<String>) :
    RecyclerView.Adapter<SongsListAdapter.MyViewHolder>() {

    class MyViewHolder(private val binding: SongListItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(songId: String) {
            FirebaseFirestore.getInstance().collection("songs")
                .document(songId)
                .get()
                .addOnSuccessListener {
                    val song = it.toObject(SongModel::class.java)
                    song?.apply {
                        binding.songTitleTextView.text = title
                        binding.songSubtitleTextView.text = subtitle
                        Glide.with(binding.songCoverImageView)
                            .load(coverUrl)
                            .apply(
                                RequestOptions().transform(RoundedCorners(32))
                            )
                            .into(binding.songCoverImageView)
                        val context = binding.root.context
                        binding.root.setOnClickListener {
                            MyExoPlayer.startPlaying(context, song)
                            context.startActivity(Intent(context, PlayerActivity::class.java))
                        }
                    }
                }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            SongListItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return songIdList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindData(songIdList[position])
    }
}