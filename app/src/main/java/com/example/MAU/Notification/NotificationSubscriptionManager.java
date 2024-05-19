package com.example.MAU.Notification;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class NotificationSubscriptionManager {

    // This class manages subscription to topics for Firebase Cloud Messaging

    public static void subscribeToNotifications(Context context) {
        FirebaseMessaging.getInstance().subscribeToTopic("Meditation")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Вы успешно подписались на уведомления", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Ошибка подписки на уведомления", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    public static void unsubscribeFromNotifications(Context context) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("Meditation")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Вы успешно отписались от уведомлений ", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(context, "Ошибка отписки от уведомлений", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
