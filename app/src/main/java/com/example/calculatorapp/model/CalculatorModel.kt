package com.example.calculatorapp.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Stack

/**
 * Represents a calculator operation for history
 */
data class CalculationEntry(
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Calculator state model
 */
class CalculatorModel {
    var memory by mutableStateOf(0.0)
    var calculationHistory = mutableListOf<CalculationEntry>()
    
    // For undo/redo functionality
    private val undoStack = Stack<String>()
    private val redoStack = Stack<String>()
    
    fun memoryStore(value: Double) {
        memory = value
    }
    
    fun memoryAdd(value: Double) {
        memory += value
    }
    
    fun memorySubtract(value: Double) {
        memory -= value
    }
    
    fun memoryClear() {
        memory = 0.0
    }
    
    fun addToHistory(expression: String, result: String) {
        calculationHistory.add(CalculationEntry(expression, result))
        // Limit history size if needed
        if (calculationHistory.size > 100) {
            calculationHistory.removeAt(0)
        }
    }
    
    fun clearHistory() {
        calculationHistory.clear()
    }
    
    fun pushToUndo(state: String) {
        undoStack.push(state)
        // Clear redo stack when new action is performed
        redoStack.clear()
    }
    
    fun undo(): String? {
        if (undoStack.isEmpty()) return null
        val current = undoStack.pop()
        redoStack.push(current)
        return if (undoStack.isEmpty()) "" else undoStack.peek()
    }
    
    fun redo(): String? {
        if (redoStack.isEmpty()) return null
        val state = redoStack.pop()
        undoStack.push(state)
        return state
    }
    
    fun canUndo(): Boolean = undoStack.isNotEmpty()
    
    fun canRedo(): Boolean = redoStack.isNotEmpty()
}
