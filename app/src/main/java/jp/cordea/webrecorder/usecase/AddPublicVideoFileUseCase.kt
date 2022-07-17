package jp.cordea.webrecorder.usecase

import android.content.ContentValues
import android.content.Context
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import dagger.Reusable
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@Reusable
class AddPublicVideoFileUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun execute(name: String): ParcelFileDescriptor {
        val uri = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val details = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, name)
        }
        val fileUri = context.contentResolver.insert(uri, details) ?: throw IllegalStateException()
        return context.contentResolver.openFileDescriptor(fileUri, "w")
            ?: throw IllegalStateException()
    }
}
