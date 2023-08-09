package com.coding.meet.mathquizapp

import android.app.Dialog
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import com.coding.meet.mathquizapp.databinding.ActivityQuestionBinding
import com.coding.meet.mathquizapp.models.QuestionItem
import com.coding.meet.mathquizapp.models.QuestionSplit
import com.coding.meet.mathquizapp.util.CustomCountdownTimer
import com.coding.meet.mathquizapp.util.SecurityManger
import com.coding.meet.mathquizapp.util.SharedPreferenceManger
import com.coding.meet.mathquizapp.util.invisible
import com.coding.meet.mathquizapp.util.loadJsonFromAssets
import com.coding.meet.mathquizapp.util.setupDialog
import com.coding.meet.mathquizapp.util.visible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import java.text.DecimalFormat
import kotlin.math.roundToInt

class QuestionActivity : AppCompatActivity() {
    private val gameScoreNextRoundDialog: Dialog by lazy {
        Dialog(this,R.style.DialogCustomTheme).apply {
            setupDialog(R.layout.game_score_next_round_dialog)
        }
    }
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBackPressedMethod()
        }
    }

    private val countdownTime = 60 // 1 hour , 3600 second, 60 min
    private val clockTime = (countdownTime * 1000).toLong()
    private val progressTime = (clockTime / 1000).toFloat()

    private lateinit var questionTimer: CustomCountdownTimer
    private var levelSize = 100
    private var level = 0
    private var correctQuestionPos = 0
    private var correctAnswerStr = ""

    private lateinit var correctScoreTxt : TextView
    private lateinit var wrongScoreTxt : TextView
    private lateinit var nextResumeOrRestartBtn  : Button

    private val tickMusic: MediaPlayer by lazy {
        MediaPlayer.create(this, R.raw.tick)
    }
    private val rightMusic: MediaPlayer by lazy {
        MediaPlayer.create(this, R.raw.right)
    }
    private val gameOverMusic: MediaPlayer by lazy {
        MediaPlayer.create(this, R.raw.gameover)
    }
    private val questionBinding : ActivityQuestionBinding by lazy {
        ActivityQuestionBinding.inflate(layoutInflater)
    }
    private val sharedPreferenceManger: SharedPreferenceManger by lazy {
        SharedPreferenceManger(this)
    }


    private lateinit var questionList : ArrayList<ArrayList<QuestionItem>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(questionBinding.root)
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)

        questionBinding.toolbarLayout.backImg.setOnClickListener {
            tickMusic.start()
            onBackPressedMethod()
        }

        level = intent.getIntExtra("level",0)
