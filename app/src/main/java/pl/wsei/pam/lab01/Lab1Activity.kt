package pl.wsei.pam.lab01

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class Lab1Activity : AppCompatActivity() {
    lateinit var mLayout: LinearLayout
    lateinit var mTitle: TextView
    lateinit var mProgress: ProgressBar
    var mBoxes: MutableList<CheckBox> = mutableListOf()
    var mButtons: MutableList<Button> = mutableListOf()
    private var completedTests = 0
    private val totalTests = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mLayout = findViewById(R.id.main)

        mTitle = TextView(this).apply {
            text = "Laboratorium 1"
            textSize = 24f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.setMargins(20, 20, 20, 20) }
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        }
        mLayout.addView(mTitle)

        for (i in 1..totalTests) {
            val row = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.HORIZONTAL
            }

            val checkBox = CheckBox(this).apply {
                text = "Zadanie $i"
                isEnabled = false
            }
            mBoxes.add(checkBox)

            val button = Button(this).apply {
                text = "Testuj"
                setOnClickListener {
                    if (!checkBox.isChecked && runTest(i)) {
                        checkBox.isChecked = true
                        completedTests++
                        updateProgress()
                        Toast.makeText(this@Lab1Activity, "Test zaliczony: Zadanie $i", Toast.LENGTH_SHORT).show()
                    } else if (checkBox.isChecked) {
                        Toast.makeText(this@Lab1Activity, "Już zaliczone!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@Lab1Activity, "Test niezaliczony: Zadanie $i", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            mButtons.add(button)

            row.addView(checkBox)
            row.addView(button)
            mLayout.addView(row)
        }

        mProgress = ProgressBar(
            this,
            null,
            androidx.appcompat.R.attr.progressBarStyle,
            androidx.appcompat.R.style.Widget_AppCompat_ProgressBar_Horizontal
        ).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            max = 100
            progress = 0
        }
        mLayout.addView(mProgress)
    }

    private fun runTest(taskNumber: Int): Boolean {
        return when (taskNumber) {
            1 -> task11(4, 6) in 0.666665..0.666667 &&
                    task11(7, -6) in -1.1666667..-1.1666665
            2 -> task12(7U, 6U) == "7 + 6 = 13" &&
                    task12(12U, 15U) == "12 + 15 = 27"
            3 -> task13(0.0, 5.4f) && !task13(7.0, 5.4f) &&
                    !task13(-6.0, -1.0f) && task13(6.0, 9.1f) &&
                    !task13(6.0, -1.0f) && task13(1.0, 1.1f)
            4 -> task14(-2, 5) == "-2 + 5 = 3" &&
                    task14(-2, -5) == "-2 - 5 = -7"
            5 -> task15("DOBRY") == 4 &&
                    task15("barDzo dobry") == 5 &&
                    task15("doStateczny") == 3 &&
                    task15("Dopuszczający") == 2 &&
                    task15("NIEDOSTATECZNY") == 1 &&
                    task15("XYZ") == -1
            6 -> task16(
                mapOf("A" to 2U, "B" to 4U, "C" to 3U),
                mapOf("A" to 1U, "B" to 2U)
            ) == 2U &&
                    task16(
                        mapOf("A" to 2U, "B" to 4U, "C" to 3U),
                        mapOf("F" to 1U, "G" to 2U)
                    ) == 0U &&
                    task16(
                        mapOf("A" to 23U, "B" to 47U, "C" to 30U),
                        mapOf("A" to 1U, "B" to 2U, "C" to 4U)
                    ) == 7U
            else -> false
        }
    }

    private fun updateProgress() {
        val progressValue = (completedTests * 100) / totalTests
        mProgress.progress = progressValue
    }

    private fun task11(a: Int, b: Int): Double = a.toDouble() / b.toDouble()
    private fun task12(a: UInt, b: UInt): String = "$a + $b = ${a + b}"
    private fun task13(a: Double, b: Float): Boolean = a >= 0 && a < b
    private fun task14(a: Int, b: Int): String =
        if (b >= 0) "$a + $b = ${a + b}" else "$a - ${Math.abs(b)} = ${a + b}"
    private fun task15(degree: String): Int = when (degree.lowercase()) {
        "bardzo dobry" -> 5
        "dobry" -> 4
        "dostateczny" -> 3
        "dopuszczający" -> 2
        "niedostateczny" -> 1
        else -> -1
    }
    private fun task16(store: Map<String, UInt>, asset: Map<String, UInt>): UInt =
        asset.map { (key, value) -> store[key]?.div(value) ?: 0U }.minOrNull() ?: 0U
}