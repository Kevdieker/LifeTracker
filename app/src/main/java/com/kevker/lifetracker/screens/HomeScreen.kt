package com.kevker.lifetracker.screens


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.kevker.lifetracker.data.LTDatabase
import com.kevker.lifetracker.factories.ViewModelFactory
import com.kevker.lifetracker.data.Repository
import com.kevker.lifetracker.widget.SimpleBottomAppBar
import com.kevker.lifetracker.widget.SimpleTopAppBar

@Composable
fun HomeScreen(
    navController: NavController
) {

    val db = LTDatabase.getDatabase(LocalContext.current)

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = "Home",
                onNavigationIconClick = null
            )
        },
        bottomBar = { SimpleBottomAppBar(navController) }
    ) { innerPadding ->
        Text(modifier = Modifier.padding(innerPadding), text = "hi")
    }
}