//        val jsonStr = loadJsonFromAssets("question.json")
        val securityManager = SecurityManger(this)
        val jsonStr = securityManager.decryptFile(
            "encryptedQuestion.json"
        )
        Log.d("jsonStr",jsonStr)

        // Manually json to Data class convert
        val manuallyQuestionList = manuallyJsonConvertDataClass(jsonStr)
        Log.d("manuallyQuestionList",manuallyQuestionList.toString())


        // Automatic json to data class convert
        val questionItemList : ArrayList<QuestionItem> = Gson().fromJson(
            jsonStr, object : TypeToken<ArrayList<QuestionItem>>() {}.type
        )
        Log.d("questionItemList",questionItemList.toString())

        // Splits this collection into a list of lists each not exceeding the given [size = 10].
        questionList = questionItemList.chunked(10) as ArrayList<ArrayList<QuestionItem>>

        setBtnOnClick()

        var secondsLeft = 0
        questionTimer = object : CustomCountdownTimer(clockTime, 1000) {}
        questionTimer.onTick = { millisUntilFinished ->
            val second = (millisUntilFinished / 1000.0f).roundToInt()
            if (second != secondsLeft) {
                secondsLeft = second
                timerFormat(
                    secondsLeft
                )
            }
        }
        questionTimer.onFinish = {
            gameScoreNextRoundShow()
        }

        questionBinding.toolbarLayout.titleTxt.text = "Level:${level + 1}"
        gameScoreNextRound()
        questionBinding.circularProgressBar.max = progressTime.toInt()
        questionBinding.circularProgressBar.progress = progressTime.toInt()
        question()
        questionTimer.startTimer()


    }

    private fun setBtnOnClick() {
        questionBinding.firstOptionBtn.setOnClickListener { setAnswerTxt(it) }
        questionBinding.secondOptionBtn.setOnClickListener { setAnswerTxt(it) }
        questionBinding.thirdOptionBtn.setOnClickListener { setAnswerTxt(it) }
        questionBinding.fourthOptionBtn.setOnClickListener { setAnswerTxt(it) }

        questionBinding.twoOptionRemoveBtn.setOnClickListener {
            when {
                correctAnswerStr.equals(questionBinding.firstOptionBtn.text.toString(),true)->{
                    optionHide(
                        questionBinding.secondOptionBtn,
                        questionBinding.thirdOptionBtn,
                        questionBinding.fourthOptionBtn,
                    )
                }
                correctAnswerStr.equals(questionBinding.secondOptionBtn.text.toString(),true)->{
                    optionHide(
                        questionBinding.firstOptionBtn,
                        questionBinding.thirdOptionBtn,
                        questionBinding.fourthOptionBtn
                    )
                }
                correctAnswerStr.equals(questionBinding.thirdOptionBtn.text.toString(),true)->{
                    optionHide(
                        questionBinding.secondOptionBtn,
                        questionBinding.fourthOptionBtn,
                        questionBinding.firstOptionBtn,
                    )
                }
                correctAnswerStr.equals(questionBinding.fourthOptionBtn.text.toString(),true)->{
                    optionHide(
                        questionBinding.firstOptionBtn,
                        questionBinding.secondOptionBtn,
                        questionBinding.thirdOptionBtn
                    )
                }
            }
            questionBinding.twoOptionRemoveBtn.isEnabled = false
        }
    }

    private fun optionHide(
        option1:Button,
        option2:Button,
        option3:Button
    ){
        when((1..3).random()){
            1 -> {
                option1.invisible()
                option2.invisible()
            }
            2 -> {
                option2.invisible()
                option3.invisible()
            }
            else -> {
                option1.invisible()
                option3.invisible()
            }
        }
    }

    private var mLastClickTime : Long = 0
    private fun setAnswerTxt(view: View) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()

        val answerTxt = (view as TextView).text.toString()
        Log.d("answerTxt",answerTxt)
        Log.d("correctAnswerStr",correctAnswerStr)
        if (correctAnswerStr.equals(answerTxt,true)){
            view.setBackgroundColor(Color.GREEN)
            correctQuestionPos += 1
            rightMusic.start()
            questionBinding.noOfQuestionTxt.text = "Question ${correctQuestionPos+1} / 10"
            if (correctQuestionPos > 9){
                correctScoreTxt.text = correctQuestionPos.toString()
                wrongScoreTxt.text = (10 - correctQuestionPos).toString()
                sharedPreferenceManger.setLevelState("level$level",true)
                nextResumeOrRestartBtn.text = "Next"
                questionTimer.pauseTimer()
                gameScoreNextRoundDialog.show()
            }else{
                Handler(Looper.getMainLooper()).postDelayed({
                    nextQuestion()
                },1000)
            }
        }else{
            view.setBackgroundColor(Color.RED)
            gameScoreNextRoundShow()
        }


    }

    private fun nextQuestion() {
        question()
        questionBinding.noOfQuestionTxt.text = "Question ${correctQuestionPos+1} / 10"
    }

    private fun gameScoreNextRound() {
        correctScoreTxt = gameScoreNextRoundDialog.findViewById(R.id.correctScoreTxt)
        wrongScoreTxt = gameScoreNextRoundDialog.findViewById(R.id.wrongScoreTxt)
        nextResumeOrRestartBtn = gameScoreNextRoundDialog.findViewById(R.id.nextResumeOrRestartBtn)

        nextResumeOrRestartBtn.setOnClickListener {
            tickMusic.start()
            if (nextResumeOrRestartBtn.text.toString().equals("Resume",true)){
                gameScoreNextRoundDialog.dismiss()
                onResume()
            }else{
                correctQuestionPos = 0
                if (nextResumeOrRestartBtn.text.toString().equals("Next",true)){
                    level += 1
                }
                gameScoreNextRoundDialog.dismiss()
                questionBinding.noOfQuestionTxt.text = "Question ${correctQuestionPos+1} / 10"
                if (level < levelSize){
                    questionBinding.toolbarLayout.titleTxt.text = "Level:${level + 1}"
                    nextQuestion()
                    questionBinding.circularProgressBar.max = progressTime.toInt()
                    questionBinding.circularProgressBar.progress = progressTime.toInt()
                    questionTimer.restartTimer()
                }else{
                    MaterialAlertDialogBuilder(this)
                        .setTitle("All Level Completed")
                        .setPositiveButton("Ok"){_,_ ->
                            finish()
                        }
                        .setCancelable(false)
                        .show()
                }
            }
        }

        val levelsBtn = gameScoreNextRoundDialog.findViewById<Button>(R.id.levelBtn)
        levelsBtn.setOnClickListener {
            tickMusic.start()
            gameScoreNextRoundDialog.dismiss()
            finish()
        }
    }

    private fun question() {
        val question = questionList[level][correctQuestionPos]

        correctAnswerStr = question.questionSplit.questionMarkValue
        questionBinding.questionTxt.text = question.questionTxt

        // Returns a new list with the elements of this list randomly shuffled.
        val optionsList = question.options.shuffled()

        questionBinding.firstOptionBtn.setBackgroundColor(Color.WHITE)
        questionBinding.secondOptionBtn.setBackgroundColor(Color.WHITE)
        questionBinding.thirdOptionBtn.setBackgroundColor(Color.WHITE)
        questionBinding.fourthOptionBtn.setBackgroundColor(Color.WHITE)

        questionBinding.firstOptionBtn.visible()
        questionBinding.secondOptionBtn.visible()
        questionBinding.thirdOptionBtn.visible()
        questionBinding.fourthOptionBtn.visible()

        questionBinding.firstOptionBtn.text = optionsList[0]
        questionBinding.secondOptionBtn.text = optionsList[1]
        questionBinding.thirdOptionBtn.text = optionsList[2]
        questionBinding.fourthOptionBtn.text = optionsList[3]

        questionBinding.twoOptionRemoveBtn.isEnabled = true

    }

    private fun timerFormat(secondsLeft: Int) {
        questionBinding.circularProgressBar.progress = secondsLeft
        val decimalFormat = DecimalFormat("00")

        val timeFormat1 = decimalFormat.format(secondsLeft)
        questionBinding.timeTxt.text = timeFormat1
    }

    private fun onBackPressedMethod() {
        questionTimer.pauseTimer()
        correctScoreTxt.text = correctQuestionPos.toString()
        wrongScoreTxt.text = "0"
        nextResumeOrRestartBtn.text = "Resume"
        gameScoreNextRoundDialog.show()
    }

    private fun gameScoreNextRoundShow(){
        gameOverMusic.start()
        questionTimer.destroyTimer()
        correctScoreTxt.text = correctQuestionPos.toString()
        wrongScoreTxt.text = (10 - correctQuestionPos).toString()
        nextResumeOrRestartBtn.text = if (correctQuestionPos == 9) "Next" else "Restart"
        gameScoreNextRoundDialog.show()
    }

    // manually Json Convert Data Class
    private fun manuallyJsonConvertDataClass(jsonString: String): ArrayList<QuestionItem>{
        val jsonArray = JSONArray(jsonString)

        val questionList = ArrayList<QuestionItem>()

        for (index in 0 until  jsonArray.length()){
            val questionObj = jsonArray.getJSONObject(index)

            val optionArray = questionObj.getJSONArray("options")
            val optionList = Array(optionArray.length()) {
                optionArray.getString(it)
            }

            val questionSplitObj = questionObj.getJSONObject("questionSplit")
            val questionSplit = QuestionSplit(
                questionSplitObj.getInt("answer"),
                questionSplitObj.getInt("first"),
                questionSplitObj.getString("operator"),
                questionSplitObj.getString("questionMarkValue"),
                questionSplitObj.getInt("second"),
            )

            val questionItem = QuestionItem(
                optionList.toList(),
                questionSplit,
                questionObj.getString("questionTxt"),
                questionObj.getString("type")
            )

            questionList.add(questionItem)

        }


        return questionList
    }

    override fun onPause() {
        questionTimer.pauseTimer()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (!gameScoreNextRoundDialog.isShowing){
            questionTimer.resumeTimer()
        }
    }

    override fun onDestroy() {
        questionTimer.destroyTimer()
        super.onDestroy()
    }
}