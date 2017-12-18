package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.widget.TextView

/**
 * Created by 上官轩明 on 2017/11/9.
 */

class MyTextView : TextView {

    private var myText: CharSequence = ""
    private var mType: TextView.BufferType? = null
    private var mHandler: Handler? = null
    private var mIndex = 0
    private val NEXT_CHAR = 1


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }


    fun ExSetText(text: String) {
        mIndex = 0
        myText = text
        super.setText("")

        //        for (int i = 0; i <myText.length() ; i++) {
        //            super.setText(myText.subSequence(mIndex,mIndex), type);
        //        }


        object : Thread() {

            override fun run() {


                try {
                    while (mIndex < myText.length - 1) {
                        Thread.sleep(40)
                        //Message msg = Message.obtain();
                        //msg.what = NEXT_CHAR;
                        mHandler!!.sendEmptyMessage(NEXT_CHAR)

                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
        }.start()
    }

    override fun setText(text: CharSequence, type: TextView.BufferType) {
        super.setText(text, type)
        mType = type

    }

    @SuppressLint("HandlerLeak")
    private fun init() {

        //myText = getText().toString();
        //super.setText("");
        mHandler = object : Handler() {
            @SuppressLint("SetTextI18n")
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                if (msg.what == NEXT_CHAR && mIndex < myText.length) {
                    val str = this@MyTextView.text.toString() + myText[mIndex]
                    super@MyTextView.setText(str, mType)
                    mIndex++
                }
            }
        }
    }

}
