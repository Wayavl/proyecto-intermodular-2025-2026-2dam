package com.paralearn.android.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.paralearn.android.ui.components.ParalearnBottomBar
import com.paralearn.android.ui.screens.algorithm_catalog.AlgorithmCatalogScreen
import com.paralearn.android.ui.screens.algorithm_catalog.AlgorithmCatalogViewModel
import com.paralearn.android.ui.screens.algorithm_lesson.AlgorithmSandboxScreen
import com.paralearn.android.ui.screens.content_detail.ContentDetailViewModel
import com.paralearn.android.ui.screens.course_catalog.CourseCatalogScreen
import com.paralearn.android.ui.screens.course_catalog.CourseCatalogViewModel
import com.paralearn.android.ui.screens.course_lesson.CourseLessonScreen
import com.paralearn.android.ui.screens.course_tree.CourseLessonsTreeScreen
import com.paralearn.android.ui.screens.course_tree.CourseLessonsTreeViewModel
import com.paralearn.android.ui.screens.home.HomeScreen
import com.paralearn.android.ui.screens.home.HomeViewModel
import com.paralearn.android.ui.screens.profile.ProfileScreen
import com.paralearn.android.ui.screens.profile.ProfileViewModel
import com.paralearn.android.ui.screens.settings.SettingsScreen
import com.paralearn.android.ui.screens.settings.SettingsViewModel

private val bottomBarRoutes = setOf("home", "catalog", "algorithms", "profile")

private fun routeToSelectedTab(route: String?): String = when (route) {
    "catalog" -> "courses"
    "algorithms" -> "algorithms"
    "profile" -> "profile"
    else -> "home"
}

private fun navigateToTab(navController: NavHostController, tab: String) {
    val route = when (tab) {
        "courses" -> "catalog"
        "profile" -> "profile"
        "home" -> "home"
        "algorithms" -> "algorithms"
        else -> tab
    }
    navController.navigate(route) {
        popUpTo(navController.graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun AppNavHost(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                ParalearnBottomBar(
                    selectedTab = routeToSelectedTab(currentRoute),
                    onTabSelected = { tab -> navigateToTab(navController, tab) }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable("home") {
                HomeNavScreen(navController = navController)
            }
            composable("algorithms") {
                AlgorithmCatalogNavScreen(navController = navController)
            }
            composable("profile") {
                ProfileNavScreen(navController = navController)
            }
            composable("settings") {
                SettingsNavScreen(navController = navController)
            }
            composable("catalog") {
                CourseCatalogNavScreen(navController = navController)
            }
            composable(
                "lessons_tree/{courseId}",
                arguments = listOf(navArgument("courseId") { type = NavType.StringType })
            ) { backStackEntry ->
                val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
                CourseLessonsTreeNavScreen(navController = navController, courseId = courseId)
            }
            composable(
                "lesson_detail/{lessonId}",
                arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
            ) { backStackEntry ->
                val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
                CourseLessonNavScreen(navController = navController, lessonId = lessonId)
            }
            composable(
                "algorithm/{algorithmId}",
                arguments = listOf(navArgument("algorithmId") { type = NavType.StringType })
            ) { backStackEntry ->
                val algorithmId = backStackEntry.arguments?.getString("algorithmId") ?: ""
                AlgorithmSandboxNavScreen(navController = navController, algorithmId = algorithmId)
            }
        }
    }
}

@Composable
fun HomeNavScreen(navController: NavHostController) {
    HomeScreen(
        viewModel = hiltViewModel<HomeViewModel>(),
        onNavigateToCourse = { courseId -> navController.navigate("lessons_tree/$courseId") },
        onNavigateToAlgorithm = { algorithmId -> navController.navigate("algorithm/$algorithmId") },
        onNavigateToProfile = { navController.navigate("profile") }
    )
}

@Composable
fun AlgorithmCatalogNavScreen(navController: NavHostController) {
    AlgorithmCatalogScreen(
        viewModel = hiltViewModel<AlgorithmCatalogViewModel>(),
        onAlgorithmClick = { algorithmId -> navController.navigate("algorithm/$algorithmId") }
    )
}

@Composable
fun ProfileNavScreen(navController: NavHostController) {
    ProfileScreen(
        viewModel = hiltViewModel<ProfileViewModel>(),
        onNavigateToCourse = { courseId -> navController.navigate("lessons_tree/$courseId") },
        onNavigateToSettings = { navController.navigate("settings") },
        onLoggedOut = { /* Session cleared in ViewModel; MainActivity shows auth */ }
    )
}

@Composable
fun SettingsNavScreen(navController: NavHostController) {
    SettingsScreen(
        viewModel = hiltViewModel<SettingsViewModel>(),
        onBackClick = { navController.popBackStack() }
    )
}

@Composable
fun CourseCatalogNavScreen(navController: NavHostController) {
    CourseCatalogScreen(
        viewModel = hiltViewModel<CourseCatalogViewModel>(),
        onCourseClick = { courseId -> navController.navigate("lessons_tree/$courseId") }
    )
}

@Composable
fun CourseLessonsTreeNavScreen(navController: NavHostController, courseId: String) {
    CourseLessonsTreeScreen(
        courseId = courseId,
        viewModel = hiltViewModel<CourseLessonsTreeViewModel>(),
        onBackClick = { navController.popBackStack() },
        onLessonSelected = { lessonId -> navController.navigate("lesson_detail/$lessonId") }
    )
}

@Composable
fun CourseLessonNavScreen(navController: NavHostController, lessonId: String) {
    CourseLessonScreen(
        lessonId = lessonId,
        viewModel = hiltViewModel<ContentDetailViewModel>(),
        onBackClick = { navController.popBackStack() },
        onLaunchSandbox = { algorithmId -> navController.navigate("algorithm/$algorithmId") }
    )
}

@Composable
fun AlgorithmSandboxNavScreen(navController: NavHostController, algorithmId: String) {
    AlgorithmSandboxScreen(
        algorithmId = algorithmId,
        viewModel = hiltViewModel<ContentDetailViewModel>(),
        onBackClick = { navController.popBackStack() },
        onNavigateToAlgorithm = { id ->
            navController.navigate("algorithm/$id") {
                launchSingleTop = true
            }
        }
    )
}
