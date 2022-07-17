package jp.cordea.webrecorder

import android.content.Intent
import android.media.projection.MediaProjectionManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class MediaProjectionRequest(private val activity: AppCompatActivity) {
    private lateinit var manager: MediaProjectionManager
    private lateinit var continuation: CancellableContinuation<Intent>

    private val launcher =
        activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val data = result.data
            if (data == null) {
                continuation.cancel()
                return@registerForActivityResult
            }
            continuation.resume(data)
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
