package com.coding.meet.mathquizapp.util

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast

fun Dialog.setupDialog(layoutResID:Int){
    setContentView(layoutResID)
    window!!.setLayout(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )
    setCancelable(false)
}

fun View.visible(){
    visibility  = View.VISIBLE
}
fun View.invisible(){
    visibility  = View.INVISIBLE
}
fun View.gone(){
    visibility  = View.GONE
}


fun Context.longToastShow(msg:String){
    Toast.makeText(this,msg,Toast.LENGTH_LONG).show()
}


fun Activity.loadJsonFromAssets(fileName:String) : String{
    val inputStream = assets.open(fileName)
    val size = inputStream.available()
    val byteArray = ByteArray(size)
    inputStream.read(byteArray)
    inputStream.close()
    return String(byteArray,Charsets.UTF_8)
}