package ir.farhangi.feature.books.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import ir.farhangi.core.designsystem.icon.FarhangiIcons
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.toPersianDigits
import ir.farhangi.core.ui.LoadingState
import ir.farhangi.core.ui.ReadingBackdrop

private const val SECONDARY_ALPHA = 0.7f
private const val SWIPE_THRESHOLD_PX = 80f
private const val FONT_STEP = 2
private const val FONT_MIN_SP = 14
private const val FONT_MAX_SP = 28
private const val LINE_HEIGHT_STEP = 2
private const val LINE_HEIGHT_MIN_SP = 20
private const val LINE_HEIGHT_MAX_SP = 40
private const val WORD_SPACING_STEP_EM = 0.05f
private const val WORD_SPACING_MIN_EM = 0f
private const val WORD_SPACING_MAX_EM = 0.35f
private const val NAV_BUTTON_WEIGHT = 1f
private val ReaderChromeContentPadding = PaddingValues(
    horizontal = FarhangiSpacing.xs,
    vertical = FarhangiSpacing.xxs,
)

@Composable
fun BookReaderScreen(
    uiState: ReaderUiState,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onJumpToPage: (Int) -> Unit,
    onToggleNight: () -> Unit,
    onToggleBookmark: () -> Unit,
    onFontSizeChange: (Int) -> Unit,
    onLineHeightChange: (Int) -> Unit,
    onWordSpacingChange: (Float) -> Unit,
    onBoldChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (uiState.isLoading) {
        LoadingState(modifier = modifier)
        return
    }

    val snackbarHostState = remember { SnackbarHostState() }
    var showJumpDialog by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showSavedMessage by remember { mutableStateOf(false) }
    var dragAccum by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(showSavedMessage) {
        if (showSavedMessage) {
            snackbarHostState.showSnackbar("صفحه ذخیره شد")
            showSavedMessage = false
        }
    }

    val colorScheme = MaterialTheme.colorScheme
    val foreground: Color
    val secondary: Color
    val actionColor: Color
    if (uiState.isNightMode) {
        foreground = colorScheme.inverseOnSurface
        secondary = colorScheme.inverseOnSurface.copy(alpha = SECONDARY_ALPHA)
        actionColor = colorScheme.inverseOnSurface
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(colorScheme.inverseSurface)
                .padding(FarhangiSpacing.md)
                .pointerInput(uiState.page, uiState.totalPages) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            when {
                                dragAccum <= -SWIPE_THRESHOLD_PX -> onNext()
                                dragAccum >= SWIPE_THRESHOLD_PX -> onPrevious()
                            }
                            dragAccum = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            dragAccum += dragAmount
                        },
                    )
                },
        ) {
            ReaderBody(
                uiState = uiState,
                foreground = foreground,
                secondary = secondary,
                actionColor = actionColor,
                onNext = onNext,
                onPrevious = onPrevious,
                onJumpClick = { showJumpDialog = true },
                onToggleNight = onToggleNight,
                onToggleBookmark = {
                    onToggleBookmark()
                    if (!uiState.isBookmarked) showSavedMessage = true
                },
                onSettingsClick = { showSettings = true },
                onBack = onBack,
            )
            SnackbarHost(hostState = snackbarHostState)
        }
    } else {
        foreground = colorScheme.onSurface
        secondary = colorScheme.onSurfaceVariant
        actionColor = colorScheme.primary
        ReadingBackdrop(modifier) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(FarhangiSpacing.md)
                    .pointerInput(uiState.page, uiState.totalPages) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                when {
                                    dragAccum <= -SWIPE_THRESHOLD_PX -> onNext()
                                    dragAccum >= SWIPE_THRESHOLD_PX -> onPrevious()
                                }
                                dragAccum = 0f
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                dragAccum += dragAmount
                            },
                        )
                    },
            ) {
                ReaderBody(
                    uiState = uiState,
                    foreground = foreground,
                    secondary = secondary,
                    actionColor = actionColor,
                    onNext = onNext,
                    onPrevious = onPrevious,
                    onJumpClick = { showJumpDialog = true },
                    onToggleNight = onToggleNight,
                    onToggleBookmark = {
                        onToggleBookmark()
                        if (!uiState.isBookmarked) showSavedMessage = true
                    },
                    onSettingsClick = { showSettings = true },
                    onBack = onBack,
                )
                SnackbarHost(hostState = snackbarHostState)
            }
        }
    }

    if (showJumpDialog) {
        JumpPageDialog(
            totalPages = uiState.totalPages,
            currentPage = uiState.page,
            onDismiss = { showJumpDialog = false },
            onConfirm = { page ->
                onJumpToPage(page)
                showJumpDialog = false
            },
        )
    }
    if (showSettings) {
        ReadingSettingsDialog(
            fontSizeSp = uiState.fontSizeSp,
            lineHeightSp = uiState.lineHeightSp,
            wordSpacingEm = uiState.wordSpacingEm,
            isBold = uiState.isBold,
            onDismiss = { showSettings = false },
            onFontDecrease = {
                onFontSizeChange((uiState.fontSizeSp - FONT_STEP).coerceAtLeast(FONT_MIN_SP))
            },
            onFontIncrease = {
                onFontSizeChange((uiState.fontSizeSp + FONT_STEP).coerceAtMost(FONT_MAX_SP))
            },
            onLineHeightDecrease = {
                onLineHeightChange(
                    (uiState.lineHeightSp - LINE_HEIGHT_STEP).coerceAtLeast(LINE_HEIGHT_MIN_SP),
                )
            },
            onLineHeightIncrease = {
                onLineHeightChange(
                    (uiState.lineHeightSp + LINE_HEIGHT_STEP).coerceAtMost(LINE_HEIGHT_MAX_SP),
                )
            },
            onWordSpacingDecrease = {
                onWordSpacingChange(
                    (uiState.wordSpacingEm - WORD_SPACING_STEP_EM).coerceAtLeast(WORD_SPACING_MIN_EM),
                )
            },
            onWordSpacingIncrease = {
                onWordSpacingChange(
                    (uiState.wordSpacingEm + WORD_SPACING_STEP_EM).coerceAtMost(WORD_SPACING_MAX_EM),
                )
            },
            onBoldChange = onBoldChange,
        )
    }
}

