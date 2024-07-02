package com.example.mystreammusic

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.bumptech.glide.Glide
import com.example.mystreammusic.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var exoPlayer: ExoPlayer

    private var playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            showGif(isPlaying)
        }
    }

    private fun showGif(show: Boolean) {
        if (show) {
            binding.playerSongGifImg.visibility = View.VISIBLE
        } else {
            binding.playerSongGifImg.visibility = View.INVISIBLE
        }
    }

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MyExoPlayer.getCurrentSong()?.apply {
            binding.playerSongTitleTxt.text = title
            binding.playerSongSubtitleTxt.text = subtitle
            Glide.with(binding.playerSongCoverImg)
                .load(coverUrl)
                .circleCrop()
                .into(binding.playerSongCoverImg)
            Glide.with(binding.playerSongGifImg)
                .load(R.drawable.media_playing)
                .circleCrop()
                .into(binding.playerSongGifImg)
            exoPlayer = MyExoPlayer.getInstance()!!
            binding.playerView.player = exoPlayer
            binding.playerView.showController()
            exoPlayer.addListener(playerListener)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.removeListener(playerListener)
    }
}