package com.android.currencyx

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class CalculatorFragment : Fragment() {

    private lateinit var resultTextView: TextView
    private lateinit var inputTextView: TextView
    private var currentInput: StringBuilder = StringBuilder()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calculator, container, false)

        resultTextView = view.findViewById(R.id.angkaKeluar)
        inputTextView = view.findViewById(R.id.angkaMasuk)

        val buttons = arrayOf(
            view.findViewById<Button>(R.id.btn0),
            view.findViewById<Button>(R.id.btn1),
            view.findViewById<Button>(R.id.btn2),
            view.findViewById<Button>(R.id.btn3),
            view.findViewById<Button>(R.id.btn4),
            view.findViewById<Button>(R.id.btn5),
            view.findViewById<Button>(R.id.btn6),
            view.findViewById<Button>(R.id.btn7),
            view.findViewById<Button>(R.id.btn8),
            view.findViewById<Button>(R.id.btn9),
            view.findViewById<Button>(R.id.btnTambah),
            view.findViewById<Button>(R.id.btnBagi),
            view.findViewById<Button>(R.id.btnKali),
            view.findViewById<Button>(R.id.btnKurang),
            view.findViewById<Button>(R.id.btnHasil),
            view.findViewById<Button>(R.id.btnHapus),
            view.findViewById<Button>(R.id.btnTitik),
            view.findViewById<Button>(R.id.btnPersen),
            view.findViewById<Button>(R.id.btnBackspace)
        )

        for (button in buttons) {
            button.setOnClickListener { onButtonClick(button) }
        }

        return view
    }

    private fun onButtonClick(button: Button) {
        when (button.id) {
            R.id.btnHasil -> calculateResult()
            R.id.btnHapus -> clearInput()
            R.id.btnBackspace -> clearOneInput()
            R.id.btnPersen -> appendToInput("%")
            else -> {
                appendToInput(button.text.toString())
                updateInputText()
            }
        }
        updateResultText()
    }

    private fun appendToInput(value: String) {
        currentInput.append(value)
    }

    private fun updateInputText() {
        inputTextView.text = currentInput.toString()
    }

    private fun clearInput() {
        currentInput = StringBuilder()
        updateInputText()
        updateResultText()
    }

    private fun clearOneInput() {
        if (currentInput.isNotEmpty()) {
            currentInput.deleteCharAt(currentInput.length - 1)
            updateInputText()
            updateResultText()
        }
    }

    private fun calculateResult() {
        try {
            val result = eval(currentInput.toString())
            currentInput = StringBuilder(result.toString())
            updateResultText()
        } catch (e: Exception) {
            currentInput = StringBuilder("Error")
            updateResultText()
        }
    }

    private fun updateResultText() {
        resultTextView.text = currentInput.toString()
    }
    
    private fun eval(input: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0.toChar()

            fun nextChar() {
                ch = if (++pos < input.length) input[pos] else (-1).toChar()
            }

            fun eat(charToEat: Char): Boolean {
                while (ch == ' ') nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < input.length) throw RuntimeException("Unexpected: " + ch)
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    when {
                        eat('+') -> x += parseTerm()
                        eat('-') -> x -= parseTerm()
                        else -> return x
                    }
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    when {
                        eat('*') -> x *= parseFactor()
                        eat('/') -> x /= parseFactor()
                        else -> return x
                    }
                }
            }

            fun parseFactor(): Double {
                if (eat('+')) return parseFactor()
                if (eat('-')) return -parseFactor()
                var x: Double
                val startPos = pos
                if (eat('(')) {
                    x = parseExpression()
                    eat(')')
                } else if ((ch in '0'..'9') || ch == '.') {
                    while ((ch in '0'..'9') || ch == '.') nextChar()
                    x = input.substring(startPos, pos).toDouble()
                } else {
                    throw RuntimeException("Unexpected: " + ch)
                }

                // Check for percentage after parsing a factor
                while (true) {
                    when {
                        eat('^') -> x = Math.pow(x, parseFactor())
                        eat('%') -> x /= 100.0  // Divide by 100 for percentage
                        else -> break
                    }
                }

                return x
            }
        }.parse()
    }
}