@Composable
private fun ColumnScope.ReaderBody(
    uiState: ReaderUiState,
    foreground: Color,
    secondary: Color,
    actionColor: Color,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onJumpClick: () -> Unit,
    onToggleNight: () -> Unit,
    onToggleBookmark: () -> Unit,
    onSettingsClick: () -> Unit,
    onBack: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(
            onClick = onBack,
            contentPadding = ReaderChromeContentPadding,
            modifier = Modifier
                .weight(1f)
                .heightIn(min = FarhangiSize.touchTargetMin)
                .semantics { contentDescription = "بازگشت از خواننده" },
        ) {
            Text(
                text = "بازگشت",
                color = actionColor,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
            )
        }
        TextButton(
            onClick = onToggleNight,
            contentPadding = ReaderChromeContentPadding,
            modifier = Modifier
                .weight(1f)
                .heightIn(min = FarhangiSize.touchTargetMin)
                .semantics {
                    contentDescription = if (uiState.isNightMode) {
                        "فعال‌سازی حالت روز"
                    } else {
                        "فعال‌سازی حالت شب"
                    }
                },
        ) {
            Text(
                text = if (uiState.isNightMode) "حالت روز" else "حالت شب",
                color = actionColor,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
            )
        }
        TextButton(
            onClick = onSettingsClick,
            contentPadding = ReaderChromeContentPadding,
            modifier = Modifier
                .weight(1f)
                .heightIn(min = FarhangiSize.touchTargetMin)
                .semantics { contentDescription = "تنظیمات خواندن" },
        ) {
            Text(
                text = "تنظیمات",
                color = actionColor,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
            )
        }
    }
    HorizontalDivider(
        modifier = Modifier.padding(vertical = FarhangiSpacing.xxs),
        color = secondary.copy(alpha = 0.35f),
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "خلاصه کتاب ${uiState.bookTitle}",
            style = MaterialTheme.typography.bodyMedium,
            color = foreground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xxs),
        ) {
            Text(
                text = "صفحه ${uiState.page.toPersianDigits()}",
                style = MaterialTheme.typography.labelSmall,
                color = secondary,
            )
            Box(
                modifier = Modifier
                    .size(FarhangiSize.touchTargetMin)
                    .clickable(onClick = onToggleBookmark)
                    .semantics {
                        contentDescription = if (uiState.isBookmarked) {
                            "حذف ذخیره صفحه"
                        } else {
                            "ذخیره صفحه"
                        }
                    },
                contentAlignment = Alignment.CenterStart,
            ) {
                Icon(
                    imageVector = if (uiState.isBookmarked) {
                        FarhangiIcons.Bookmark
                    } else {
                        FarhangiIcons.BookmarkBorder
                    },
                    contentDescription = null,
                    tint = actionColor,
                    modifier = Modifier.size(FarhangiSize.iconDefault),
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(FarhangiSpacing.xs))
    Text(
        text = uiState.pageText,
        style = MaterialTheme.typography.bodyLarge.copy(
            fontSize = uiState.fontSizeSp.sp,
            lineHeight = uiState.lineHeightSp.sp,
            letterSpacing = uiState.wordSpacingEm.em,
            fontWeight = if (uiState.isBold) FontWeight.Bold else FontWeight.Normal,
        ),
        color = foreground,
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs),
    ) {
        FilledTonalButton(
            onClick = onPrevious,
            enabled = uiState.page > 1,
            contentPadding = ReaderChromeContentPadding,
            modifier = Modifier
                .weight(NAV_BUTTON_WEIGHT)
                .heightIn(min = FarhangiSize.touchTargetMin),
        ) {
            ReaderNavLabel("قبلی")
        }
        FilledTonalButton(
            onClick = onJumpClick,
            contentPadding = ReaderChromeContentPadding,
            modifier = Modifier
                .weight(NAV_BUTTON_WEIGHT)
                .heightIn(min = FarhangiSize.touchTargetMin),
        ) {
            ReaderNavLabel("انتخابی")
        }
        FilledTonalButton(
            onClick = onNext,
            enabled = uiState.page < uiState.totalPages,
            contentPadding = ReaderChromeContentPadding,
            modifier = Modifier
                .weight(NAV_BUTTON_WEIGHT)
                .heightIn(min = FarhangiSize.touchTargetMin),
        ) {
            ReaderNavLabel("بعدی")
        }
    }
}

