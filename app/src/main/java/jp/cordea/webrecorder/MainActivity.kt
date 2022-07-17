package jp.cordea.webrecorder

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import jp.cordea.webrecorder.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transform

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mediaProjectionRequest = MediaProjectionRequest(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewModel by viewModels<MainViewModel>()
        binding.viewModel = viewModel
        startForegroundService(Intent(this, Recorder::class.java))
        viewModel.event
            .transform {
                when (it) {
                    MainEvent.RequestMediaProjection ->
                        emit(mediaProjectionRequest.request())
                }
            }
            .onEach {
                viewModel.onMediaProjectionObtained(it)
            }
            .launchIn(lifecycleScope)
    }
}
