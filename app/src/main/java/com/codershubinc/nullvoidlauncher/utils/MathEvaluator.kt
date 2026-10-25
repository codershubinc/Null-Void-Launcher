package com.codershubinc.nullvoidlauncher.utils

import java.text.DecimalFormat

object MathEvaluator {

    private val df = ThreadLocal.withInitial { DecimalFormat("#,##0.######") }
    private const val MAX_EXPR_LENGTH = 120
    private const val MAX_RECURSION_DEPTH = 25

    fun evaluate(expression: String): String? {
        val clean = expression.trim().replace(" ", "").replace("×", "*").replace("÷", "/")
        if (clean.isEmpty() || clean.length > MAX_EXPR_LENGTH) return null

        // Only evaluate if it contains basic math operators and digits
        val isMathExpr = clean.matches(Regex("^[0-9+\\-*/().,%^]+$")) &&
                clean.any { it in "+-*/%^" }
        if (!isMathExpr) return null

        return try {
            val result = Parser(clean).parse()
            if (result.isNaN() || result.isInfinite()) null
            else df.get()?.format(result)
        } catch (_: Throwable) {
            null
        }
    }

    private class Parser(private val str: String) {
        private var pos = -1
        private var ch = -1
        private var depth = 0

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
            if (++depth > MAX_RECURSION_DEPTH) throw RuntimeException("Recursion depth exceeded")
            try {
                var x = parseTerm()
                while (true) {
                    when {
                        eat('+'.code) -> x += parseTerm()
                        eat('-'.code) -> x -= parseTerm()
                        else -> return x
                    }
                }
            } finally {
                depth--
            }
        }

        private fun parseTerm(): Double {
            if (++depth > MAX_RECURSION_DEPTH) throw RuntimeException("Recursion depth exceeded")
            try {
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
            } finally {
                depth--
            }
        }

        private fun parseFactor(): Double {
            if (++depth > MAX_RECURSION_DEPTH) throw RuntimeException("Recursion depth exceeded")
            try {
                if (eat('+'.code)) return +parseFactor()
                if (eat('-'.code)) return -parseFactor()

                var x: Double
                val startPos = pos
                if (eat('('.code)) {
                    x = parseExpression()
                    eat(')'.code)
                } else if ((ch in '0'.code..'9'.code) || ch == '.'.code) {
                    while ((ch in '0'.code..'9'.code) || ch == '.'.code) nextChar()
                    val numStr = str.substring(startPos, pos)
                    if (numStr == "." || numStr.isEmpty()) throw RuntimeException("Invalid number")
                    x = numStr.toDouble()
                } else {
                    throw RuntimeException("Unexpected: " + ch.toChar())
                }

                if (eat('^'.code)) x = Math.pow(x, parseFactor())

                return x
            } finally {
                depth--
            }
        }
    }
}
