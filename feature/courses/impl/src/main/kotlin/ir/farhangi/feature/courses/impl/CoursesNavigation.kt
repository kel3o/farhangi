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
import ir.farhangi.core.model.CourseType
import ir.farhangi.core.navigation.EntryProviderInstaller
import ir.farhangi.core.navigation.Navigator
import ir.farhangi.feature.courses.api.CourseDetailRoute
import ir.farhangi.feature.courses.api.CoursesRoute
import ir.farhangi.feature.courses.api.LessonRoute
import ir.farhangi.feature.courses.api.PracticalCatalogRoute
import ir.farhangi.feature.courses.api.ProfessionalCatalogRoute

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
        CoursesScreen(
            onProfessionalClick = { navigator.navigate(ProfessionalCatalogRoute) },
            onPracticalClick = { navigator.navigate(PracticalCatalogRoute) },
        )
    }
    entry<ProfessionalCatalogRoute> {
        val viewModel: CourseCatalogViewModel = hiltViewModel()
        LaunchedEffect(Unit) { viewModel.load(CourseType.PROFESSIONAL) }
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        CourseCatalogScreen(
            uiState = uiState,
            onCourseClick = { navigator.navigate(CourseDetailRoute(it.id)) },
            onCategorySelected = viewModel::selectCategory,
        )
    }
    entry<PracticalCatalogRoute> {
        val viewModel: CourseCatalogViewModel = hiltViewModel()
        LaunchedEffect(Unit) { viewModel.load(CourseType.PRACTICAL) }
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        CourseCatalogScreen(
            uiState = uiState,
            onCourseClick = { navigator.navigate(CourseDetailRoute(it.id)) },
            onCategorySelected = viewModel::selectCategory,
        )
    }
    entry<CourseDetailRoute> { key ->
        val viewModel: CourseDetailViewModel = hiltViewModel()
        LaunchedEffect(key.courseId) { viewModel.load(key.courseId) }
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        CourseDetailScreen(
            uiState = uiState,
            onOpenLesson = { sectionId -> navigator.navigate(LessonRoute(key.courseId, sectionId)) },
            onBack = { navigator.pop() },
        )
    }
    entry<LessonRoute> { key ->
        val viewModel: LessonViewModel = hiltViewModel()
        LaunchedEffect(key.courseId, key.sectionId) { viewModel.load(key.courseId, key.sectionId) }
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        LessonScreen(
            uiState = uiState,
            onToggleCompleted = viewModel::toggleCompleted,
            onPreviousLesson = {
                val success = uiState as? LessonUiState.Success
                val previousId = success?.previousSectionId
                if (previousId != null) {
                    navigator.pop()
                    navigator.navigate(LessonRoute(key.courseId, previousId))
                }
            },
            onNextLesson = {
                val success = uiState as? LessonUiState.Success
                val nextId = success?.nextSectionId
                if (nextId != null) {
                    navigator.pop()
                    navigator.navigate(LessonRoute(key.courseId, nextId))
                }
            },
            onBack = { navigator.pop() },
        )
    }
}
