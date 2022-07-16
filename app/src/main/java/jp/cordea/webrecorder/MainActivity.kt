package jp.cordea.webrecorder

import android.content.ContentValues
import android.content.Intent
import android.media.projection.MediaProjection
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Size
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.lifecycle.lifecycleScope
import jp.cordea.webrecorder.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        private const val OUTPUT_FILENAME = "video.mp4"
    }

    private val mediaProjectionRequest = MediaProjectionRequest(this)
    private val screenRecorder = ScreenRecorder(this)
    private var mediaProjection: MediaProjection? = null
    private var fileDescriptor: ParcelFileDescriptor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        startForegroundService(Intent(this, Recorder::class.java))
        binding.fab.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (screenRecorder.recording) {
            release()
            return
        }
        val window = getSystemService<WindowManager>() ?: return
        val metrics = window.currentWindowMetrics
        val file = prepareFile() ?: return
        fileDescriptor = file
        flow { emit(mediaProjectionRequest.request()) }
            .onEach {
                mediaProjection = it
                screenRecorder.start(
                    it,
                    file.fileDescriptor,
                    Size(
                        metrics.bounds.width(),
                        metrics.bounds.height()
                    )
                )
            }
            .launchIn(lifecycleScope)
    }

    private fun prepareFile(): ParcelFileDescriptor? {
        val uri = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val details = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, OUTPUT_FILENAME)
        }
        val fileUri = contentResolver.insert(uri, details) ?: return null
        return contentResolver.openFileDescriptor(fileUri, "w")
    }

    private fun release() {
        screenRecorder.stop()
        mediaProjection?.stop()
        fileDescriptor?.close()
        screenRecorder.release()
    }
}
