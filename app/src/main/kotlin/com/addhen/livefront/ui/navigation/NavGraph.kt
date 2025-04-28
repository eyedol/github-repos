package com.addhen.livefront.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.addhen.livefront.screen.githubrepodetail.GithubRepoDetailScreen
import com.addhen.livefront.screen.githubrepolist.GithubRepoListScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = GithubRepoListRoute,
    ) {
        composable<GithubRepoListRoute> {
            GithubRepoListScreen(
                viewModel = hiltViewModel(),
                onRepoClick = { repoId ->
                    navController.navigate(GithubRepoDetailRoute(repoId))
                }
            )
        }

        composable<GithubRepoDetailRoute> { backStackEntry ->
            GithubRepoDetailScreen(
                viewModel = hiltViewModel(),
                onBackClick = {
                    // Checking if the current back stack entry is resumed to avoid an issue when the
                    // back button is double pressed in succession which causes the display to show a
                    // blank screen with no view components on it.
                    // See: https://github.com/google/accompanist/issues/1408#issuecomment-1673011548
                    if (navController.currentBackStackEntry?.lifecycle?.currentState == RESUMED) {
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}
