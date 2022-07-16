package jp.cordea.webrecorder

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val url = MutableLiveData("")

    fun onSubmitClicked() {
    }
}
