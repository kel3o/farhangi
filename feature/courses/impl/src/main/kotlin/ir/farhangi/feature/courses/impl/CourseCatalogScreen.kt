package ir.farhangi.feature.courses.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.Course
import ir.farhangi.core.ui.CourseCard
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState

@Composable
fun CourseCatalogScreen(
    uiState: CoursesUiState,
    onCourseClick: (Course) -> Unit,
    onCategorySelected: (String?) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        CoursesUiState.Loading -> LoadingState(modifier.padding(contentPadding))
        is CoursesUiState.Error -> EmptyState("خطا", uiState.message, modifier.padding(contentPadding))
        is CoursesUiState.Success -> {
            val courses = uiState.practical + uiState.professional
            val categories = courses.map { it.category }.filter { it.isNotBlank() }.distinct()
            Column(modifier.fillMaxSize().padding(top = contentPadding.calculateTopPadding())) {
                if (categories.isNotEmpty()) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = FarhangiSpacing.md),
                        horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs),
                    ) {
                        item {
                            FilterChip(
                                selected = uiState.selectedCategory == null,
                                onClick = { onCategorySelected(null) },
                                label = { Text("همه") },
                            )
                        }
                        items(categories) { category ->
                            FilterChip(
                                selected = uiState.selectedCategory == category,
                                onClick = { onCategorySelected(category) },
                                label = { Text(category) },
                            )
                        }
                    }
                }
                val visible = if (uiState.selectedCategory == null) {
                    courses
                } else {
                    courses.filter { it.category == uiState.selectedCategory }
                }
                if (visible.isEmpty()) {
                    EmptyState("موردی نیست", "در این دسته دوره‌ای نیست.", Modifier.padding(FarhangiSpacing.lg))
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            top = FarhangiSpacing.sm,
                            bottom = contentPadding.calculateBottomPadding() + FarhangiSpacing.lg,
                        ),
                        verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
                    ) {
                        items(visible, key = { it.id }) { course ->
                            CourseCard(
                                course = course,
                                onClick = { onCourseClick(course) },
                                modifier = Modifier.padding(horizontal = FarhangiSpacing.md),
                            )
                        }
                    }
                }
            }
        }
    }
}
