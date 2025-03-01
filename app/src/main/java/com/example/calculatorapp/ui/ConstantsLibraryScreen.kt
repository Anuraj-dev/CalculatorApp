package com.example.calculatorapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.calculatorapp.utils.ScientificConstants

data class ConstantItem(
    val name: String,
    val symbol: String, 
    val value: Double,
    val unit: String = "",
    val description: String = ""
)

@Composable
fun ConstantsLibraryDialog(
    onDismiss: () -> Unit,
    onConstantSelected: (Double) -> Unit
) {
    // Prepare constants list
    val constants = remember {
        listOf(
            ConstantItem("Pi", "π", ScientificConstants.PI, "", "Ratio of circumference to diameter"),
            ConstantItem("Euler's Number", "e", ScientificConstants.E, "", "Base of natural logarithm"),
            ConstantItem("Golden Ratio", "φ", ScientificConstants.GOLDEN_RATIO, "", "Approx. 1.618..."),
            ConstantItem("Euler-Mascheroni", "γ", ScientificConstants.EULER_MASCHERONI, "", "Approx. 0.577..."),
            ConstantItem("Square Root of 2", "√2", ScientificConstants.SQRT_2, "", "Approx. 1.414..."),
            ConstantItem("Planck Constant", "h", ScientificConstants.PLANCK_CONSTANT, "J⋅s", "Base quantum value"),
            ConstantItem("Avogadro's Number", "N_A", ScientificConstants.AVOGADRO_NUMBER, "mol⁻¹", "Particles per mole"),
            ConstantItem("Speed of Light", "c", ScientificConstants.SPEED_OF_LIGHT, "m/s", "In vacuum"),
            ConstantItem("Gravitational Constant", "G", ScientificConstants.GRAVITATIONAL_CONSTANT, "N⋅m²/kg²", "Newton's constant"),
            ConstantItem("Boltzmann Constant", "k_B", ScientificConstants.BOLTZMANN_CONSTANT, "J/K", "Relates temperature to energy")
        )
    }
    
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredConstants = if (searchQuery.isEmpty()) {
        constants
    } else {
        constants.filter { it.name.contains(searchQuery, ignoreCase = true) || 
                           it.symbol.contains(searchQuery, ignoreCase = true) }
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(max = 500.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Constants Library",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search constants") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                
                Divider()
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(filteredConstants) { constant ->
                        ConstantListItem(
                            constant = constant,
                            onClick = { 
                                onConstantSelected(constant.value)
                                onDismiss()
                            }
                        )
                    }
                }
                
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

@Composable
fun ConstantListItem(
    constant: ConstantItem,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = constant.name,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = constant.symbol,
                fontWeight = FontWeight.Bold
            )
        }
        
        Text(
            text = constant.value.toString() + if (constant.unit.isNotEmpty()) " ${constant.unit}" else "",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp)
        )
        
        if (constant.description.isNotEmpty()) {
            Text(
                text = constant.description,
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
    
    Divider()
}
