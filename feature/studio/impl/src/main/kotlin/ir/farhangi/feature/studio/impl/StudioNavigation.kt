package ir.farhangi.feature.studio.impl

import androidx.compose.runtime.getValue
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
import ir.farhangi.feature.studio.api.CreateArticleRoute
import ir.farhangi.feature.studio.api.CreateBookRoute
import ir.farhangi.feature.studio.api.CreateContestRoute
import ir.farhangi.feature.studio.api.CreateCourseRoute
import ir.farhangi.feature.studio.api.OrgInboxRoute
import ir.farhangi.feature.studio.api.ReportsRoute
import ir.farhangi.feature.studio.api.RolesRoute
import ir.farhangi.feature.studio.api.StudioHomeRoute

@Module
@InstallIn(ActivityRetainedComponent::class)
object StudioNavigationModule {
    @Provides
    @IntoSet
    fun provideStudioEntryInstaller(navigator: Navigator): EntryProviderInstaller = {
        studioEntries(navigator)
    }
}

fun EntryProviderScope<NavKey>.studioEntries(navigator: Navigator) {
    entry<StudioHomeRoute> {
        StudioHomeScreen(
            onCreateBook = { navigator.navigate(CreateBookRoute) },
            onCreateCourse = { navigator.navigate(CreateCourseRoute) },
            onCreateArticle = { navigator.navigate(CreateArticleRoute) },
            onCreateContest = { navigator.navigate(CreateContestRoute) },
        )
    }
    entry<CreateBookRoute> {
        val viewModel: StudioViewModel = hiltViewModel()
        val status by viewModel.status.collectAsStateWithLifecycle()
        SimpleCreateScreen(
            title = "کتاب تازه",
            subtitle = "عنوان، نویسنده و متن صفحه اول را وارد کنید.",
            onSubmit = viewModel::createBook,
            onBack = { navigator.pop() },
            statusMessage = status,
        )
    }
    entry<CreateCourseRoute> {
        val viewModel: StudioViewModel = hiltViewModel()
        val status by viewModel.status.collectAsStateWithLifecycle()
        SimpleCreateScreen(
            title = "آموزش تازه",
            subtitle = "عنوان، دسته و متن جلسه را وارد کنید.",
            onSubmit = viewModel::createCourse,
            onBack = { navigator.pop() },
            statusMessage = status,
        )
    }
    entry<CreateArticleRoute> {
        val viewModel: StudioViewModel = hiltViewModel()
        val status by viewModel.status.collectAsStateWithLifecycle()
        SimpleCreateScreen(
            title = "مطلب مجله",
            subtitle = "عنوان، نام دسته مجله و متن را وارد کنید.",
            onSubmit = viewModel::createArticle,
            onBack = { navigator.pop() },
            statusMessage = status,
        )
    }
    entry<CreateContestRoute> {
        val viewModel: StudioViewModel = hiltViewModel()
        val status by viewModel.status.collectAsStateWithLifecycle()
        SimpleCreateScreen(
            title = "مسابقه تازه",
            subtitle = "عنوان، دسته و صورت سؤال نمونه را وارد کنید.",
            onSubmit = viewModel::createContest,
            onBack = { navigator.pop() },
            statusMessage = status,
        )
    }
    entry<OrgInboxRoute> {
        val viewModel: OrgInboxViewModel = hiltViewModel()
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        OrgInboxScreen(
            uiState = uiState,
            onSend = viewModel::send,
            onBack = { navigator.pop() },
        )
    }
    entry<ReportsRoute> {
        val viewModel: ReportsViewModel = hiltViewModel()
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        ReportsScreen(uiState = uiState, onBack = { navigator.pop() })
    }
    entry<RolesRoute> {
        val viewModel: RolesViewModel = hiltViewModel()
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        RolesScreen(
            uiState = uiState,
            onRoleChange = viewModel::updateRole,
            onBack = { navigator.pop() },
        )
    }
}
