package jp.cordea.webrecorder

import android.app.*
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Binder
import android.os.IBinder
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import dagger.hilt.android.AndroidEntryPoint
import jp.cordea.webrecorder.usecase.AddPublicVideoFileUseCase
import javax.inject.Inject

class RecorderBinder : Binder()

@AndroidEntryPoint
class Recorder : Service() {
    companion object {
        private const val OUTPUT_FILENAME = "video.mp4"
        private const val NOTIFICATION_ID = 1
        const val INTENT_KEY = "intent_key"

        const val ACTION_INIT = "init"
        const val ACTION_START = "start"
        const val ACTION_STOP = "stop"
    }

    @Inject
    lateinit var screenRecorder: ScreenRecorder

    @Inject
    lateinit var windowSizeProvider: WindowSizeProvider

    @Inject
    lateinit var addPublicVideoFileUseCase: AddPublicVideoFileUseCase

    private val channelId get() = "$packageName.notification"

    private var mediaProjection: MediaProjection? = null
    private var file: ParcelFileDescriptor? = null

    override fun onBind(intent: Intent?): IBinder = RecorderBinder()

    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(
            channelId,
            "${packageName}.Recorder",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        getSystemService<NotificationManager>()?.createNotificationChannel(channel)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId)
        }
        when (intent.action) {
            ACTION_INIT -> {
                startForeground(
                    NOTIFICATION_ID,
                    NotificationCompat
                        .Builder(this, channelId)
                        .build()
                )
            }
            ACTION_START -> {
                val projection = intent.getParcelableExtra<Intent>(INTENT_KEY)?.let {
                    getSystemService<MediaProjectionManager>()?.getMediaProjection(
                        Activity.RESULT_OK,
                        it
                    )
                } ?: return super.onStartCommand(intent, flags, startId)
                val file = addPublicVideoFileUseCase.execute(OUTPUT_FILENAME)
                screenRecorder.start(
                    projection,
                    file.fileDescriptor,
                    windowSizeProvider.provide()
                )
                val actionIntent = PendingIntent.getForegroundService(
                    this,
                    0,
                    Intent(this, Recorder::class.java).apply {
                        action = ACTION_STOP
                    },
                    PendingIntent.FLAG_IMMUTABLE
                )
                getSystemService<NotificationManager>()?.notify(
                    NOTIFICATION_ID,
                    NotificationCompat
                        .Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.recorder_content))
                        .addAction(
                            NotificationCompat.Action.Builder(
                                R.mipmap.ic_launcher,
                                getString(R.string.recorder_stop_action),
                                actionIntent
                            ).build()
                        )
                        .build()
                )
            }
            ACTION_STOP -> {
                screenRecorder.stop()
                mediaProjection?.stop()
                file?.close()
                screenRecorder.release()
                stopForeground(true)
            }
        }
        return START_STICKY
    }
}
