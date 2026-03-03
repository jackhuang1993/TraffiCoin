package com.example.trafficoin.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.trafficoin.ui.coin.CoinRoot
import com.example.trafficoin.ui.flight.FlightRoot
import com.example.trafficoin.ui.navigation.NavigationScreen

/**
 * @author Jack
 */
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavigationScreen.Flight.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavigationScreen.Flight.route) { FlightRoot() }
            composable(NavigationScreen.Coin.route) { CoinRoot() }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        NavigationScreen.items.forEach { screen ->
            NavigationBarItem(
                label = { Text(screen.title) },
                icon = {
                    Icon(
                        painter = painterResource(id = screen.iconRes),
                        contentDescription = screen.title
                    )
                },
                // 判斷目前是否選中此分頁
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBottomNavigationBar() {
    MaterialTheme {
        BottomNavigationBar(rememberNavController())
    }
}