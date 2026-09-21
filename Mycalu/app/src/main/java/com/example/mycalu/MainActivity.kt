package com.example.mycalu

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import java.text.DecimalFormat

class MainActivity : ComponentActivity() {

    private lateinit var tvHistory: TextView
    private lateinit var tvDisplay: TextView

    private var currentInput = ""
    private var lastResult: Double? = null
    private var activeOperator: String? = null
    private var isOperatorJustPressed = false
    private var isEqualsJustPressed = false

    private val decimalFormat = DecimalFormat("#.########")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reui)

        tvHistory = findViewById(R.id.textView)
        tvDisplay = findViewById(R.id.textView2)

        // Number Buttons
        val btn0: Button = findViewById(R.id.button39)
        val btn1: Button = findViewById(R.id.button32)
        val btn2: Button = findViewById(R.id.button33)
        val btn3: Button = findViewById(R.id.button34)
        val btn4: Button = findViewById(R.id.button28)
        val btn5: Button = findViewById(R.id.button29)
        val btn6: Button = findViewById(R.id.button30)
        val btn7: Button = findViewById(R.id.button24)
        val btn8: Button = findViewById(R.id.button25)
        val btn9: Button = findViewById(R.id.button26)
        val btnDot: Button = findViewById(R.id.button36)

        // Operator Buttons
        val btnAdd: Button = findViewById(R.id.button35)
        val btnSub: Button = findViewById(R.id.button31)
        val btnMul: Button = findViewById(R.id.button27)
        val btnDiv: Button = findViewById(R.id.button23)
        val btnEqual: Button = findViewById(R.id.button38)

        // Control Buttons
        val btnAC: Button = findViewById(R.id.button19)
        val btnC: Button = findViewById(R.id.button21)
        val btnX: Button = findViewById(R.id.button22)

        val numberClickListener = { text: String ->
            if (isOperatorJustPressed || isEqualsJustPressed) {
                currentInput = ""
                isOperatorJustPressed = false
                isEqualsJustPressed = false
            }
            if (text == "." && currentInput.contains(".")) {
                // Do nothing if already contains a decimal point
            } else {
                if (currentInput == "0" && text != ".") {
                    currentInput = text
                } else {
                    currentInput += text
                }
                tvDisplay.text = currentInput
            }
        }

        btn0.setOnClickListener { numberClickListener("0") }
        btn1.setOnClickListener { numberClickListener("1") }
        btn2.setOnClickListener { numberClickListener("2") }
        btn3.setOnClickListener { numberClickListener("3") }
        btn4.setOnClickListener { numberClickListener("4") }
        btn5.setOnClickListener { numberClickListener("5") }
        btn6.setOnClickListener { numberClickListener("6") }
        btn7.setOnClickListener { numberClickListener("7") }
        btn8.setOnClickListener { numberClickListener("8") }
        btn9.setOnClickListener { numberClickListener("9") }
        btnDot.setOnClickListener { numberClickListener(".") }

        val operatorClickListener = { op: String ->
            val value = currentInput.toDoubleOrNull() ?: lastResult
            if (value != null) {
                if (activeOperator != null && !isOperatorJustPressed && currentInput.isNotEmpty()) {
                    val result = calculate(lastResult ?: 0.0, value, activeOperator!!)
                    lastResult = result
                    tvDisplay.text = formatValue(result)
                } else {
                    lastResult = value
                }
                activeOperator = op
                isOperatorJustPressed = true
                isEqualsJustPressed = false
                tvHistory.text = "${formatValue(lastResult!!)} $op"
            }
        }

        btnAdd.setOnClickListener { operatorClickListener("+") }
        btnSub.setOnClickListener { operatorClickListener("-") }
        btnMul.setOnClickListener { operatorClickListener("×") }
        btnDiv.setOnClickListener { operatorClickListener("÷") }

        btnEqual.setOnClickListener {
            val value = currentInput.toDoubleOrNull()
            if (activeOperator != null && value != null && lastResult != null) {
                val result = calculate(lastResult!!, value, activeOperator!!)
                tvHistory.text = "${formatValue(lastResult!!)} $activeOperator ${formatValue(value)} ="
                tvDisplay.text = formatValue(result)
                lastResult = result
                currentInput = formatValue(result)
                activeOperator = null
                isEqualsJustPressed = true
            }
        }

        btnAC.setOnClickListener {
            currentInput = ""
            lastResult = null
            activeOperator = null
            isOperatorJustPressed = false
            isEqualsJustPressed = false
            tvDisplay.text = "0"
            tvHistory.text = ""
        }

        btnC.setOnClickListener {
            currentInput = ""
            tvDisplay.text = "0"
        }

        btnX.setOnClickListener {
            if (currentInput.isNotEmpty()) {
                currentInput = currentInput.dropLast(1)
                tvDisplay.text = if (currentInput.isEmpty()) "0" else currentInput
            }
        }
    }

    private fun calculate(val1: Double, val2: Double, op: String): Double {
        return when (op) {
            "+" -> val1 + val2
            "-" -> val1 - val2
            "×" -> val1 * val2
            "÷" -> if (val2 != 0.0) val1 / val2 else 0.0
            else -> 0.0
        }
    }

    private fun formatValue(value: Double): String {
        return decimalFormat.format(value)
    }
}
