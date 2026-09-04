package ir.farhangi.feature.studio.impl

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.ui.BookCover

@Composable
fun StudioCoverPicker(
    coverUrl: String?,
    title: String,
    onCoverPicked: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> uri?.let(onCoverPicked) },
    )
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
    ) {
        BookCover(
            coverUrl = coverUrl,
            title = title.ifBlank { "جلد" },
            modifier = Modifier.width(FarhangiSize.coverWidth),
        )
        OutlinedButton(
            onClick = {
                picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = FarhangiSize.touchTargetMin),
        ) {
            Text("انتخاب تصویر")
        }
    }
}
