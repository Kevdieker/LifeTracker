package com.kevker.lifetracker.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kevker.lifetracker.enums.Category


@Composable
fun CategoryFilterMenu(
    categories: List<Category>,
    selectedCategory: Category?,
    onCategorySelected: (Category?) -> Unit
) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text("Select Category", modifier = Modifier.padding(20.dp))

        // Option for "No Category"
        CategoryMenuItem(
            isSelected = selectedCategory == null,
            text = "No Category",
            onClick = { onCategorySelected(null) }
        )

        // List of categories
        categories.forEach { category ->
            CategoryMenuItem(
                isSelected = selectedCategory == category,
                text = category.name,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}