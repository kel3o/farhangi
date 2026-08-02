package ir.farhangi.feature.courses.impl

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import ir.farhangi.core.navigation.EntryProviderInstaller
import ir.farhangi.core.navigation.Navigator
import ir.farhangi.feature.courses.api.CourseDetailRoute
import ir.farhangi.feature.courses.api.CoursesRoute

@Module
@InstallIn(ActivityRetainedComponent::class)
object CoursesNavigationModule {
    @Provides
    @IntoSet
    fun provideCoursesEntryInstaller(navigator: Navigator): EntryProviderInstaller = {
        coursesEntries(navigator)
    }
}

fun EntryProviderScope<NavKey>.coursesEntries(navigator: Navigator) {
    entry<CoursesRoute> {
        val viewModel: CoursesViewModel = hiltViewModel()
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        CoursesScreen(
            uiState = uiState,
            onCourseClick = { navigator.navigate(CourseDetailRoute(it.id)) },
        )
    }
    entry<CourseDetailRoute> { key ->
        val viewModel: CourseDetailViewModel = hiltViewModel()
        LaunchedEffect(key.courseId) { viewModel.load(key.courseId) }
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        CourseDetailScreen(
            uiState = uiState,
            onCompleteSection = viewModel::completeSection,
            onBack = { navigator.pop() },
        )
    }
}