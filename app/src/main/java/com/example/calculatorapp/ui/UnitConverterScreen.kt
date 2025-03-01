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

@OptIn(ExperimentalMaterial3Api::class)
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
    
    // Define unit options based on type
    val lengthUnits = listOf("mm", "cm", "m", "km", "in", "ft", "yd", "mi")
    val weightUnits = listOf("mg", "g", "kg", "t", "oz", "lb", "st")
    val temperatureUnits = listOf("C", "F", "K")
    
    // Get current units list based on selected type
    val currentUnits = when (unitType) {
        UnitType.LENGTH -> lengthUnits
        UnitType.WEIGHT -> weightUnits
        UnitType.TEMPERATURE -> temperatureUnits
    }
    
    // Dropdown expanded states
    var fromDropdownExpanded by remember { mutableStateOf(false) }
    var toDropdownExpanded by remember { mutableStateOf(false) }
    
    // Update default unit selections when type changes
    LaunchedEffect(unitType) {
        fromUnit = currentUnits.first()
        toUnit = if (currentUnits.size > 1) currentUnits[1] else currentUnits.first()
        result = ""
        // Reset input to avoid confusion with previous conversion
        inputValue = ""
    }
    
    // Calculate result when inputs change
    fun calculateResult() {
        try {
            if (inputValue.isNotEmpty()) {
                val value = inputValue.toDoubleOrNull() ?: return
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
                        calculateResult()
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
                        
                        // Exposed dropdown menu
                        ExposedDropdownMenuBox(
                            expanded = fromDropdownExpanded,
                            onExpandedChange = { fromDropdownExpanded = !fromDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = fromUnit,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fromDropdownExpanded) },
                                modifier = Modifier.menuAnchor()
                            )
                            
                            ExposedDropdownMenu(
                                expanded = fromDropdownExpanded,
                                onDismissRequest = { fromDropdownExpanded = false }
                            ) {
                                currentUnits.forEach { unit ->
                                    DropdownMenuItem(
                                        text = { Text(unit) },
                                        onClick = { 
                                            fromUnit = unit
                                            fromDropdownExpanded = false
                                            calculateResult()
                                        }
                                    )
                                }
                            }
                        }
                    }
                    
                    // Arrow indicator
                    Text(
                        text = "→",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(horizontal = 8.dp),
                        fontSize = 24.sp
                    )
                    
                    // To unit
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("To")
                        
                        // Exposed dropdown menu
                        ExposedDropdownMenuBox(
                            expanded = toDropdownExpanded,
                            onExpandedChange = { toDropdownExpanded = !toDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = toUnit,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = toDropdownExpanded) },
                                modifier = Modifier.menuAnchor()
                            )
                            
                            ExposedDropdownMenu(
                                expanded = toDropdownExpanded,
                                onDismissRequest = { toDropdownExpanded = false }
                            ) {
                                currentUnits.forEach { unit ->
                                    DropdownMenuItem(
                                        text = { Text(unit) },
                                        onClick = { 
                                            toUnit = unit
                                            toDropdownExpanded = false
                                            calculateResult()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Result display
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Result",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = if (result.isNotEmpty()) "$result $toUnit" else "—",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
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
                            if (result.isNotEmpty() && !result.startsWith("Error")) {
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
