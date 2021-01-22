package com.lge.kotlinstudyapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.session.MediaSession
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.session.MediaButtonReceiver
import com.lge.kotlinstudyapp.R
import com.lge.kotlinstudyapp.activity.MusicActivity

object KTBwMusicNotificationBuilder {
    private const val CHANNEL_NAME = "KSAMusicService"
    private const val CHANNEL_ID = "KTMUSIC_CHANNEL"

    fun createChannel(notiManager: NotificationManager) {
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
        notiManager.createNotificationChannel(channel)
    }

    fun getNotiBuilder(context: Context, title: String, artist: String, sessionToken: MediaSession.Token, isPlaying: Boolean) : Notification.Builder {
        return Notification.Builder(context, CHANNEL_ID).apply {
            setContentTitle(title)
            setContentText(artist)
            setColor(0)
            //setLargeIcon(description.iconBitmap) todo use
            setContentIntent(PendingIntent.getActivity(context, 0, Intent(context, MusicActivity::class.java), 0))
            // Stop the service when the notification is swiped away
            setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    context, PlaybackStateCompat.ACTION_STOP
                )
            )
            setVisibility(Notification.VISIBILITY_PUBLIC)
            setSmallIcon(R.mipmap.ic_launcher)

            // Add button
            addAction(
                Notification.Action(
                    android.R.drawable.ic_media_previous, "prev",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS))) //0
            if (!isPlaying) {
                addAction(
                    Notification.Action(
                        android.R.drawable.ic_media_play, "play",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            context, PlaybackStateCompat.ACTION_PLAY))) //1
            } else {
                addAction(
                    Notification.Action(
                        android.R.drawable.ic_media_pause, "pause",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            context, PlaybackStateCompat.ACTION_PAUSE))) //1
            }
            addAction(
                Notification.Action(
                    android.R.drawable.ic_media_next, "next",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context, PlaybackStateCompat.ACTION_SKIP_TO_NEXT))) //2

            addAction(
                Notification.Action(
                    android.R.drawable.ic_menu_more, "exit",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context, PlaybackStateCompat.ACTION_STOP))) //3
            // Take advantage of MediaStyle features
            style = Notification.MediaStyle()
                //.setMediaSession(sessionToken)
                .setShowActionsInCompactView(0, 1, 2)
        }
    }
}