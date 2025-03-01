package com.example.calculatorapp.utils

import java.lang.Math.pow
import kotlin.math.*
import java.math.BigInteger

/**
 * Scientific constants library
 */
object ScientificConstants {
    // Mathematical constants
    val PI = Math.PI
    val E = Math.E
    val GOLDEN_RATIO = 1.618033988749895
    val EULER_MASCHERONI = 0.57721566490153286
    val SQRT_2 = sqrt(2.0)

    // Physical constants
    val PLANCK_CONSTANT = 6.62607015e-34 // J⋅s
    val AVOGADRO_NUMBER = 6.02214076e23 // mol^-1
    val SPEED_OF_LIGHT = 2.99792458e8 // m/s
    val GRAVITATIONAL_CONSTANT = 6.67430e-11 // N⋅m²/kg²
    val BOLTZMANN_CONSTANT = 1.380649e-23 // J/K
    val ELECTRON_MASS = 9.1093837015e-31 // kg
}

/**
 * Advanced mathematical operations
 */
object AdvancedMath {
    /**
     * Calculate permutation (nPr)
     */
    fun permutation(n: Int, r: Int): Double {
        if (n < 0 || r < 0 || r > n) {
            throw IllegalArgumentException("Invalid arguments for permutation: n=$n, r=$r")
        }
        return factorial(n) / factorial(n - r)
    }

    /**
     * Calculate combination (nCr)
     */
    fun combination(n: Int, r: Int): Double {
        if (n < 0 || r < 0 || r > n) {
            throw IllegalArgumentException("Invalid arguments for combination: n=$n, r=$r")
        }
        return factorial(n) / (factorial(r) * factorial(n - r))
    }

    /**
     * Enhanced factorial that can handle larger numbers
     */
    fun factorial(n: Int): Double {
        if (n < 0) throw IllegalArgumentException("Negative factorial not defined")
        if (n > 170) throw ArithmeticException("Factorial too large to represent as Double")
        
        var result = 1.0
        for (i in 2..n) {
            result *= i
        }
        return result
    }

    /**
     * Calculate gamma function for non-integer factorial
     * Simple implementation of Lanczos approximation
     */
    fun gamma(x: Double): Double {
        // For integers, use regular factorial
        if (x == x.toInt().toDouble() && x > 0) {
            return factorial(x.toInt() - 1)
        }
        
        // Implementation of Lanczos approximation for gamma function
        if (x < 0.5) {
            // Reflection formula: Γ(x) = π / (sin(πx) * Γ(1-x))
            val piDouble: Double = Math.PI
            val sinPiX: Double = sin(piDouble * x)
            val gamma1MinusX: Double = gamma(1.0 - x)
            return piDouble / (sinPiX * gamma1MinusX)
        }
        
        // Lanczos coefficients
        val p = doubleArrayOf(
            676.5203681218851, -1259.1392167224028, 771.32342877765313,
            -176.61502916214059, 12.507343278686905, -0.13857109526572012,
            9.9843695780195716e-6, 1.5056327351493116e-7
        )
        
        val y: Double = x - 1.0
        var sum: Double = 0.99999999999980993
        for (i in p.indices) {
            sum += p[i] / (y + (i + 1).toDouble())
        }
        
        val t: Double = y + p.size.toDouble() - 0.5
        
        // Break down the calculation into explicit steps to avoid ambiguity
        val piDouble: Double = Math.PI
        val sqrt2Pi: Double = sqrt(2.0 * piDouble)
        val tPower: Double = pow(t, y + 0.5)
        val expNegT: Double = exp(-t)
        
        // Final calculation with explicit Double multiplication
        return sqrt2Pi * (tPower * (expNegT * sum))
    }

    /**
     * Convert a decimal number to another base (up to base 36)
     */
    fun toBase(number: Long, base: Int): String {
        if (base < 2 || base > 36) {
            throw IllegalArgumentException("Base must be between 2 and 36")
        }
        return number.toString(base).uppercase()
    }

    /**
     * Parse a number from a given base to decimal
     */
    fun fromBase(number: String, base: Int): Long {
        if (base < 2 || base > 36) {
            throw IllegalArgumentException("Base must be between 2 and 36")
        }
        return number.toLong(base)
    }

    /**
     * GCD (Greatest Common Divisor)
     */
    fun gcd(a: Long, b: Long): Long {
        var x = a.absoluteValue
        var y = b.absoluteValue
        
        while (y != 0L) {
            val temp = y
            y = x % y
            x = temp
        }
        
        return x
    }

    /**
     * LCM (Least Common Multiple)
     */
    fun lcm(a: Long, b: Long): Long {
        return if (a == 0L || b == 0L) 0 else (a.absoluteValue * b.absoluteValue) / gcd(a, b)
    }
}

/**
 * Unit conversion utility
 */
object UnitConverter {
    // Length conversions (to meters)
    private val lengthFactors = mapOf(
        "mm" to 0.001,
        "cm" to 0.01,
        "m" to 1.0,
        "km" to 1000.0,
        "in" to 0.0254,
        "ft" to 0.3048,
        "yd" to 0.9144,
        "mi" to 1609.344
    )
    
    // Weight conversions (to kilograms)
    private val weightFactors = mapOf(
        "mg" to 1e-6,
        "g" to 0.001,
        "kg" to 1.0,
        "t" to 1000.0,
        "oz" to 0.0283495,
        "lb" to 0.453592,
        "st" to 6.35029
    )
    
    // Temperature conversion requires special handling
    fun convertTemperature(value: Double, from: String, to: String): Double {
        // First convert to Kelvin
        val kelvin = when (from) {
            "C" -> value + 273.15
            "F" -> (value + 459.67) * 5.0 / 9.0
            "K" -> value
            else -> throw IllegalArgumentException("Unsupported temperature unit: $from")
        }
        
        // Then convert from Kelvin to target unit
        return when (to) {
            "C" -> kelvin - 273.15
            "F" -> kelvin * 9.0 / 5.0 - 459.67
            "K" -> kelvin
            else -> throw IllegalArgumentException("Unsupported temperature unit: $to")
        }
    }
    
    fun convertLength(value: Double, from: String, to: String): Double {
        if (!lengthFactors.containsKey(from) || !lengthFactors.containsKey(to)) {
            throw IllegalArgumentException("Unsupported length unit: $from or $to")
        }
        
        return value * lengthFactors[from]!! / lengthFactors[to]!!
    }
    
    fun convertWeight(value: Double, from: String, to: String): Double {
        if (!weightFactors.containsKey(from) || !weightFactors.containsKey(to)) {
            throw IllegalArgumentException("Unsupported weight unit: $from or $to")
        }
        
        return value * weightFactors[from]!! / weightFactors[to]!!
    }
}
