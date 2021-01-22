package com.lge.kotlinstudyapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.session.MediaSession
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.session.MediaButtonReceiver
import com.lge.kotlinstudyapp.R

object KTBwMusicNotificationBuilder {
    private const val CHANNEL_NAME = "KSAMusicService"
    private const val CHANNEL_ID = "KTMUSIC_CHANNEL"

    fun getNotiBuilder(context: Context, mediaSession: MediaSession) : Notification.Builder {
        val controller = mediaSession.controller
        //val description = mediaMetadata.description
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        return Notification.Builder(context, CHANNEL_ID).apply {
            // Add the metadata for the currently playing track
            setContentTitle("test Title")
            setContentText("test SubTitle")
            setSubText("test Description")
            //setLargeIcon(description.iconBitmap)
            // Enable launching the player by clicking the notification
            setContentIntent(controller.sessionActivity)
            // Stop the service when the notification is swiped away
            setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    context, PlaybackStateCompat.ACTION_STOP)
            )

            setVisibility(Notification.VISIBILITY_PUBLIC)
            setSmallIcon(R.mipmap.ic_launcher)


            // Add button
            addAction(
                Notification.Action(
                    android.R.drawable.ic_media_previous,"prev",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)))
            addAction(
                Notification.Action(
                    android.R.drawable.ic_media_play,"play",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context, PlaybackStateCompat.ACTION_PLAY)))
            addAction(
                Notification.Action(
                    android.R.drawable.ic_media_pause, "pause",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context, PlaybackStateCompat.ACTION_PAUSE)))
            addAction(
                Notification.Action(
                    android.R.drawable.ic_media_next, "next",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)))
            addAction(
                Notification.Action(
                    android.R.drawable.ic_menu_more, "exit",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context, PlaybackStateCompat.ACTION_STOP)))

            // Take advantage of MediaStyle features
            style = Notification.MediaStyle()
                .setMediaSession(mediaSession.sessionToken)
                .setShowActionsInCompactView(1, 2)
        }
    }
}