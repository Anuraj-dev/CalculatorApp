package com.example.calculatorapp

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

/**
 * A utility class to manage calculation history
 */
class CalculationHistory {
    // Using SnapshotStateList for Compose state management
    private val _history = mutableStateListOf<HistoryItem>()
    val history: List<HistoryItem> get() = _history
    
    fun addCalculation(expression: String, result: String) {
        _history.add(HistoryItem(expression, result))
        // Keep history size manageable
        if (_history.size > 10) {
            _history.removeAt(0)
        }
    }
    
    fun clearHistory() {
        _history.clear()
    }
}

/**
 * Data class to represent a calculation history item
 */
data class HistoryItem(
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)
