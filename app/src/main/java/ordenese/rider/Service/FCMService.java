package ordenese.rider.Service;/*
package ordenese.rider.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import ordenese.rider.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMService extends FirebaseMessagingService {


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Log.e("Token ","Token "+token);

    }

    @Override
    public void onMessageSent(@NonNull String msgId) {
        super.onMessageSent(msgId);

        Log.e("Test","onMessageSent");

    }

    @Override
    public void handleIntent(Intent intent) {
        //super.handleIntent(intent);

        Log.e("eOrder","onMessage Receive in handle Intent");

//        Bundle bundle = intent.getExtras();
//        if (bundle != null) {
//            for (String key : bundle.keySet()) {
//                Object value = bundle.get(key);
//                Log.e("FCM", "Key: " + key + " Value: " + value);
//            }
//        }


//        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//
//        int notificationId = 1;
//        String channelId = "channel-01";
//        String channelName = "Channel Name";
//        int importance = NotificationManager.IMPORTANCE_HIGH;
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            NotificationChannel mChannel = new NotificationChannel(
//                    channelId, channelName, importance);
//            notificationManager.createNotificationChannel(mChannel);
//        }
//
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
//                .setSmallIcon(R.drawable.splashscreen)
//                //.setSound(customSoundUri)
//                .setContentTitle("Test Title")
//                .setContentText("Test Message");
//
//
//        Intent intent1 = new Intent(getApplicationContext(), TestActivity.class);
//
//
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
//        stackBuilder.addNextIntent(intent1);
//        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
//                0,
//                PendingIntent.FLAG_UPDATE_CURRENT
//        );
//        mBuilder.setContentIntent(resultPendingIntent);
//
//        notificationManager.notify(notificationId, mBuilder.build());



    }

    @Override
    public boolean handleIntentOnMainThread(Intent intent) {

        Log.e("eOrder","onMessage Receive in handleIntentOnMainThread");

        return super.handleIntentOnMainThread(intent);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        try
        {
            Uri customSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_new);

//            Notification notification = new NotificationCompat.Builder(this)
//                    .setContentTitle(remoteMessage.getNotification().getTitle())
//                    .setContentText(remoteMessage.getNotification().getBody())
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setSound(customSoundUri)
//                    .setAutoCancel(true)
//                    .setPriority(Notification.PRIORITY_MAX)
//                    .build();
//            NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
//            manager.notify(123, notification);



            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

            int notificationId = 1;
            String channelId = "channel-01";
            String channelName = "Channel Name";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(
                        channelId, channelName, importance);
                notificationManager.createNotificationChannel(mChannel);
            }

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setSound(customSoundUri)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody());

            Intent intent = new Intent(getApplicationContext(), TestActivity.class);


            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
            stackBuilder.addNextIntent(intent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                    0,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            mBuilder.setContentIntent(resultPendingIntent);

            notificationManager.notify(notificationId, mBuilder.build());








//            NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(this)
//                    .setSmallIcon(R.mipmap.ic_launcher) // notification icon
//                    .setContentTitle(remoteMessage.getNotification().getTitle()) // title for notification
//                    .setContentText(remoteMessage.getNotification().getBody()) // message for notification
//                    //.setAutoCancel(true) // clear notification after click
//                    .setSound(customSoundUri);
//            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            mNotificationManager.notify(0, mBuilder.build());


            Log.e("eOrder.ae","Title "+remoteMessage.getNotification().getTitle()+"Text "+remoteMessage.getNotification().getBody());
        }
        catch (Exception e)
        {
            Log.e("Exception",e.getMessage());
        }

//        Log.e("Notification : ",remoteMessage.getFrom());
//
//        if(remoteMessage.getNotification()!=null)
//        {
//            String title=remoteMessage.getNotification().getTitle();
//            String body=remoteMessage.getNotification().getBody();
//
//            Log.e("Title and Body",title+" "+body);
//            Toast.makeText(getApplicationContext(), ""+title+" "+body, Toast.LENGTH_SHORT).show();
//        }
//
//        Map<String, String> params = remoteMessage.getData();
//        JSONObject object = new JSONObject(params);
//        Log.e("JSON_OBJECT", object.toString());
//
//        String NOTIFICATION_CHANNEL_ID = "Nilesh_channel";
//
//        long pattern[] = {0, 1000, 500, 1000};
//
//        NotificationManager mNotificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Dream",
//                    NotificationManager.IMPORTANCE_HIGH);
//
//            notificationChannel.setDescription("");
//            notificationChannel.enableLights(true);
//            notificationChannel.setLightColor(Color.RED);
//            notificationChannel.setVibrationPattern(pattern);
//            notificationChannel.enableVibration(true);
//
//            mNotificationManager.createNotificationChannel(notificationChannel);
//        }
//
//        // to diaplay notification in DND Mode
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = mNotificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID);
//            channel.canBypassDnd();
//        }
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
//
//        notificationBuilder.setAutoCancel(true)
//                .setColor(ContextCompat.getColor(this, R.color.cardview_shadow_end_color))
//                .setContentTitle(getString(R.string.app_name))
//                .setContentText(remoteMessage.getNotification().getBody())
//                .setDefaults(Notification.DEFAULT_ALL)
//                .setWhen(System.currentTimeMillis())
//                //.setSmallIcon(R.drawable.ic_action_name)
//                .setAutoCancel(true);
//
//
//        mNotificationManager.notify(1000, notificationBuilder.build());
    }

}
*/
