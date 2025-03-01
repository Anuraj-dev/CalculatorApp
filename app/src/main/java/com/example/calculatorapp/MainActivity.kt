package com.example.calculatorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculatorapp.ui.theme.CalculatorAppTheme
import kotlin.math.*

// Import exp4j library for expression parsing
import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.operator.Operator
import org.apache.commons.math3.fraction.Fraction
import com.example.calculatorapp.model.CalculatorModel
import com.example.calculatorapp.ui.ConstantsLibraryDialog
import com.example.calculatorapp.ui.UnitConverterDialog
import com.example.calculatorapp.utils.AdvancedMath
import com.example.calculatorapp.utils.ScientificConstants

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculatorAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    CalculatorApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// Main calculator composable
@Composable
fun CalculatorApp(modifier: Modifier = Modifier) {
    // State management
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var rawResult by remember { mutableStateOf<Double?>(null) } // Store the raw numeric result
    var showScientific by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isDegreeMode by remember { mutableStateOf(true) }
    var showFraction by remember { mutableStateOf(false) }
    var calculationHistory by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    // Add flag to track when the last action was equals
    var lastActionWasEquals by remember { mutableStateOf(false) }
    val calculatorModel = remember { CalculatorModel() }

    // Dialog visibility states
    var showUnitConverter by remember { mutableStateOf(false) }
    var showConstantsLibrary by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    
    // Effect to update result format when fraction mode changes
    LaunchedEffect(showFraction, rawResult) {
        if (rawResult != null) {
            result = formatResult(rawResult!!, showFraction)
        }
    }

    // UI Components
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Memory indicator (if memory has a value)
        AnimatedVisibility(visible = calculatorModel.memory != 0.0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "M = ${calculatorModel.memory}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Calculator display
        CalculatorDisplay(
            input = input,
            result = result,
            errorMessage = errorMessage,
            showFraction = showFraction,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        // Function buttons row (unit converter, constants, history)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { showUnitConverter = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text("Units")
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Button(
                onClick = { showConstantsLibrary = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text("Constants")
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Button(
                onClick = { showHistory = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text("History")
            }
        }

        // Memory function buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("MC", "MR", "M+", "M-").forEach { memOp ->
                CalculatorButton(
                    text = memOp,
                    onClick = {
                        when (memOp) {
                            "MC" -> calculatorModel.memoryClear()
                            "MR" -> {
                                if (calculatorModel.memory != 0.0) {
                                    // Use memory value in calculation
                                    if (lastActionWasEquals) {
                                        input = calculatorModel.memory.toString()
                                    } else {
                                        input += calculatorModel.memory.toString()
                                    }
                                }
                            }
                            "M+" -> calculatorModel.memoryAdd(rawResult ?: 0.0)
                            "M-" -> calculatorModel.memorySubtract(rawResult ?: 0.0)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    buttonColor = MaterialTheme.colorScheme.secondaryContainer,
                    textColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        // Toggles row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Degrees/Radians toggle
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isDegreeMode) "DEG" else "RAD",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Switch(
                    checked = isDegreeMode,
                    onCheckedChange = { isDegreeMode = it }
                )
            }
            
            // Fraction/Decimal toggle
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Fraction",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Switch(
                    checked = showFraction,
                    onCheckedChange = { showFraction = it }
                )
            }
            
            // Scientific mode toggle
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Scientific",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Switch(
                    checked = showScientific,
                    onCheckedChange = { showScientific = it }
                )
            }
        }

        // Calculator keypad
        CalculatorKeypad(
            showScientific = showScientific,
            onButtonClick = { button ->
                when (button) {
                    "=" -> {
                        try {
                            rawResult = calculateExpression(input, isDegreeMode) // Store raw result
                            result = formatResult(rawResult!!, showFraction)
                            // Add to history
                            if (input.isNotEmpty()) {
                                calculationHistory = calculationHistory + Pair(input, result)
                            }
                            errorMessage = null
                            lastActionWasEquals = true // Set flag when equals is pressed
                        } catch (e: ArithmeticException) {
                            errorMessage = "Error: Division by zero"
                            result = ""
                            rawResult = null
                            lastActionWasEquals = false
                        } catch (e: Exception) {
                            errorMessage = "Error: ${e.message ?: "Invalid expression"}"
                            result = ""
                            rawResult = null
                            lastActionWasEquals = false
                        }
                    }
                    "C" -> {
                        input = ""
                        result = ""
                        rawResult = null
                        errorMessage = null
                        lastActionWasEquals = false // Reset flag
                    }
                    "⌫" -> {
                        if (input.isNotEmpty()) {
                            input = input.dropLast(1)
                        }
                    }
                    // Check if the button is an operation
                    "+", "-", "*", "/", "^", "%" -> {
                        if (lastActionWasEquals && result.isNotEmpty()) {
                            // Use previous result as input for the next calculation
                            input = result + button
                            lastActionWasEquals = false
                        } else {
                            input += button
                        }
                    }
                    else -> {
                        // Check if we should start a new calculation
                        if (lastActionWasEquals) {
                            // If last action was equals and we're entering a new number,
                            // clear the input and start fresh
                            input = when (button) {
                                "sin", "cos", "tan", "ln", "log", "√" -> "$button("
                                "π" -> "π"
                                "e" -> "e"
                                "!" -> "!"
                                else -> button
                            }
                            lastActionWasEquals = false
                        } else {
                            // Handle special functions
                            input += when (button) {
                                "sin", "cos", "tan", "ln", "log", "√" -> "$button("
                                "π" -> "π"
                                "e" -> "e"
                                "!" -> "!"
                                else -> button
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f)
        )
    }
}

@Composable
fun CalculatorDisplay(
    input: String,
    result: String,
    errorMessage: String?,
    showFraction: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            horizontalAlignment = Alignment.End
        ) {
            // Error message if present
            AnimatedVisibility(visible = errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            // Input expression
            Text(
                text = if (input.isEmpty()) "0" else input,
                fontSize = 32.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.End,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Result
            Text(
                text = result,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.End,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun CalculatorKeypad(
    showScientific: Boolean,
    onButtonClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val basicButtons = listOf(
        listOf("C", "⌫", "(", ")", "÷"),
        listOf("7", "8", "9", "×", "^"),
        listOf("4", "5", "6", "-", "√"),
        listOf("1", "2", "3", "+", "="),
        listOf("0", ".", "π", "e", "%")
    )

    val scientificButtons = listOf(
        "sin", "cos", "tan",
        "ln", "log", "!",
        "sinh", "cosh", "tanh"
    )

    Column(modifier = modifier) {
        // Scientific buttons row (conditionally visible)
        AnimatedVisibility(visible = showScientific) {
            Column {
                // Display scientific buttons in a 3x3 grid
                for (i in 0 until 3) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (j in 0 until 3) {
                            val index = i * 3 + j
                            if (index < scientificButtons.size) {
                                CalculatorButton(
                                    text = scientificButtons[index],
                                    onClick = { onButtonClick(scientificButtons[index]) },
                                    modifier = Modifier.weight(1f),
                                    buttonColor = MaterialTheme.colorScheme.tertiary,
                                    textColor = MaterialTheme.colorScheme.onTertiary
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Basic numeric and operation buttons
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxHeight()
        ) {
            basicButtons.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    row.forEach { buttonText ->
                        CalculatorButton(
                            text = buttonText,
                            onClick = { onButtonClick(buttonText.replace("÷", "/").replace("×", "*")) },
                            modifier = Modifier.weight(1f),
                            buttonColor = when (buttonText) {
                                "C", "⌫" -> MaterialTheme.colorScheme.errorContainer
                                "+", "-", "×", "÷", "^", "√", "%" -> MaterialTheme.colorScheme.secondaryContainer
                                "=" -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            },
                            textColor = when (buttonText) {
                                "C", "⌫" -> MaterialTheme.colorScheme.onErrorContainer
                                "+", "-", "×", "÷", "^", "√", "%" -> MaterialTheme.colorScheme.onSecondaryContainer
                                "=" -> MaterialTheme.colorScheme.onPrimaryContainer
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    // Button state for animation
    var isPressed by remember { mutableStateOf(false) }
    
    // Animations
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 100)
    )
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(4.dp)
            .clip(CircleShape)
            .background(buttonColor)
            .scale(scale)
            .clickable {
                isPressed = true
                onClick()
                // Reset after click
                isPressed = false
            }
            .padding(8.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

// New calculation engine using exp4j
fun calculateExpression(expression: String, isDegreeMode: Boolean): Double {
    if (expression.isEmpty()) return 0.0
    
    try {
        // Replace mathematical symbols and handle factorial
        var processedExpr = expression
            .replace("×", "*")
            .replace("÷", "/")
            .replace("π", "PI")
            .replace("e", "E")
            .replace("√(", "sqrt(")
        
        // Handle factorial operator
        val factorialRegex = "(\\d+)!".toRegex()
        while (factorialRegex.containsMatchIn(processedExpr)) {
            processedExpr = processedExpr.replace(factorialRegex) { matchResult ->
                val number = matchResult.groupValues[1].toInt()
                factorial(number).toString()
            }
        }
        
        // Create expression builder
        val expressionBuilder = ExpressionBuilder(processedExpr)
        
        // Add variables
        expressionBuilder.variable("PI")
                        .variable("E")
        
        // Create custom functions with degree/radian handling
        if (isDegreeMode) {
            expressionBuilder.function(object : net.objecthunter.exp4j.function.Function("sin", 1) {
                override fun apply(args: DoubleArray): Double = sin(Math.toRadians(args[0]))
            })
            expressionBuilder.function(object : net.objecthunter.exp4j.function.Function("cos", 1) {
                override fun apply(args: DoubleArray): Double = cos(Math.toRadians(args[0]))
            })
            expressionBuilder.function(object : net.objecthunter.exp4j.function.Function("tan", 1) {
                override fun apply(args: DoubleArray): Double = tan(Math.toRadians(args[0]))
            })
        } else {
            expressionBuilder.function(object : net.objecthunter.exp4j.function.Function("sin", 1) {
                override fun apply(args: DoubleArray): Double = sin(args[0])
            })
            expressionBuilder.function(object : net.objecthunter.exp4j.function.Function("cos", 1) {
                override fun apply(args: DoubleArray): Double = cos(args[0])
            })
            expressionBuilder.function(object : net.objecthunter.exp4j.function.Function("tan", 1) {
                override fun apply(args: DoubleArray): Double = tan(args[0])
            })
        }
        
        // Add additional functions
        expressionBuilder.function(object : net.objecthunter.exp4j.function.Function("sinh", 1) {
            override fun apply(args: DoubleArray): Double = sinh(args[0])
        })
        expressionBuilder.function(object : net.objecthunter.exp4j.function.Function("cosh", 1) {
            override fun apply(args: DoubleArray): Double = cosh(args[0])
        })
        expressionBuilder.function(object : net.objecthunter.exp4j.function.Function("tanh", 1) {
            override fun apply(args: DoubleArray): Double = tanh(args[0])
        })
        expressionBuilder.function(object : net.objecthunter.exp4j.function.Function("ln", 1) {
            override fun apply(args: DoubleArray): Double = ln(args[0])
        })
        expressionBuilder.function(object : net.objecthunter.exp4j.function.Function("log", 1) {
            override fun apply(args: DoubleArray): Double = log10(args[0])
        })
        expressionBuilder.function(object : net.objecthunter.exp4j.function.Function("sqrt", 1) {
            override fun apply(args: DoubleArray): Double = sqrt(args[0])
        })
            
        // Build the expression
        val expression = expressionBuilder.build()
        
        // Set variable values
        expression.setVariable("PI", Math.PI)
        expression.setVariable("E", Math.E)
        
        // Evaluate
        val result = expression.evaluate()
        
        return result
    } catch (e: ArithmeticException) {
        throw ArithmeticException("Division by zero")
    } catch (e: Exception) {
        throw RuntimeException("Invalid expression: ${e.message}")
    }
}

// Factorial implementation (for integers)
fun factorial(n: Int): Double {
    if (n < 0) throw IllegalArgumentException("Negative factorial not defined")
    var result = 1.0
    for (i in 2..n) {
        result *= i
    }
    return result
}

// Format result based on fraction/decimal mode
fun formatResult(value: Double, showFraction: Boolean): String {
    // For integer results, show without decimal point
    if (value == value.toLong().toDouble()) {
        return value.toLong().toString()
    }
    
    // For fraction mode
    if (showFraction) {
        try {
            // Create a new fraction from the double value
            // Using epsilon and max iterations for better accuracy
            val fraction = Fraction(value, 1.0E-10, 100)
            // Only show as fraction if denominator is reasonable
            if (fraction.denominator < 10000) {
                return "${fraction.numerator}/${fraction.denominator}"
            }
        } catch (e: Exception) {
            // Fall back to decimal if fraction conversion fails
        }
    }
    
    // Default to decimal format
    return value.toString()
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    CalculatorAppTheme {
        CalculatorApp()
    }
}