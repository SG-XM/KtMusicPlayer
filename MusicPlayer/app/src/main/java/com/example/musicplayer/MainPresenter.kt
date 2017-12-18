package com.example.musicplayer

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast

/**
 * Created by 上官轩明 on 2017/12/12.
 */
class MainPresenter(val mainView: MainActivityInterface, val context: Context, var mbinder: PlayerService.MyBinder) {
    val mediaMR = mbinder.getMediaMR()
    var mp = mbinder.getMp().apply {
        setOnCompletionListener {
            nextMusic()
        }
    }

    inner class ServiceBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.getStringExtra("action")) {
                "pre" -> mainView.clickButton(0)
                "next" -> mainView.clickButton(1)
                "play" -> mainView.clickButton(2)
            }
            Log.d("woggle", "受到广播" + intent?.getStringExtra("action"))
        }

    }

    fun regReceiver() {
        val intentfilter = IntentFilter()
        intentfilter.addAction("android.intent.action.Service")
        context.registerReceiver(this.ServiceBroadcastReceiver(), intentfilter)
    }

    fun unregReceiver() {
        context.unregisterReceiver(this.ServiceBroadcastReceiver())
    }

    fun play() {
        mbinder.playMusic()
        mainView.changePlayButtonSrc(R.drawable.vector_drawable_pause)
    }

    fun pause() {
        mbinder.pauseMusic()
        mainView.changePlayButtonSrc(R.drawable.vector_drawable_play)
    }

    fun nextMusic() {
        mbinder.nextMusic()
        play()
        changeMusicInfor(500)
    }

    fun preMusic() {
        mbinder.preMusic()
        play()
        changeMusicInfor(500)
    }

    fun changeMusicInfor(duration: Int) {
        mainView.changeMusicInfor(mediaMR.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE), mediaMR.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST), mediaMR.getEmbeddedPicture(), mp.duration, duration)
        mbinder.upDateNotificationInfo(mediaMR.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE), mediaMR.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST), mediaMR.getEmbeddedPicture())
    }




}