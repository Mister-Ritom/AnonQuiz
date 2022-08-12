package com.anons.anonquiz

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.net.URLConnection
import kotlin.collections.ArrayList


class AnswerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer)
        val questionText = findViewById<TextView>(R.id.question_txt)
        val answersList = findViewById<ListView>(R.id.answers_list)
        val nextButton = findViewById<Button>(R.id.next_button)
        val url = URL(intent.getStringExtra("url"))
        var question = ""
        val answers:MutableList<String> = ArrayList()
        var rightAnswer = ""
        var rootobj:JsonObject? = null
        var results: JsonArray? = null
        var position = 0
        var currentObject: JsonObject?
        var answered = false
        val thread = Thread {
            try {
                if (rootobj == null && results == null) {
                    val request: URLConnection = url.openConnection()
                    request.connect()
                    val root: JsonElement =
                        JsonParser.parseReader(InputStreamReader(request.content as InputStream)) //Convert the input stream to a json element
                    rootobj = root.asJsonObject
                    results=rootobj!!.getAsJsonArray("results")
                    currentObject = results!!.get(position).asJsonObject
                    question = currentObject!!.getAsJsonPrimitive("question").asString
                    rightAnswer = currentObject!!.getAsJsonPrimitive("correct_answer").asString
                    answers.add(rightAnswer)
                    for (je in currentObject!!.getAsJsonArray("incorrect_answers")) {
                        answers.add(je.asJsonPrimitive.asString)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            finally {
                runOnUiThread {
                    questionText.text = question
                    answers.shuffle()
                    val adapter:ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,answers)
                    answersList.adapter = adapter
                    answersList.setOnItemClickListener { parent, view, position, id ->
                        if (rightAnswer.equals(answers.get(position),true)) {
                            answersList[position].setBackgroundColor(getColor(R.color.colorAccent))
                        }
                        else answersList.setBackgroundColor(getColor(R.color.red))
                        answered = true
                    }
                }
            }

        }
        thread.start()

        nextButton.setOnClickListener {
            position++
            if (answered) {
                answers.removeAll(answers)
                currentObject = results!!.get(position).asJsonObject
                question = currentObject!!.getAsJsonPrimitive("question").asString
                rightAnswer = currentObject!!.getAsJsonPrimitive("correct_answer").asString
                answers.add(rightAnswer)
                for (je in currentObject!!.getAsJsonArray("incorrect_answers")) {
                    answers.add(je.asJsonPrimitive.asString)
                }
                questionText.text = question
                answers.shuffle()
                val adapter:ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,answers)
                answersList.adapter = adapter
                answersList.setOnItemClickListener { parent, view, position, id ->
                    if (rightAnswer.equals(answers.get(position),true)) {
                        answersList[position].setBackgroundColor(getColor(R.color.colorAccent))
                    }
                    else answersList.setBackgroundColor(getColor(R.color.red))
                    answered = true
                }
            }
            Log.w("Next Button","Trying to skip the level")
        }

    }

}