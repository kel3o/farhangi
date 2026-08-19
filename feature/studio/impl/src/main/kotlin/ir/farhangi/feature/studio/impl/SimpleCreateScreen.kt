package ir.farhangi.feature.studio.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing

@Composable
fun SimpleCreateScreen(
    title: String,
    subtitle: String,
    onSubmit: (String, String, String) -> Unit,
    onBack: () -> Unit,
    statusMessage: String?,
    modifier: Modifier = Modifier,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var authorOrCategory by rememberSaveable { mutableStateOf("") }
    var body by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(FarhangiSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.md),
    ) {
        Text(title, style = MaterialTheme.typography.headlineSmall)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(value = name, onValueChange = { name = it }, modifier = Modifier.fillMaxWidth(), label = { Text("عنوان") })
        OutlinedTextField(value = authorOrCategory, onValueChange = { authorOrCategory = it }, modifier = Modifier.fillMaxWidth(), label = { Text("نویسنده / دسته") })
        OutlinedTextField(value = body, onValueChange = { body = it }, modifier = Modifier.fillMaxWidth(), label = { Text("متن") }, minLines = 4)
        if (!statusMessage.isNullOrBlank()) {
            Text(statusMessage, color = MaterialTheme.colorScheme.primary)
        }
        Button(
            onClick = { onSubmit(name, authorOrCategory, body) },
            enabled = name.isNotBlank(),
            modifier = Modifier.fillMaxWidth().heightIn(min = FarhangiSize.touchTargetMin),
        ) { Text("انتشار") }
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().heightIn(min = FarhangiSize.touchTargetMin),
        ) { Text("بازگشت") }
    }
}
