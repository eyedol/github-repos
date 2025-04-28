package com.addhen.livefront.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.addhen.livefront.screen.githubrepolist.GithubRepoListScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = GithubRepoListRoute,
    ) {
        composable<GithubRepoListRoute> {
            GithubRepoListScreen()
        }
    }
}
