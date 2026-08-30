package ir.farhangi.feature.studio.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.OrgMessage
import ir.farhangi.core.model.persianLabel
import ir.farhangi.core.ui.ContentImage
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState

sealed interface OrgMessageDetailUiState {
    data object Loading : OrgMessageDetailUiState
    data class Success(val message: OrgMessage, val status: String? = null) : OrgMessageDetailUiState
    data class Error(val message: String) : OrgMessageDetailUiState
}

@Composable
fun OrgMessageDetailScreen(
    uiState: OrgMessageDetailUiState,
    onMarkRead: () -> Unit,
    onSendReply: (String, String) -> Unit,
    onBack: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        OrgMessageDetailUiState.Loading -> LoadingState(modifier.padding(contentPadding))
        is OrgMessageDetailUiState.Error -> EmptyState(
            "خطا",
            uiState.message,
            modifier.padding(contentPadding),
        )
        is OrgMessageDetailUiState.Success -> {
            val message = uiState.message
            var showReplyHint by rememberSaveable { mutableStateOf(false) }
            var showReplyForm by rememberSaveable { mutableStateOf(false) }
            var replyTitle by rememberSaveable { mutableStateOf("") }
            var replyBody by rememberSaveable { mutableStateOf("") }
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(contentPadding)
                    .padding(FarhangiSpacing.md),
                verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
            ) {
                Text(message.title, style = MaterialTheme.typography.headlineSmall)
                Text(
                    "${message.fromName} · ${message.fromRole.persianLabel()}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    "گیرنده: ${message.recipient.persianLabel()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(message.body, style = MaterialTheme.typography.bodyLarge)
                if (!message.imageUrl.isNullOrBlank()) {
                    ContentImage(
                        coverUrl = message.imageUrl,
                        contentDescription = "پیوست پیام ${message.title}",
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                uiState.status?.let {
                    Text(it, color = MaterialTheme.colorScheme.primary)
                }
                if (showReplyForm) {
                    OutlinedTextField(
                        value = replyTitle,
                        onValueChange = { replyTitle = it },
                        label = { Text("عنوان پاسخ") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = replyBody,
                        onValueChange = { replyBody = it },
                        label = { Text("متن پاسخ") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Button(
                        onClick = {
                            onSendReply(replyTitle, replyBody)
                            replyTitle = ""
                            replyBody = ""
                            showReplyForm = false
                        },
                        enabled = replyTitle.isNotBlank() && replyBody.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = FarhangiSize.touchTargetMin),
                    ) {
                        Text("ارسال پاسخ")
                    }
                }
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
                        onClick = { showReplyHint = true },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = FarhangiSize.touchTargetMin),
                    ) {
                        Text("پاسخ")
                    }
                }
                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = FarhangiSize.touchTargetMin),
                ) {
                    Text("بازگشت")
                }
            }
            if (showReplyHint) {
                AlertDialog(
                    onDismissRequest = { showReplyHint = false },
                    text = {
                        Text("پاسخ خود را در قالب یک پیام از همین صفحه ارسال فرمایید")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showReplyHint = false
                                showReplyForm = true
                                if (replyTitle.isBlank()) {
                                    replyTitle = "پاسخ: ${message.title}"
                                }
                            },
                        ) { Text("متوجه شدم") }
                    },
                )
            }
        }
    }
}
