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
    var showScientific by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isDegreeMode by remember { mutableStateOf(true) }
    var showFraction by remember { mutableStateOf(false) }
    var calculationHistory by remember { mutableStateOf(listOf<Pair<String, String>>()) }

    // UI Components
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
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
                            errorMessage = null
                        } catch (e: Exception) {
                            errorMessage = "Error: ${e.message ?: "Unknown error"}"
                            result = ""
                        }
                    }
                    "C" -> {
                        input = ""
                        result = ""
                        errorMessage = null
                    }
                    "⌫" -> {
                        if (input.isNotEmpty()) {
                            input = input.dropLast(1)
                        }
                    }
                    else -> {
                        // Handle special functions
                        input += when (button) {
                            "sin", "cos", "tan", "ln", "log", "√" -> "$button("
                            "π" -> "π"
                            "e" -> "e"
                            else -> button
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

// Calculation engine
fun calculateExpression(expression: String): String {
    if (expression.isEmpty()) return ""
    
    try {
        // Replace mathematical symbols with their values
        var expr = expression.replace("π", Math.PI.toString())
                             .replace("e", Math.E.toString())
        
        // Handle scientific functions
        expr = evaluateScientificFunctions(expr)
        
        // Calculate basic operations
        val result = evaluateBasicOperations(expr)
        
        // Format result to avoid unnecessary decimal places
        return if (result == result.toLong().toDouble()) {
            result.toLong().toString()
        } else {
            result.toString()
        }
    } catch (e: Exception) {
        throw RuntimeException("Invalid expression")
    }
}

// Evaluate scientific functions like sin, cos, etc.
fun evaluateScientificFunctions(expression: String): String {
    var expr = expression
    
    // Pattern to find function calls like sin(x), log(x), etc.
    val functionRegex = "(sin|cos|tan|ln|log|sqrt|√)\\((.*?)\\)".toRegex()
    while (functionRegex.containsMatchIn(expr)) {
        expr = expr.replace(functionRegex) { matchResult ->
            val function = matchResult.groupValues[1]
            val argument = evaluateBasicOperations(matchResult.groupValues[2])
            
            when (function) {
                "sin" -> sin(Math.toRadians(argument)).toString()
                "cos" -> cos(Math.toRadians(argument)).toString()
                "tan" -> tan(Math.toRadians(argument)).toString()
                "ln" -> ln(argument).toString()
                "log" -> log10(argument).toString()
                "sqrt", "√" -> sqrt(argument).toString()
                else -> argument.toString()
            }
        }
    }
    
    return expr
}

// Basic operation evaluation using a simple recursive descent parser
fun evaluateBasicOperations(expression: String): Double {
    // Parse expression and compute result
    // This is a simplified implementation. In a real app, use a proper expression parser.
    // For demonstration purposes, we'll implement a basic evaluator
    
    // Handle parentheses first
    val parenRegex = "\\(([^()]+)\\)".toRegex()
    var expr = expression
    while (parenRegex.containsMatchIn(expr)) {
        expr = expr.replace(parenRegex) { matchResult ->
            evaluateBasicOperations(matchResult.groupValues[1]).toString()
        }
    }
    
    // Evaluate addition and subtraction
    val parts = expr.split("""(?<=\d)([+\-])(?=\d)""".toRegex())
    if (parts.size > 1) {
        var result = evaluateMultiplicationDivision(parts[0])
        
        for (i in 1 until parts.size step 2) {
            val operator = parts[i]
            val operand = evaluateMultiplicationDivision(parts[i + 1])
            
            when (operator) {
                "+" -> result += operand
                "-" -> result -= operand
            }
        }
        
        return result
    }
    
    // If no addition/subtraction, evaluate multiplication/division
    return evaluateMultiplicationDivision(expr)
}

// Helper function to evaluate multiplication and division
fun evaluateMultiplicationDivision(expression: String): Double {
    val parts = expression.split("""(?<=\d)([*/^%])(?=\d)""".toRegex())
    if (parts.size > 1) {
        var result = parts[0].toDouble()
        
        for (i in 1 until parts.size step 2) {
            val operator = parts[i]
            val operand = parts[i + 1].toDouble()
            
            when (operator) {
                "*" -> result *= operand
                "/" -> {
                    if (operand == 0.0) throw ArithmeticException("Division by zero")
                    result /= operand
                }
                "^" -> result = result.pow(operand)
                "%" -> result %= operand
            }
        }
        
        return result
    }
    
    // If no operations, convert to double
    return expression.toDouble()
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    CalculatorAppTheme {
        CalculatorApp()
    }
}