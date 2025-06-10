package com.example.androidplanowaniewycieczek.ui.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.example.androidplanowaniewycieczek.R;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String channelId = "REMINDER_CHANNEL";
        String channelName = "Reminder Notifications";

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Tworzenie kanału powiadomień (dla Android 8+)
        NotificationChannel channel = new NotificationChannel(channelId, channelName,
                NotificationManager.IMPORTANCE_HIGH);
        manager.createNotificationChannel(channel);

        // Tworzenie powiadomienia
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle("Przypomnienie")
                .setContentText("Przypominamy o wycieczce!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        manager.notify(1001, builder.build());
    }
}
