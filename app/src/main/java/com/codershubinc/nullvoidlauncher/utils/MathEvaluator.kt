package com.codershubinc.nullvoidlauncher.utils

import java.text.DecimalFormat

object MathEvaluator {

    private val df = DecimalFormat("#,##0.######")

    fun evaluate(expression: String): String? {
        val clean = expression.trim().replace(" ", "").replace("×", "*").replace("÷", "/")
        if (clean.isEmpty()) return null

        // Only evaluate if it contains basic math operators and digits
        val isMathExpr = clean.matches(Regex("^[0-9+\\-*/().,%^]+$")) &&
                clean.any { it in "+-*/%^" }
        if (!isMathExpr) return null

        return try {
            val result = Parser(clean).parse()
            if (result.isNaN() || result.isInfinite()) null
            else df.format(result)
        } catch (_: Exception) {
            null
        }
    }

    private class Parser(private val str: String) {
        private var pos = -1
        private var ch = -1

        private fun nextChar() {
            ch = if (++pos < str.length) str[pos].code else -1
        }

        private fun eat(charToEat: Int): Boolean {
            while (ch == ' '.code) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            if (pos < str.length) throw RuntimeException("Unexpected: " + ch.toChar())
            return x
        }

        private fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                when {
                    eat('+'.code) -> x += parseTerm()
                    eat('-'.code) -> x -= parseTerm()
                    else -> return x
                }
            }
        }

        private fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                when {
                    eat('*'.code) -> x *= parseFactor()
                    eat('/'.code) -> {
                        val divisor = parseFactor()
                        if (divisor == 0.0) throw ArithmeticException("Divide by zero")
                        x /= divisor
                    }
                    eat('%'.code) -> x %= parseFactor()
                    else -> return x
                }
            }
        }

        private fun parseFactor(): Double {
            if (eat('+'.code)) return +parseFactor()
            if (eat('-'.code)) return -parseFactor()

            var x: Double
            val startPos = pos
            if (eat('('.code)) {
                x = parseExpression()
                eat(')'.code)
            } else if ((ch in '0'.code..'9'.code) || ch == '.'.code) {
                while ((ch in '0'.code..'9'.code) || ch == '.'.code) nextChar()
                x = str.substring(startPos, pos).toDouble()
            } else {
                throw RuntimeException("Unexpected: " + ch.toChar())
            }

            if (eat('^'.code)) x = Math.pow(x, parseFactor())

            return x
        }
    }
}
