package ir.farhangi.feature.studio.impl

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudioCoverStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun persist(uri: Uri): String {
        val directory = File(context.filesDir, COVER_DIRECTORY)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val destination = File(directory, "${UUID.randomUUID()}$COVER_EXTENSION")
        context.contentResolver.openInputStream(uri)?.use { input ->
            destination.outputStream().use { output -> input.copyTo(output) }
        }
        return destination.toURI().toString()
    }

    companion object {
        private const val COVER_DIRECTORY = "studio-covers"
        private const val COVER_EXTENSION = ".jpg"
    }
}
