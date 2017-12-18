package com.example.musicplayer

import android.graphics.Bitmap
import android.graphics.drawable.Drawable

/**
 * Created by 上官轩明 on 2017/12/12.
 */
interface MainActivityInterface {
    //fun changeImg(pic: Drawable);
    fun changeMusicInfor(title: String, artist: String, byteArray: ByteArray, seekbarmax: Int, duration: Int)

    fun changePlayButtonSrc(resId: Int)
    fun changeLikeButtonSrc(resId: Int)
    fun setGurationLong(begin: String, end: String)
    fun clickButton(id: Int)
}