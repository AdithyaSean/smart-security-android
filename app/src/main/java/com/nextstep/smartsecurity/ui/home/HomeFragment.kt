package com.nextstep.smartsecurity.ui.home

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nextstep.smartsecurity.R

class HomeFragment : Fragment() {

    private lateinit var surfaceView1: SurfaceView
    private lateinit var surfaceView2: SurfaceView
    private lateinit var mediaPlayer1: MediaPlayer
    private lateinit var mediaPlayer2: MediaPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        surfaceView1 = root.findViewById(R.id.surface_view_1)
        surfaceView2 = root.findViewById(R.id.surface_view_2)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMediaPlayer(surfaceView1, "http://path.to/your/video1.mp4")
        setupMediaPlayer(surfaceView2, "http://path.to/your/video2.mp4")
    }

    private fun setupMediaPlayer(surfaceView: SurfaceView, videoUrl: String) {
        val mediaPlayer = MediaPlayer()
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                mediaPlayer.setDisplay(holder)
                mediaPlayer.setDataSource(videoUrl)
                mediaPlayer.prepareAsync()
                mediaPlayer.setOnPreparedListener { it.start() }
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                mediaPlayer.release()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer1.release()
        mediaPlayer2.release()
    }
}