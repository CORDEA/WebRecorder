package jp.cordea.webrecorder

import android.content.Context
import android.util.Size
import android.view.WindowManager
import androidx.core.content.getSystemService
import dagger.Reusable
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@Reusable
class WindowSizeProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun provide(): Size {
        val metrics = context.getSystemService<WindowManager>()?.currentWindowMetrics
            ?: throw IllegalStateException()
        return Size(
            metrics.bounds.width(),
            metrics.bounds.height()
        )
    }
}
