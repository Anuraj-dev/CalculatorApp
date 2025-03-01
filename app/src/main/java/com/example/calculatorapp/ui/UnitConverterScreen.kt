package com.example.calculatorapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.calculatorapp.utils.UnitConverter

enum class UnitType { LENGTH, WEIGHT, TEMPERATURE }

@Composable
fun UnitConverterDialog(
    onDismiss: () -> Unit,
    onValueCalculated: (String) -> Unit
) {
    var unitType by remember { mutableStateOf(UnitType.LENGTH) }
    var inputValue by remember { mutableStateOf("") }
    var fromUnit by remember { mutableStateOf("m") }
    var toUnit by remember { mutableStateOf("cm") }
    var result by remember { mutableStateOf("") }
    
    val lengthUnits = listOf("mm", "cm", "m", "km", "in", "ft", "yd", "mi")
    val weightUnits = listOf("mg", "g", "kg", "t", "oz", "lb", "st")
    val temperatureUnits = listOf("C", "F", "K")
    
    // Get current units list based on selected type
    val currentUnits = when (unitType) {
        UnitType.LENGTH -> lengthUnits
        UnitType.WEIGHT -> weightUnits
        UnitType.TEMPERATURE -> temperatureUnits
    }
    
    // Update default unit selections when type changes
    LaunchedEffect(unitType) {
        fromUnit = currentUnits.first()
        toUnit = if (currentUnits.size > 1) currentUnits[1] else currentUnits.first()
        result = ""
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Unit Converter",
                    style = MaterialTheme.typography.headlineSmall
                )
                
                // Unit type selection
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    UnitType.values().forEach { type ->
                        FilterChip(
                            selected = unitType == type,
                            onClick = { unitType = type },
                            label = { Text(type.name) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                // Input value field
                OutlinedTextField(
                    value = inputValue,
                    onValueChange = { 
                        inputValue = it 
                        // Perform conversion when input changes
                        try {
                            if (inputValue.isNotEmpty()) {
                                val value = inputValue.toDoubleOrNull() ?: return@OutlinedTextField
                                result = when (unitType) {
                                    UnitType.LENGTH -> UnitConverter.convertLength(value, fromUnit, toUnit)
                                    UnitType.WEIGHT -> UnitConverter.convertWeight(value, fromUnit, toUnit)
                                    UnitType.TEMPERATURE -> UnitConverter.convertTemperature(value, fromUnit, toUnit)
                                }.toString()
                            } else {
                                result = ""
                            }
                        } catch (e: Exception) {
                            result = "Error: ${e.message}"
                        }
                    },
                    label = { Text("Value") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                // From/To unit selection
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // From unit
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("From")
                        DropdownMenu(
                            expanded = false,
                            onDismissRequest = { },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            // This is just a placeholder to show structure
                        }
                        
                        // Using an exposed dropdown menu would be better but simplifying for now
                        OutlinedCard(
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            DropdownMenuItem(
                                text = { Text(fromUnit) },
                                onClick = { },
                                trailingIcon = { Text("▼", fontSize = 12.sp) }
                            )
                        }
                    }
                    
                    // Arrow indicator
                    Text(
                        text = "→",
                        modifier = Modifier.align(Alignment.CenterVertically),
                        fontSize = 24.sp
                    )
                    
                    // To unit
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("To")
                        OutlinedCard(
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            DropdownMenuItem(
                                text = { Text(toUnit) },
                                onClick = { },
                                trailingIcon = { Text("▼", fontSize = 12.sp) }
                            )
                        }
                    }
                }
                
                // Small note about how to pick units (in real implementation)
                Text(
                    text = "(Tap unit to change)",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Result display
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Result:", style = MaterialTheme.typography.bodyLarge)
                    
                    if (result.isNotEmpty()) {
                        Text(
                            text = "$result $toUnit",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                // Action buttons
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = { 
                            if (result.isNotEmpty()) {
                                onValueCalculated(result)
                                onDismiss()
                            }
                        },
                        enabled = result.isNotEmpty() && !result.startsWith("Error")
                    ) {
                        Text("Use Result")
                    }
                }
            }
        }
    }
}
