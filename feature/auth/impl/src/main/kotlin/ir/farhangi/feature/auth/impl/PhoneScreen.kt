package ir.farhangi.feature.auth.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing

@Composable
fun PhoneScreen(
    uiState: AuthUiState,
    onSendOtp: (String) -> Unit,
    initialPhone: String = "",
    modifier: Modifier = Modifier,
) {
    var phone by rememberSaveable(initialPhone) { mutableStateOf(initialPhone) }
    val isLoading = uiState is AuthUiState.Loading

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .padding(FarhangiSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.md, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "ورود به فرهنگی",
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = "شماره موبایل خود را وارد کنید",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "کد تأیید آزمایشی ۱۲۳۴۵۶ است. برای نقش‌ها: ۰۹۱۱۱۱۱۱۱۱۱ ویرایشگر، ۰۹۲۲۲۲۲۲۲۲۲ سازمانی، ۰۹۳۳۳۳۳۳۳۳۳ مدیرکل.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it.filter { ch -> ch.isDigit() || ch == '+' }.take(MAX_PHONE_LENGTH) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("شماره موبایل") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            enabled = !isLoading,
        )
        if (uiState is AuthUiState.Error) {
            Text(
                text = uiState.message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Button(
            onClick = { onSendOtp(phone) },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = FarhangiSize.touchTargetMin)
                .semantics { contentDescription = "دریافت کد تأیید" },
            enabled = !isLoading && phone.filter(Char::isDigit).length >= MIN_PHONE_DIGITS,
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(FarhangiSize.iconDefault))
            } else {
                Text("دریافت کد تأیید")
            }
        }
    }
}

private const val MIN_PHONE_DIGITS = 10
private const val MAX_PHONE_LENGTH = 13