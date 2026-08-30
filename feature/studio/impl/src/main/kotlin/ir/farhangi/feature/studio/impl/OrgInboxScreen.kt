package ir.farhangi.feature.studio.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.OrgInboxRecipient
import ir.farhangi.core.model.OrgMessage
import ir.farhangi.core.model.persianLabel
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState

private const val MESSAGE_PREVIEW_LINES = 2

sealed interface OrgInboxUiState {
    data object Loading : OrgInboxUiState
    data class Success(val messages: List<OrgMessage>, val status: String? = null) : OrgInboxUiState
    data class Error(val message: String) : OrgInboxUiState
}

@Composable
fun OrgInboxScreen(
    uiState: OrgInboxUiState,
    onSend: (String, String, OrgInboxRecipient) -> Unit,
    onMarkRead: (String) -> Unit,
    onView: (String) -> Unit,
    onBack: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        OrgInboxUiState.Loading -> LoadingState(modifier.padding(contentPadding))
        is OrgInboxUiState.Error -> EmptyState("خطا", uiState.message, modifier.padding(contentPadding))
        is OrgInboxUiState.Success -> {
            var title by rememberSaveable { mutableStateOf("") }
            var body by rememberSaveable { mutableStateOf("") }
            var recipientName by rememberSaveable {
                mutableStateOf(OrgInboxRecipient.CULTURAL_DEPUTY.name)
            }
            val recipient = runCatching { OrgInboxRecipient.valueOf(recipientName) }
                .getOrDefault(OrgInboxRecipient.CULTURAL_DEPUTY)
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
                item { Text("صندوق سازمانی", style = MaterialTheme.typography.headlineSmall) }
                item {
                    Text(
                        "پیام‌ها فقط بین سطح سازمانی و مدیرکل ردوبدل می‌شود.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("عنوان") },
                    )
                }
                item {
                    Text("گیرنده", style = MaterialTheme.typography.labelLarge)
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs)) {
                        items(OrgInboxRecipient.entries) { option ->
                            FilterChip(
                                selected = recipient == option,
                                onClick = { recipientName = option.name },
                                label = { Text(option.persianLabel()) },
                            )
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = body,
                        onValueChange = { body = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("متن") },
                    )
                }
                item {
                    Button(
                        onClick = {
                            onSend(title, body, recipient)
                            title = ""
                            body = ""
                        },
                        enabled = title.isNotBlank() && body.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().heightIn(min = FarhangiSize.touchTargetMin),
                    ) { Text("ارسال") }
                }
                uiState.status?.let { item { Text(it, color = MaterialTheme.colorScheme.primary) } }
                items(uiState.messages, key = { it.id }) { message ->
                    OrgMessageListCard(
                        message = message,
                        onMarkRead = { onMarkRead(message.id) },
                        onView = { onView(message.id) },
                    )
                }
                item {
                    Button(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth().heightIn(min = FarhangiSize.touchTargetMin),
                    ) {
                        Text("بازگشت")
                    }
                }
            }
        }
    }
}

@Composable
private fun OrgMessageListCard(
    message: OrgMessage,
    onMarkRead: () -> Unit,
    onView: () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            Modifier.padding(FarhangiSpacing.md),
            verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs),
        ) {
            Text(
                "${message.fromName} · ${message.fromRole.persianLabel()}",
                style = MaterialTheme.typography.labelMedium,
            )
            Text(message.title, style = MaterialTheme.typography.titleSmall)
            Text(
                text = "گیرنده: ${message.recipient.persianLabel()}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = message.body,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = MESSAGE_PREVIEW_LINES,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs),
            ) {
                OutlinedButton(
                    onClick = onMarkRead,
                    enabled = !message.isRead,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = FarhangiSize.touchTargetMin),
                ) {
                    Text(if (message.isRead) "خوانده شده" else "خوانده شد")
                }
                Button(
                    onClick = onView,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = FarhangiSize.touchTargetMin),
                ) {
                    Text("مشاهده")
                }
            }
        }
    }
}
