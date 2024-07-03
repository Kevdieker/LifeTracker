package com.kevker.lifetracker.widget

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopAppBar(
    title: String,
    navigationIcon: ImageVector? = Icons.AutoMirrored.Filled.ArrowBack,
    onNavigationIconClick: (() -> Unit)? = null,
    onDateIconClick: (() -> Unit)? = null,
    onSettingsIconClick: (() -> Unit)? = null
) {
    CenterAlignedTopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            if (navigationIcon != null && onNavigationIconClick != null) {
                IconButton(onClick = onNavigationIconClick) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = "Navigation Icon"
                    )
                }
            }
        },
        actions = {
            if (onDateIconClick != null) {
                IconButton(onClick = onDateIconClick) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Date Icon"
                    )
                }
            }
            if (onSettingsIconClick != null) {
                IconButton(onClick = onSettingsIconClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings Icon"
                    )
                }
            }
        }
    )
}
