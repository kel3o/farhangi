package ir.farhangi.feature.courses.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.Course
import ir.farhangi.core.ui.CourseCard
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState
import ir.farhangi.core.ui.SectionHeader

@Composable
fun CoursesScreen(
    uiState: CoursesUiState,
    onCourseClick: (Course) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        CoursesUiState.Loading -> LoadingState(modifier.padding(contentPadding))
        is CoursesUiState.Error -> EmptyState("خطا", uiState.message, modifier.padding(contentPadding))
        is CoursesUiState.Success -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = contentPadding.calculateTopPadding() + FarhangiSpacing.sm,
                bottom = contentPadding.calculateBottomPadding() + FarhangiSpacing.lg,
            ),
            verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
        ) {
            item { SectionHeader(title = "یادگیری عملی") }
            items(uiState.practical, key = { it.id }) { course ->
                CourseCard(
                    course = course,
                    onClick = { onCourseClick(course) },
                    modifier = Modifier.padding(horizontal = FarhangiSpacing.md),
                )
            }
            item { SectionHeader(title = "دوره‌های تخصصی") }
            items(uiState.professional, key = { it.id }) { course ->
                CourseCard(
                    course = course,
                    onClick = { onCourseClick(course) },
                    modifier = Modifier.padding(horizontal = FarhangiSpacing.md),
                )
            }
        }
    }
}