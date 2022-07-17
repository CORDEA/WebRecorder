package jp.cordea.webrecorder

import android.media.projection.MediaProjection
import android.os.ParcelFileDescriptor
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.cordea.webrecorder.usecase.AddPublicVideoFileUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val screenRecorder: ScreenRecorder,
    private val windowSizeProvider: WindowSizeProvider,
    private val addPublicVideoFileUseCase: AddPublicVideoFileUseCase
) : ViewModel() {
    companion object {
        private const val OUTPUT_FILENAME = "video.mp4"
    }

    private val _event = MutableSharedFlow<MainEvent>()
    val event = _event.asSharedFlow()

    val url = MutableLiveData("")

    private var file: ParcelFileDescriptor? = null
    private var mediaProjection: MediaProjection? = null

    fun onSubmitClicked() {
        if (screenRecorder.recording) {
            screenRecorder.stop()
            mediaProjection?.stop()
            file?.close()
            mediaProjection = null
            file = null
            screenRecorder.release()
            return
        }
        viewModelScope.launch { _event.emit(MainEvent.RequestMediaProjection) }
    }

    fun onMediaProjectionObtained(mediaProjection: MediaProjection) {
        val size = windowSizeProvider.provide()
        val file = addPublicVideoFileUseCase.execute(OUTPUT_FILENAME)
        this.mediaProjection = mediaProjection
        this.file = file
        screenRecorder.start(
            mediaProjection,
            file.fileDescriptor,
            size
        )
    }

    override fun onCleared() {
        mediaProjection?.stop()
        file?.close()
        screenRecorder.release()
        super.onCleared()
    }
}

sealed class MainEvent {
    object RequestMediaProjection : MainEvent()
}
