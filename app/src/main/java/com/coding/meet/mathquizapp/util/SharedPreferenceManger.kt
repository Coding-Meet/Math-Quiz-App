package com.coding.meet.mathquizapp.util

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.coding.meet.mathquizapp.R

class SharedPreferenceManger(context: Context) {
    private val preference = context.getSharedPreferences(
        context.getString(R.string.app_name),
        AppCompatActivity.MODE_PRIVATE
    )
    private val editor = preference.edit()


    fun getLevelState(key:String) : Boolean {
        return preference.getBoolean(key,false)
    }

    fun setLevelState(key: String,value:Boolean){
        editor.putBoolean(key,value)
        editor.commit()
    }

}