package com.anons.anonquiz

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class MainActivity : AppCompatActivity() {
    private val categories = arrayOf("General Knowledge","Entertainment: Books","Entertainment: Film","Entertainment: Music","Entertainment: Musicals & Theatres","Entertainment: Television","Entertainment: Video Games","Entertainment: Board Games","Science & Nature","Science: Computers","Science: Mathematics","Mythology,Sports","Geography","History","Politics","Art,Celebrities","Animals","Vehicles","Entertainment: Comics","Science: Gadgets","Entertainment: Japanese Anime & Manga","Entertainment: Cartoon & Animations")
    private val difficulties = arrayOf("Easy","Hard")
    private val types = arrayOf("Multiple Choice","True False")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val questionsText = findViewById<EditText>(R.id.question)
        val categorySpinner = getSpinner(R.id.category)
        val difficultySpinner = getSpinner(R.id.difficulty)
        val typeSpinner = getSpinner(R.id.type)
        val submitButton = findViewById<Button>(R.id.submit)
        val pointsText = findViewById<TextView>(R.id.points)
        pointsText.text = "Points:${getPoints()}"
        categorySpinner.adapter = getArrayAdapter(categories)
        difficultySpinner.adapter = getArrayAdapter(difficulties)
        typeSpinner.adapter = getArrayAdapter(types)

        submitButton.setOnClickListener{
            val questions = getQuestionInt(questionsText)
            val category = categorySpinner.selectedItem
            val difficulty = difficultySpinner.selectedItem
            val type = typeSpinner.selectedItem
            if (questions >= 1) {
                Log.i(
                    "Finding",
                    "Finding questions for amount $questions category $category Category Code ${
                        getCategoryCode(category.toString())
                    } type $type difficulty $difficulty"
                )
                val url = generateUrlFromInput(
                    category.toString(),
                    difficulty.toString(), type.toString(), questions.toString()
                )
                Log.i("Finding", "Found Questions Url $url")
                val intent = Intent(this,AnswerActivity::class.java)
                intent.putExtra("url",url)
                startActivity(intent)
            }
            else Log.e("Finding","Questions cannot be less than 1")
        }


    }

    private fun generateUrlFromInput(category:String, difficulty:String, type:String, questions:String):String {
        var a = "9"
        var b = "easy"
        var c = "boolean"

        if (!category.equals("General Knowledge",true)) {
            a = getCategoryCode(category).toString()
        }
        if (difficulty.equals("Hard",true)) {
            b = "medium"
        }
        if (type.equals("Multiple Choice",true)) {
            c = "multiple"
        }
//TODO use better API
        return "https://opentdb.com/api.php?amount=$questions&category=$a&difficulty=$b&type=$c"

    }

    private fun getCategoryCode(s:String):Int {
        return when (s.lowercase(Locale.ROOT)){
            "General Knowledge"-> 9
            "Entertainment: Books"->10
            "Entertainment: Film"->11
            "Entertainment: Music"->12
            "Entertainment: Musicals & Theatres"->13
            "Entertainment: Television"->14
            "Entertainment: Video Games"->15
            "Entertainment: Board Games"->16
            "Science & Nature"->17
            "Science: Computers"->18
            "Science: Mathematics"->19
            "Mythology"->20
            "Sports"->21
            "Geography"->22
            "History"->23
            "Politics"->24
            "Art"->25
            "Celebrities"->26
            "Animals"->27
            "Vehicles"->28
            "Entertainment: Comics"->29
            "Science: Gadgets"->30
            "Entertainment: Japanese Anime & Manga"->31
            "Entertainment: Cartoon & Animations"->32
            else ->9
        }
    }

    private fun getArrayAdapter(items:Array<String>):ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items)

    private fun getSpinner(id:Int):Spinner = findViewById(id)

    fun getQuestionInt(text:EditText):Int {
        if (text.text.isNullOrEmpty()) {
            text.error = "How many questions do you want? 10? 20?"
            return 0
        }
        val regex = "\\d+".toRegex()
        if (!text.text.matches(regex)) {
            text.error = "Only numbers are allowed"
            return 0
        }
        return text.text.toString().toInt()
    }


    fun getPoints():Int {
        return PreferenceManager.getDefaultSharedPreferences(this).getInt("points",0)
    }

}