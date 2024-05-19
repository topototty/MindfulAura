package com.example.MAU.Notification;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class NotificationSubscriptionManager {

    // This class manages subscription to topics for Firebase Cloud Messaging

    public static void subscribeToNotifications() {
        FirebaseMessaging.getInstance().subscribeToTopic("Meditation")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {

                        } else {

                        }
                    }
                });
    }

    public static void unsubscribeFromNotifications() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("Meditation")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Unsubscription successful
                        } else {
                            // Unsubscription failed
                        }
                    }
                });
    }

}
