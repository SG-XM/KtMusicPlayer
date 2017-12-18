package com.example.musicplayer

import android.app.Activity
import android.widget.Toast

/**
 * Created by 上官轩明 on 2017/12/14.
 */

fun Activity.toast(content: String) {
    Toast.makeText(this,content, Toast.LENGTH_SHORT).show()
}
