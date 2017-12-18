package com.example.musicplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener, View.OnClickListener, MainActivityInterface {


    lateinit var seekbar: SeekBar
    lateinit var ImagineButton_play: ImageButton
    lateinit var ImagineButton_next: ImageButton
    lateinit var ImagineButton_previous: ImageButton
    lateinit var ImagineButton_like: ImageButton
    lateinit var imv_song: ImageView
    lateinit var tv_curtime: TextView
    lateinit var Mtv_endtime: MyTextView
    lateinit var Mtv_song_title: MyTextView
    lateinit var Mtv_song_artist: MyTextView
    lateinit var intent_Service: Intent
    lateinit var serviceConnection: ServiceConnection
    lateinit var mPresenter: MainPresenter
    lateinit var handler: Handler
    lateinit var runnable: Runnable
    val simpletime = SimpleDateFormat("mm:ss")
    private var isLike = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inite()
    }

    private fun inite() {

        serviceConnection = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
                 }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                mPresenter = MainPresenter(this@MainActivity, this@MainActivity, service as PlayerService.MyBinder)
                mPresenter.changeMusicInfor(0)
                mPresenter.regReceiver()
                handler.post(runnable)

            }

        }
        //window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)//无状态栏
        intent_Service = Intent(this, PlayerService::class.java)
        bindService(intent_Service, serviceConnection, Context.BIND_AUTO_CREATE)
        seekbar = findViewById(R.id.seekbar)
        ImagineButton_next = findViewById(R.id.button_next)
        ImagineButton_play = findViewById(R.id.button_play)
        ImagineButton_previous = findViewById(R.id.button_previous)
        ImagineButton_like = findViewById(R.id.button_like)
        imv_song = findViewById(R.id.imv_song)
        tv_curtime = findViewById(R.id.tv_curtime)
        Mtv_endtime = findViewById(R.id.tv_wholetime)
        Mtv_song_artist = findViewById(R.id.tv_artist)
        Mtv_song_title = findViewById(R.id.tv_songtitle)
///////////////////////////////现在都可以通过xml中的id直接设置Listenser，因为kotlin还是AS特性////////////////////////////
        val myGrad = button_play.getBackground() as RippleDrawable
        myGrad.setColor(ColorStateList.valueOf(Color.rgb(22, 22, 55)))
        Mtv_song_title.setTypeface(Typeface.createFromAsset(assets, "Futura LT Medium.ttf"))
        Mtv_song_title.setTextColor(ColorStateList.valueOf(Color.rgb(0, 0, 0)))
        Mtv_song_artist.setTypeface(Typeface.createFromAsset(assets, "Futura LT Medium.ttf"))
        Mtv_endtime.setTypeface(Typeface.createFromAsset(assets, "Futura LT Medium.ttf"))
        tv_curtime.setTypeface(Typeface.createFromAsset(assets, "Futura LT Medium.ttf"))
        button_like.setOnClickListener(this)
        button_play.setOnClickListener(this)
        button_next.setOnClickListener(this)
        button_previous.setOnClickListener(this)
        seekbar.setOnSeekBarChangeListener(this)

///////////////////////////////现在都可以通过xml中的id直接设置Listenser，因为kotlin还是AS特性////////////////////////////


////////////////////////////////////////////////////更新seekbarUI///////////////////////////////////////////////////////
        handler = Handler()
        runnable = object : Runnable {
            override fun run() {
                //Log.d("woggle", "run")
                tv_curtime.setText(simpletime.format(mPresenter.mp.currentPosition))
                seekbar.setProgress(mPresenter.mp.currentPosition)
                handler.postDelayed(this, 1000)
            }
        }
////////////////////////////////////////////////////更新seekbarUI///////////////////////////////////////////////////////

    }


    /////////////////////////////////////////////////实现各种接口方法///////////////////////////////////////////////////////

    override fun onClick(v: View) {
        when (v.id) {
            ImagineButton_play.id -> {
                if (mPresenter.mp.isPlaying) {
                    mPresenter.pause()
                    handler.removeCallbacks(runnable)
                } else if (!mPresenter.mp.isPlaying) {
                    handler.post(runnable)
                    mPresenter.play()

                }
            }
            ImagineButton_like.id -> {
                changeLikeButtonSrc(if (isLike) R.drawable.vector_drawable_like_block else R.drawable.ic_like_solid)
                isLike = !isLike
            }
            ImagineButton_next.id -> mPresenter.nextMusic()
            ImagineButton_previous.id -> mPresenter.preMusic()
        }
    }


    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        Log.d("woggle", "remove post")
        handler.removeCallbacks(runnable)

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        mPresenter.mp.seekTo(seekbar.progress)
        if (mPresenter.mp.isPlaying) {
            handler.post(runnable)
        } else {
            tv_curtime.setText(simpletime.format(mPresenter.mp.currentPosition))
            seekbar.setProgress(mPresenter.mp.currentPosition)
        }


    }

    override fun clickButton(id: Int) {
        when (id) {
            0 -> onClick(ImagineButton_previous)
            1 -> onClick(ImagineButton_next)
            2 -> onClick(ImagineButton_play)
        }
    }

    override fun changeMusicInfor(title: String, artist: String, byteArray: ByteArray, seekbarmax: Int, duration: Int) {
        tv_curtime.setText(simpletime.format(mPresenter.mp.currentPosition))
        seekbar.setProgress(mPresenter.mp.currentPosition)
        Mtv_endtime.ExSetText(simpletime.format(seekbarmax))
        Mtv_song_title.ExSetText(title)
        Mtv_song_artist.ExSetText(artist)
        setHideAnimation(imv_song, duration, byteArray)
        seekbar.max = seekbarmax
        seekbar.setProgress(0)
        handler.post(runnable)

    }

    override fun changePlayButtonSrc(resId: Int) {

        button_play.setImageResource(resId)
    }

    override fun changeLikeButtonSrc(resId: Int) {
        ImagineButton_like.setImageResource(resId)

    }

    override fun setGurationLong(begin: String, end: String) {
        tv_curtime.setText(begin)
        Mtv_endtime.ExSetText(end)
    }

    override fun onPause() {
        mPresenter.unregReceiver()
        super.onPause()
    }

    override fun onDestroy() {
        unbindService(serviceConnection)
        super.onDestroy()
    }


    private fun setHideAnimation(view: View, duration: Int, byteArray: ByteArray) {

        var mHideAnimation: AlphaAnimation? = null

        if (null != mHideAnimation) {
            mHideAnimation.cancel()
        }
        mHideAnimation = AlphaAnimation(2.0f, 0.0f)
        mHideAnimation.duration = duration.toLong()
        mHideAnimation.fillAfter = true
        mHideAnimation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                Glide.with(this@MainActivity)
                        .load(byteArray)
                        .crossFade(500)
                        .into(imv_song)
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
        view.startAnimation(mHideAnimation)
    }
}