@Composable
private fun ReaderNavLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Clip,
    )
}

@Composable
private fun JumpPageDialog(
    totalPages: Int,
    currentPage: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
) {
    var input by remember { mutableStateOf(currentPage.toString()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("انتخاب صفحه") },
        text = {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it.filter { ch -> ch.isDigit() } },
                label = { Text("شماره صفحه (۱ تا ${totalPages.toPersianDigits()})") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val page = input.toIntOrNull()
                    if (page != null && page in 1..totalPages) onConfirm(page)
                },
            ) { Text("برو") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("انصراف") }
        },
    )
}

@Composable
private fun ReadingSettingsDialog(
    fontSizeSp: Int,
    lineHeightSp: Int,
    wordSpacingEm: Float,
    isBold: Boolean,
    onDismiss: () -> Unit,
    onFontDecrease: () -> Unit,
    onFontIncrease: () -> Unit,
    onLineHeightDecrease: () -> Unit,
    onLineHeightIncrease: () -> Unit,
    onWordSpacingDecrease: () -> Unit,
    onWordSpacingIncrease: () -> Unit,
    onBoldChange: (Boolean) -> Unit,
) {
    val wordSpacingPercent = (wordSpacingEm * 100).toInt()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "تنظیمات",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs)) {
                SettingsStepperRow(
                    label = "اندازه قلم: ${fontSizeSp.toPersianDigits()}",
                    onDecrease = onFontDecrease,
                    onIncrease = onFontIncrease,
                    decreaseDescription = "کوچک‌تر کردن قلم",
                    increaseDescription = "بزرگ‌تر کردن قلم",
                )
                SettingsStepperRow(
                    label = "فاصله سطور: ${lineHeightSp.toPersianDigits()}",
                    onDecrease = onLineHeightDecrease,
                    onIncrease = onLineHeightIncrease,
                    decreaseDescription = "کاهش فاصله سطور",
                    increaseDescription = "افزایش فاصله سطور",
                )
                SettingsStepperRow(
                    label = "فاصله کلمات: ${wordSpacingPercent.toPersianDigits()}٪",
                    onDecrease = onWordSpacingDecrease,
                    onIncrease = onWordSpacingIncrease,
                    decreaseDescription = "کاهش فاصله کلمات",
                    increaseDescription = "افزایش فاصله کلمات",
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = FarhangiSize.touchTargetMin),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs),
                ) {
                    Text(
                        text = "متن پر رنگ (Bold)",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = if (isBold) "بله" else "خیر",
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Switch(
                        checked = isBold,
                        onCheckedChange = onBoldChange,
                    )
                }
            }
        },
        confirmButton = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                TextButton(onClick = onDismiss) { Text("بستن") }
            }
        },
    )
}

@Composable
private fun SettingsStepperRow(
    label: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    decreaseDescription: String,
    increaseDescription: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = FarhangiSize.touchTargetMin),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xxs),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        IconButton(
            onClick = onDecrease,
            modifier = Modifier.semantics { contentDescription = decreaseDescription },
        ) {
            Icon(imageVector = Icons.Outlined.Remove, contentDescription = null)
        }
        IconButton(
            onClick = onIncrease,
            modifier = Modifier.semantics { contentDescription = increaseDescription },
        ) {
            Icon(imageVector = Icons.Outlined.Add, contentDescription = null)
        }
    }
}
