package jp.cordea.webrecorder

import android.content.Context
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.util.Size
import java.io.FileDescriptor

class ScreenRecorder(private val context: Context) {
    companion object {
        private const val DISPLAY_NAME = "display"
    }

    private var virtualDisplay: VirtualDisplay? = null
    private var recorder: MediaRecorder? = null

    var recording = false
        private set

    fun start(
        projection: MediaProjection,
        output: FileDescriptor,
        size: Size
    ) {
        if (recording) {
            return
        }
        recording = true
        val recorder = MediaRecorder(context).apply {
            setAudioSource(MediaRecorder.AudioSource.DEFAULT)
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
            setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT)
            setVideoSize(size.width, size.height)
            setOutputFile(output)
        }
        recorder.prepare()
        virtualDisplay = projection.createVirtualDisplay(
            DISPLAY_NAME,
            size.width,
            size.height,
            context.resources.configuration.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            recorder.surface,
            null,
            null
        )
        recorder.start()
        this.recorder = recorder
    }

    fun stop() {
        recorder?.stop()
        recording = false
    }

    fun release() {
        recorder?.release()
        virtualDisplay?.release()
    }
}
