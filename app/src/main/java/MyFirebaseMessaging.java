import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sameer.smart_home.R;

public class MyFirebaseMessaging extends FirebaseMessagingService {

  @RequiresApi(api = Build.VERSION_CODES.O)
  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
   // TODO(developer): Handle FCM messages here.
   // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
   System.out.println("From: " + remoteMessage.getFrom());

    // Check if message contains a notification payload.
    if (remoteMessage.getNotification() != null) {
     System.out.println("Message Notification Body: " +
             remoteMessage.getNotification().getBody());
    }
   //sendNotification(remoteMessage.getNotification().getBody());
   Intent intent = new Intent(this, Main.class);
   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
   PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

   NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID")
           .setSmallIcon(R.drawable.backbutton)
           .setContentTitle("My notification")
           .setContentText("Hello World!")
           .setPriority(NotificationCompat.PRIORITY_DEFAULT)
           // Set the intent that will fire when the user taps the notification
          // .setContentIntent(pendingIntent)
           .setAutoCancel(true);

   NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
   notificationManager.notify(0, builder.build());
  }

 private void sendNotification(String from, String body) {
   new Handler(Looper.getMainLooper()).post(new Runnable() {
    @Override
    public void run() {
     Toast.makeText(MyFirebaseMessaging.this,
             "from -> "+ from +"\nbody is "+body,Toast.LENGTH_LONG).show();
    }
   });
 }

 @RequiresApi(api = Build.VERSION_CODES.O)
 private void sendNotification(String message)
 {
  Intent intent = new Intent(this, Main.class);
  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
  PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
  String NOTIFICATION_CHANNEL_ID = "example.sameer";
  String channelName = "Background Service";
  Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

  NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
          .setSmallIcon(R.drawable.bulb)
          .setContentTitle("Smartek")
          .setContentText(message)
          .setAutoCancel(true)
          .setSound(defaultSoundUri)
          .setPriority(NotificationManager.IMPORTANCE_MIN)
          .setCategory(Notification.CATEGORY_SERVICE)
          .setContentIntent(pendingIntent);

  NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

  NotificationChannel notificationChannel = new NotificationChannel(channelName,
          "I loved it",
          NotificationManager.IMPORTANCE_DEFAULT);
  notificationManager.createNotificationChannel(notificationChannel);
  notificationManager.notify(0,notificationBuilder.build());
 }

}