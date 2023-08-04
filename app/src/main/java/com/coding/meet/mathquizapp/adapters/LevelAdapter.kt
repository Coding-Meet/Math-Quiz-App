package com.coding.meet.mathquizapp.adapters

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.coding.meet.mathquizapp.QuestionActivity
import com.coding.meet.mathquizapp.R
import com.coding.meet.mathquizapp.util.SharedPreferenceManger
import com.coding.meet.mathquizapp.util.gone
import com.coding.meet.mathquizapp.util.longToastShow
import com.coding.meet.mathquizapp.util.visible

class LevelAdapter(
    private val context: Context,
    private val sharedPreferenceManger: SharedPreferenceManger,
    private val tickMusic: MediaPlayer,
) :
    RecyclerView.Adapter<LevelAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtLevelName: TextView = itemView.findViewById(R.id.txtLevelName)
        val lockDoneImg: ImageView = itemView.findViewById(R.id.lockDoneImg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.level_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mainPos = position + 1
        holder.txtLevelName.text = "Level $mainPos"

        val currentLevelState = sharedPreferenceManger.getLevelState("level$position")
        if (currentLevelState) {
            holder.lockDoneImg.setImageResource(R.drawable.ic_done)
            holder.lockDoneImg.visible()
        } else {
            if (position == 0) {
                holder.lockDoneImg.gone()
            } else {
                val previousLevelState =
                    sharedPreferenceManger.getLevelState("level${position - 1}")
                if (previousLevelState){
                    holder.lockDoneImg.gone()
                }else{
                    holder.lockDoneImg.setImageResource(R.drawable.ic_lock)
                    holder.lockDoneImg.visible()
                }
            }
        }


        holder.itemView.setOnClickListener {
            tickMusic.start()
            if (currentLevelState) {
                questionIntent(position)
            } else {
                if (position == 0) {
                    questionIntent(position)
                } else {
                    val previousLevelState =
                        sharedPreferenceManger.getLevelState("level${position - 1}")
                    if (previousLevelState){
                        questionIntent(position)
                    }else{
                        context.longToastShow("Locked!!")
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return 100
    }

    private fun questionIntent(position: Int) {
        val questionIntent = Intent(context, QuestionActivity::class.java)
        questionIntent.putExtra("level", position)
        context.startActivity(questionIntent)
    }
}