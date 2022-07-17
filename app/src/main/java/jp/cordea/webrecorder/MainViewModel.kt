package jp.cordea.webrecorder

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val _event = MutableSharedFlow<MainEvent>()
    val event = _event.asSharedFlow()

    val rawUrl = MutableLiveData("")
    val url = MutableLiveData("")
    val isControlVisible = MutableLiveData(true)
    val isWebViewVisible = MutableLiveData(false)

    fun onSubmitClicked() {
        url.value = rawUrl.value
        isControlVisible.value = false
        isWebViewVisible.value = true
        viewModelScope.launch { _event.emit(MainEvent.StartRecording) }
    }
}

sealed class MainEvent {
    object StartRecording : MainEvent()
}
