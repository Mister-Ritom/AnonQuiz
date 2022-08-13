package com.anons.anonquiz

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.net.URLConnection


class AnswerActivity : AppCompatActivity() {
    private var pointsInt = 0
    private var answered = false
    private val answers:ArrayList<String> = ArrayList()
    private var currentPos:Int = 0
    private var question = ""
    private var rightAnswer = ""
    private lateinit var root: JsonElement
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer)
        val questionText = findViewById<TextView>(R.id.question_txt)
        val answersList = findViewById<ListView>(R.id.answers_list)
        val nextButton = findViewById<Button>(R.id.next_button)
        val pointsText = findViewById<TextView>(R.id.points)
        val url = URL(intent.getStringExtra("url"))
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, answers)
        val thread = Thread {
            try {
                val request: URLConnection = url.openConnection()
                request.connect()
                root = JsonParser.parseReader(InputStreamReader(request.content as InputStream))
                getAnswers()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                runOnUiThread {
                    questionText.text = question
                    answersList.adapter = adapter
                    answersList.setOnItemClickListener { _, _, position, _ ->
                        onClickList(answersList,position,answers.indexOf(rightAnswer))
                    }
                }
            }

        }
        thread.start()

        nextButton.setOnClickListener {
            currentPos++
            if (answered) {
                answered = false
                getAnswers()
                questionText.text = question
                answersList.adapter = adapter
                pointsText.text = getString(R.string.points)+pointsInt
            }
            Log.w("Next Button", "Trying to skip the level")
        }

    }

    private fun getAnswers() {
        answers.clear()
        val rootobj = root.asJsonObject
        val results = rootobj.getAsJsonArray("results")
        val currentObject = results.get(currentPos).asJsonObject
        question = currentObject!!.getAsJsonPrimitive("question").asString
        rightAnswer = currentObject.getAsJsonPrimitive("correct_answer").asString
        answers.add(rightAnswer)
        for (je in currentObject.getAsJsonArray("incorrect_answers")) {
            answers.add(je.asJsonPrimitive.asString)
        }
        answers.shuffle()
    }

    private fun onClickList(list:ListView, position:Int, right:Int) {
        if (right==position) {
            pointsInt++
            setPoints(pointsInt)
        }
        list.setBackgroundColor(getColor(R.color.white))
        list[right].setBackgroundColor(getColor(R.color.instacenterclord))
        answered = true
    }

    private fun setPoints(points:Int) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt("points",points).apply()
    }

    fun getPoints():Int {
        return PreferenceManager.getDefaultSharedPreferences(this).getInt("points",0)
    }

}