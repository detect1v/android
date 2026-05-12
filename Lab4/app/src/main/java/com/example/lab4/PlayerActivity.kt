package com.example.lab4

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class PlayerActivity : AppCompatActivity() {

    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var tvFileName: TextView
    private lateinit var btnPlay: Button
    private lateinit var btnPause: Button
    private lateinit var btnStop: Button
    private lateinit var btnDownload: Button

    private var playbackPosition = 0L
    private var playWhenReady = true
    private var mediaUri: String = ""
    private var mediaType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        playerView   = findViewById(R.id.playerView)
        tvFileName   = findViewById(R.id.tvFileName)
        btnPlay      = findViewById(R.id.btnPlay)
        btnPause     = findViewById(R.id.btnPause)
        btnStop      = findViewById(R.id.btnStop)
        btnDownload  = findViewById(R.id.btnDownload)

        mediaUri  = intent.getStringExtra("mediaUri") ?: ""
        mediaType = intent.getStringExtra("mediaType") ?: "video"

        tvFileName.text = when {
            mediaUri.startsWith("http") -> mediaUri
            else -> Uri.parse(mediaUri).lastPathSegment ?: mediaUri
        }

        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        val mediaItem = MediaItem.fromUri(Uri.parse(mediaUri))
        player.setMediaItem(mediaItem)
        player.prepare()

        player.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                Toast.makeText(
                    this@PlayerActivity,
                    "Помилка відтворення: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })

        btnPlay.setOnClickListener {
            player.play()
        }

        btnPause.setOnClickListener {
            if (player.isPlaying) {
                player.pause()
            } else {
                Toast.makeText(this, "Відтворення не активне", Toast.LENGTH_SHORT).show()
            }
        }

        btnStop.setOnClickListener {
            player.stop()
            player.seekTo(0)
        }

        btnDownload.setOnClickListener {
            if (mediaUri.startsWith("http")) {
                downloadFile(mediaUri, mediaType)
            } else {
                Toast.makeText(this, "Завантаження доступне лише для URL", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun downloadFile(url: String, type: String) {
        try {
            val fileName = url.substringAfterLast("/").let {
                if (it.contains(".")) it else if (type == "audio") "audio.mp3" else "video.mp4"
            }
            val request = DownloadManager.Request(Uri.parse(url)).apply {
                setTitle(fileName)
                setDescription("Завантаження медіафайлу...")
                setNotificationVisibility(
                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                )
                setDestinationInExternalPublicDir(
                    if (type == "audio") Environment.DIRECTORY_MUSIC
                    else Environment.DIRECTORY_MOVIES,
                    fileName
                )
                setAllowedOverMetered(true)
                setAllowedOverRoaming(true)
            }
            val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
            Toast.makeText(this, "Завантаження розпочато: $fileName", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Помилка: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onPause() {
        super.onPause()
        playbackPosition = player.currentPosition
        playWhenReady = player.playWhenReady
        player.pause()
    }

    override fun onResume() {
        super.onResume()
        player.seekTo(playbackPosition)
        player.playWhenReady = playWhenReady
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}