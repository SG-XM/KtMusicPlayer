package com.example.musicplayer

import android.app.Notification
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.Service
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.RemoteViews

/**
 * Created by 上官轩明 on 2017/12/12.
 */
class PlayerService : Service() {

    val mbinder = MyBinder()
    var index = 0
    var dirList = mutableListOf(R.raw.music, R.raw.lorde_green_light, R.raw.lorde_liability)
    val mediaMR = MediaMetadataRetriever()
    lateinit var mp: MediaPlayer
    lateinit var notify: Notification

    override fun onCreate() {
        super.onCreate()
        mp = MediaPlayer.create(this, dirList[index])
        mediaMR.setDataSource(this, Uri.parse("android.resource://" + this@PlayerService.getPackageName() + "/" + dirList[0]))
        mbinder.setForeService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("woggle", "onstart")
        return START_STICKY
    }

    //    override fun onBind(intent: Intent?): IBinder{
//        Log.d("woggle","onBind  "+mbinder.toString())
//        return mbinder
//    }
    override fun onBind(intent: Intent?): IBinder {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return mbinder

    }


    inner class MyBinder : Binder() {
        fun getMp() = mp
        fun getMediaMR() = mediaMR

        fun playMusic() {
            mp.start()
            notify.contentView.setImageViewBitmap(R.id.notify_button_play, if (mp.isPlaying) BitmapFactory.decodeResource(resources, R.drawable.no_pause) else BitmapFactory.decodeResource(resources, R.drawable.no_play))
            startForeground(1, notify)
            Log.d("woggle", "mp.start")

        }

        fun pauseMusic() {
            if (mp.isPlaying) {
                mp.pause()
                notify.contentView.setImageViewBitmap(R.id.notify_button_play, if (mp.isPlaying) BitmapFactory.decodeResource(resources, R.drawable.no_pause) else BitmapFactory.decodeResource(resources, R.drawable.no_play))
                startForeground(1, notify)
            }
        }

        fun nextMusic() {
            index = (index + 1) % 3
            mp.stop()
            mp.reset()
            mediaMR.setDataSource(this@PlayerService, Uri.parse("android.resource://" + this@PlayerService.getPackageName() + "/" + dirList[index]))
            mp.setDataSource(this@PlayerService, Uri.parse("android.resource://" + this@PlayerService.getPackageName() + "/" + dirList[index]))
            mp.prepare()

        }

        fun preMusic() {
            index = (index + 2) % 3
            mp.stop()
            mp.reset()
            mediaMR.setDataSource(this@PlayerService, Uri.parse("android.resource://" + this@PlayerService.getPackageName() + "/" + dirList[index]))
            mp.setDataSource(this@PlayerService, Uri.parse("android.resource://" + this@PlayerService.getPackageName() + "/" + dirList[index]))
            mp.prepare()

        }

        fun upDateNotificationInfo(song_name: String, song_artist: String, byteArray: ByteArray) {
            notify.contentView.setTextViewText(R.id.notify_title, song_name)
            notify.contentView.setTextViewText(R.id.notify_artist, song_artist)
            notify.contentView.setImageViewBitmap(R.id.notify_icon, BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size))
            //notify.contentView.setImageViewBitmap(R.id.notify_button_play, BitmapFactory.decodeResource(resources, R.drawable.vector_drawable_notify_pause))
            notify.contentView.setImageViewBitmap(R.id.notify_button_play, if (mp.isPlaying) BitmapFactory.decodeResource(resources, R.drawable.no_pause) else BitmapFactory.decodeResource(resources, R.drawable.no_play))
            // notify.contentView.set
            startForeground(1, notify)
            Log.d("woggle", "updateNotification")


        }

        fun setForeService() {
            //巨大的无敌的大坑，一定要好好学PendingIntent
            //pending flgs 和 requestcode一定要设置！！！！

            val intent_Main = Intent(this@PlayerService, MainActivity::class.java)
            val intent_pre = Intent("android.intent.action.Service").putExtra("action", "pre")
            val intent_next = Intent("android.intent.action.Service").putExtra("action", "next")
            val intent_play = Intent("android.intent.action.Service").putExtra("action", "play")
            val pi = PendingIntent.getActivity(this@PlayerService, 0, intent_Main, 0)
            val pi_pre = PendingIntent.getBroadcast(this@PlayerService, 1, intent_pre, FLAG_UPDATE_CURRENT)
            val pi_next = PendingIntent.getBroadcast(this@PlayerService, 2, intent_next, FLAG_UPDATE_CURRENT)
            val pi_play = PendingIntent.getBroadcast(this@PlayerService, 3, intent_play, FLAG_UPDATE_CURRENT)

            val remoteViews = RemoteViews(packageName, R.layout.notification_layout)
            remoteViews.setOnClickPendingIntent(R.id.notify_button_pre, pi_pre)
            remoteViews.setOnClickPendingIntent(R.id.notify_button_next, pi_next)
            remoteViews.setOnClickPendingIntent(R.id.notify_button_play, pi_play)



            notify = NotificationCompat
                    .Builder(this@PlayerService, "channel_1")
                    .setContent(remoteViews)
                    //.setCustomContentView(remoteViews)
                    .setContentTitle("MusicPlayer")
                    .setContentText("暂无歌曲")
                    .setContentIntent(pi)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .build()

            startForeground(1, notify)
        }
    }
}