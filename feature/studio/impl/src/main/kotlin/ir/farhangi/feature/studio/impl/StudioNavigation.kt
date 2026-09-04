package ir.farhangi.feature.studio.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import ir.farhangi.core.navigation.EntryProviderInstaller
import ir.farhangi.core.navigation.Navigator
import ir.farhangi.feature.books.api.BookDetailRoute
import ir.farhangi.feature.competitions.api.ContestDetailRoute
import ir.farhangi.feature.courses.api.CourseDetailRoute
import ir.farhangi.feature.magazine.api.ArticleDetailRoute
import ir.farhangi.feature.studio.api.OrgInboxRoute
import ir.farhangi.feature.studio.api.OrgMessageDetailRoute
import ir.farhangi.feature.studio.api.ReportsRoute
import ir.farhangi.feature.studio.api.RolesRoute
import ir.farhangi.feature.studio.api.StudioArticleEditorRoute
import ir.farhangi.feature.studio.api.StudioArticlesRoute
import ir.farhangi.feature.studio.api.StudioBookEditorRoute
import ir.farhangi.feature.studio.api.StudioBooksRoute
import ir.farhangi.feature.studio.api.StudioContestEditorRoute
import ir.farhangi.feature.studio.api.StudioContestStatsRoute
import ir.farhangi.feature.studio.api.StudioContestsRoute
import ir.farhangi.feature.studio.api.StudioCourseEditorRoute
import ir.farhangi.feature.studio.api.StudioCoursesRoute
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
            onManageContests = { navigator.navigate(StudioContestsRoute) },
        )
    }
    entry<StudioBooksRoute> {
        StudioCatalogRoute(
            kind = StudioCatalogKind.BOOK,
            title = "کتاب‌ها",
            onAdd = { navigator.navigate(StudioBookEditorRoute()) },
            onOpen = { navigator.navigate(BookDetailRoute(it)) },
            onEdit = { navigator.navigate(StudioBookEditorRoute(it)) },
            onBack = { navigator.pop() },
        )
    }
    entry<StudioBookEditorRoute> { key ->
        val viewModel: StudioBookEditorViewModel =
            hiltViewModel(key = key.bookId.ifBlank { NEW_EDITOR_KEY })
        LaunchedEffect(key.bookId) { viewModel.load(key.bookId) }
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        StudioBookEditorScreen(
            uiState = uiState,
            onDraftChange = viewModel::updateDraft,
            onToggleCategory = viewModel::toggleCategory,
            onCoverPicked = viewModel::onCoverPicked,
            onPublish = viewModel::publish,
            onBack = {
                viewModel.clearStatus()
                navigator.pop()
            },
            onPublishedConfirmed = {
                viewModel.clearStatus()
                navigator.pop()
            },
        )
    }
    entry<StudioCoursesRoute> {
        StudioCatalogRoute(
            kind = StudioCatalogKind.COURSE,
            title = "آموزش‌ها",
            onAdd = { navigator.navigate(StudioCourseEditorRoute()) },
            onOpen = { navigator.navigate(CourseDetailRoute(it)) },
            onEdit = { navigator.navigate(StudioCourseEditorRoute(it)) },
            onBack = { navigator.pop() },
        )
    }
    entry<StudioCourseEditorRoute> { key ->
        val viewModel: StudioCourseEditorViewModel =
            hiltViewModel(key = key.courseId.ifBlank { NEW_EDITOR_KEY })
        LaunchedEffect(key.courseId) { viewModel.load(key.courseId) }
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        StudioCourseEditorScreen(
            uiState = uiState,
            onDraftChange = viewModel::updateDraft,
            onCoverPicked = viewModel::onCoverPicked,
            onAddSection = viewModel::addSection,
            onRemoveSection = viewModel::removeSection,
            onUpdateSection = viewModel::updateSection,
            onPublish = viewModel::publish,
            onBack = {
                viewModel.clearStatus()
                navigator.pop()
            },
            onPublishedConfirmed = {
                viewModel.clearStatus()
                navigator.pop()
            },
        )
    }
    entry<StudioArticlesRoute> {
        StudioCatalogRoute(
            kind = StudioCatalogKind.ARTICLE,
            title = "مطالب مجله",
            onAdd = { navigator.navigate(StudioArticleEditorRoute()) },
            onOpen = { navigator.navigate(ArticleDetailRoute(it)) },
            onEdit = { navigator.navigate(StudioArticleEditorRoute(it)) },
            onBack = { navigator.pop() },
        )
    }
    entry<StudioArticleEditorRoute> { key ->
        val viewModel: StudioArticleEditorViewModel =
            hiltViewModel(key = key.articleId.ifBlank { NEW_EDITOR_KEY })
        LaunchedEffect(key.articleId) { viewModel.load(key.articleId) }
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        StudioArticleEditorScreen(
            uiState = uiState,
            onDraftChange = viewModel::updateDraft,
            onCoverPicked = viewModel::onCoverPicked,
            onPublish = viewModel::publish,
            onBack = {
                viewModel.clearStatus()
                navigator.pop()
            },
            onPublishedConfirmed = {
                viewModel.clearStatus()
                navigator.pop()
            },
        )
    }
    entry<StudioContestsRoute> {
        StudioCatalogRoute(
            kind = StudioCatalogKind.CONTEST,
            title = "مسابقه‌ها",
            extraActionLabel = "آمار",
            onAdd = { navigator.navigate(StudioContestEditorRoute()) },
            onOpen = { navigator.navigate(ContestDetailRoute(it)) },
            onEdit = { navigator.navigate(StudioContestEditorRoute(it)) },
            onExtraAction = { navigator.navigate(StudioContestStatsRoute(it)) },
            onBack = { navigator.pop() },
        )
    }
    entry<StudioContestEditorRoute> { key ->
        val viewModel: StudioContestEditorViewModel =
            hiltViewModel(key = key.contestId.ifBlank { NEW_EDITOR_KEY })
        LaunchedEffect(key.contestId) { viewModel.load(key.contestId) }
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        StudioContestEditorScreen(
            uiState = uiState,
            onDraftChange = viewModel::updateDraft,
            onAddQuestion = viewModel::addQuestion,
            onRemoveQuestion = viewModel::removeQuestion,
            onUpdateQuestion = viewModel::updateQuestion,
            onPublish = viewModel::publish,
            onBack = {
                viewModel.clearStatus()
                navigator.pop()
            },
            onPublishedConfirmed = {
                viewModel.clearStatus()
                navigator.pop()
            },
        )
    }
    entry<StudioContestStatsRoute> { key ->
        val viewModel: StudioContestStatsViewModel = hiltViewModel(key = key.contestId)
        LaunchedEffect(key.contestId) { viewModel.load(key.contestId) }
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        StudioContestStatsScreen(
            uiState = uiState,
            onBack = { navigator.pop() },
        )
    }
    entry<OrgInboxRoute> {
        val viewModel: OrgInboxViewModel = hiltViewModel()
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        OrgInboxScreen(
            uiState = uiState,
            onSend = viewModel::send,
            onMarkRead = viewModel::markRead,
            onView = { navigator.navigate(OrgMessageDetailRoute(it)) },
            onBack = { navigator.pop() },
        )
    }
    entry<OrgMessageDetailRoute> { key ->
        val viewModel: OrgMessageDetailViewModel = hiltViewModel()
        LaunchedEffect(key.messageId) { viewModel.load(key.messageId) }
        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        OrgMessageDetailScreen(
            uiState = uiState,
            onMarkRead = viewModel::markRead,
            onSendReply = viewModel::sendReply,
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

@Composable
private fun StudioCatalogRoute(
    kind: StudioCatalogKind,
    title: String,
    onAdd: () -> Unit,
    onOpen: (String) -> Unit,
    onEdit: (String) -> Unit,
    onBack: () -> Unit,
    extraActionLabel: String? = null,
    onExtraAction: ((String) -> Unit)? = null,
) {
    val viewModel: StudioCatalogViewModel = hiltViewModel(key = kind.name)
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(kind, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.refresh(kind)
        }
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    StudioItemList(
        title = title,
        uiState = uiState,
        onAdd = onAdd,
        onOpen = onOpen,
        onEdit = onEdit,
        onDelete = { viewModel.delete(kind, it) },
        onBack = onBack,
        extraActionLabel = extraActionLabel,
        onExtraAction = onExtraAction,
    )
}

private const val NEW_EDITOR_KEY = "new"
