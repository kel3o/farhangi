package ir.farhangi.feature.studio.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ir.farhangi.core.designsystem.icon.FarhangiIcons
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.ui.HubTile

@Composable
fun StudioHomeScreen(
    onManageContests: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier,
) {
    var showUnderDesignDialog by rememberSaveable { mutableStateOf(false) }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = FarhangiSpacing.md,
            end = FarhangiSpacing.md,
            top = contentPadding.calculateTopPadding() + FarhangiSpacing.sm,
            bottom = contentPadding.calculateBottomPadding() + FarhangiSpacing.lg,
        ),
        verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
    ) {
        item {
            HubTile(
                title = "مدیریت کتاب‌ها",
                icon = FarhangiIcons.Books,
                onClick = { showUnderDesignDialog = true },
            )
        }
        item {
            HubTile(
                title = "مدیریت آموزش‌ها",
                icon = FarhangiIcons.Courses,
                onClick = { showUnderDesignDialog = true },
            )
        }
        item {
            HubTile(
                title = "مدیریت مقالات",
                icon = FarhangiIcons.Magazine,
                onClick = { showUnderDesignDialog = true },
            )
        }
        item {
            HubTile(
                title = "مدیریت مسابقات",
                icon = FarhangiIcons.Competitions,
                onClick = onManageContests,
            )
        }
    }
    if (showUnderDesignDialog) {
        AlertDialog(
            onDismissRequest = { showUnderDesignDialog = false },
            text = { Text("این بخش در دست طراحی است.") },
            confirmButton = {
                TextButton(onClick = { showUnderDesignDialog = false }) {
                    Text("باشه")
                }
            },
        )
    }
}
