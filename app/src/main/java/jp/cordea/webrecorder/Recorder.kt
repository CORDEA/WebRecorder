package jp.cordea.webrecorder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.content.getSystemService

class RecorderBinder : Binder()

class Recorder : Service() {
    private val channelId get() = "$packageName.notification"

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
        startForeground(
            startId,
            Notification
                .Builder(this, channelId)
                .build()
        )
        return START_STICKY
    }
}
