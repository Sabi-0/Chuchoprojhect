package com.example.chuchoprojhect.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChips(
    selectedFilter: String?,
    onFilterSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val filters = listOf(
        null to "Todos",
        "Ciudad" to "Ciudad",
        "Casa" to "Casa",
        "Grande" to "Grande",
        "Pequeño" to "Pequeño"
    )

    Row(modifier = modifier.padding(8.dp)) {
        filters.forEach { (value, label) ->
            FilterChip(
                selected = selectedFilter == value,
                onClick = { onFilterSelected(value) },
                modifier = Modifier.padding(end = 8.dp),
                label = { Text(label) }
            )
        }
    }
}