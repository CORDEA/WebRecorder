package jp.cordea.webrecorder

import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class MediaProjectionRequest(private val activity: AppCompatActivity) {
    private lateinit var manager: MediaProjectionManager
    private lateinit var continuation: CancellableContinuation<MediaProjection>

    private val launcher =
        activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val data = result.data
            if (data == null) {
                continuation.cancel()
                return@registerForActivityResult
            }
            manager.getMediaProjection(result.resultCode, data)?.let {
                continuation.resume(it)
            } ?: continuation.cancel()
        }

    suspend fun request() = suspendCancellableCoroutine {
        continuation = it
        val manager = activity.getSystemService<MediaProjectionManager>()
        if (manager == null) {
            continuation.cancel()
            return@suspendCancellableCoroutine
        }
        this.manager = manager
        launcher.launch(manager.createScreenCaptureIntent())
    }
}
