package com.animal.scale.hodoo.service.firebase;

/**
 * Created by Joo on 2017. 12. 19.
 */

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.animal.scale.hodoo.R;
import com.animal.scale.hodoo.activity.home.activity.HomeActivity;
import com.animal.scale.hodoo.activity.user.login.LoginActivity;
import com.animal.scale.hodoo.fcm.NotificationActivity;
import com.animal.scale.hodoo.common.SharedPrefManager;
import com.animal.scale.hodoo.common.SharedPrefVariable;
import com.animal.scale.hodoo.constant.HodooConstant;
import com.animal.scale.hodoo.fcm.PushWakeLock;
import com.animal.scale.hodoo.service.AlwaysOnTopService;
import com.animal.scale.hodoo.util.BadgeUtils;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService implements FirebaseIn.View {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    int count = 0;
    private FirebaseIn.Presenter presenter;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
        presenter = new FirebasePresenter(this);
        presenter.initDate(getApplicationContext());
    }

    // 메시지 수신
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i("HJLEE", "onMessageReceived");

        Map<String, String> data = remoteMessage.getData();
        presenter.getData(data);

//        sendNotification(getApplicationContext(), data, type);
    }

    public void sendNotification( Map<String, String> data) {

        /* variable (s) */

        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
        KeyguardManager km = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);

        Intent intent = null;
        int type = NotificationCompat.PRIORITY_MAX, notiType = 0, badgeCount = sharedPrefManager.getIntExtra(SharedPrefVariable.BADGE_COUNT), pushIdx = 0, badgeType = 0;

        String notiTypeStr = data.get("notiType"), title = data.get("title"), message = data.get("content"), host = data.get("host"), channelId = "";
        /* variable (e) */

        if ( notiTypeStr != null || !notiTypeStr.equals("") )
            notiType = Integer.parseInt( notiTypeStr );

        switch (notiType) {
            case HodooConstant.FIREBASE_NORMAL_TYPE :
            case HodooConstant.FIREBASE_WEIGHT_TYPE :
            case HodooConstant.FIREBASE_FEED_TYPE :
                Random random = new Random();
                pushIdx = random.nextInt();
                intent = new Intent(this, HomeActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("message", message);
                intent.putExtra(HodooConstant.NOTI_TYPE_KEY, notiType);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                badgeType = notiType;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if ( notiType == HodooConstant.FIREBASE_NORMAL_TYPE ) {
                        channelId = HodooConstant.NORMAL_CHANNEL;
                    } else if (notiType == HodooConstant.FIREBASE_WEIGHT_TYPE) {
                        channelId = HodooConstant.WEIGHT_CHECK_CHANNEL;
                    } else if (notiType == HodooConstant.FIREBASE_FEED_TYPE) {
                        channelId = HodooConstant.FEED_CHECK_CHANNEL;
                    }
                }

                break;
            case HodooConstant.FIREBASE_INVITATION_TYPE :
                Gson gson = new Gson();
                int idx = Integer.parseInt(data.get("toUserIdx"));
                int fromUserIdx = Integer.parseInt(data.get("fromUserIdx"));
                String url = "selphone://" + host;

                pushIdx = idx;

                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.putExtra("title", title);
                intent.putExtra("message", message);
                intent.putExtra("host", host);
                intent.putExtra("data", gson.toJson(data));

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                message += getApplicationContext().getString(R.string.invitation_subfix);
                badgeType = HodooConstant.FIREBASE_INVITATION_TYPE;
                channelId = HodooConstant.INVITATION_GROUP_CHANNEL;
                presenter.setInvitationUser(idx, fromUserIdx);
                break;
            case HodooConstant.FIREBASE_INVITATION_ACCEPT :
                intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                badgeType = HodooConstant.FIREBASE_INVITATION_ACCEPT;
                channelId = HodooConstant.INVITATION_GROUP_CHANNEL;
                break;
        }

        /* device wake (s) */
        if(km.inKeyguardRestrictedInputMode()){
            PushWakeLock.acquireCpuWakeLock(getApplicationContext());
            PushWakeLock.releaseCpuLock();
        }
        /* device wake (e) */

        /* application is background (s) */
        if ( isAppIsInBackground(getApplicationContext()) && !km.inKeyguardRestrictedInputMode() &&  Settings.canDrawOverlays(getApplicationContext()) ) {
            Intent bacckgroundIntent = new Intent(getApplicationContext(), AlwaysOnTopService.class);
            bacckgroundIntent.putExtra("title", title);
            bacckgroundIntent.putExtra("message", message);
            if ( host != null && !host.equals("") )
                bacckgroundIntent.putExtra("host", host);

            Gson gson = new Gson();
            bacckgroundIntent.putExtra("data", gson.toJson(data));
            if ( !isServiceRunning() ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                } else {
                    getApplicationContext().startService(bacckgroundIntent);
                }
            } else {
                getApplicationContext().stopService(bacckgroundIntent);
                getApplicationContext().startService(bacckgroundIntent);
            }
            type = NotificationCompat.PRIORITY_LOW;
        }
        /* application is background (e) */

        /* start push (s) */
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), pushIdx /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.transparent_logo))
                .setSmallIcon(R.drawable.noti__status_icon)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.mainRed))
                .setColorized(true)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(type)
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_ALL)
                .setContentIntent(pendingIntent);

        //&& !isAppIsInBackground(getApplicationContext())
        if ( notiType == HodooConstant.FIREBASE_INVITATION_TYPE  ) {
            PendingIntent dismissIntent = NotificationActivity.getDismissIntent(pushIdx, getApplicationContext());
            notificationBuilder.addAction(R.drawable.ic_close_gray_24dp, getApplicationContext().getString(android.R.string.cancel), dismissIntent)
                               .addAction(R.drawable.ic_send_gray_24dp, getApplicationContext().getString(android.R.string.ok), pendingIntent);
        }

        if ( message.length() > 20 )
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));


        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setShowBadge(true);
            notificationChannel.setDescription("HodooNotification");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(notificationChannel);


            notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.transparent_logo))
                    .setSmallIcon(R.drawable.noti__status_icon)
                    .setColor(ContextCompat.getColor(getApplicationContext(), R.color.mainRed))
                    .setColorized(true)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setPriority(type)
                    .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_ALL)
                    .setContentIntent(pendingIntent)
                    .setNumber(badgeCount)
                    .setChannelId(channelId);
        }

        notificationManager.notify(pushIdx /* ID of notification */, notificationBuilder.build());

        presenter.countingBadge( badgeType, badgeCount );
        /* start push (e) */
    }

    @Override
    public void setBadge(int count) {
        BadgeUtils.setBadge(getApplicationContext(), count);
        presenter.saveBadgeCount(count);
    }

    @Override
    public void sendBroad() {
        Intent broadIntent = new Intent(HodooConstant.FCM_RECEIVER_NAME);
        getApplicationContext().sendBroadcast(broadIntent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        unbindService(connection);
    }
    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }
    private String getActiveActivity(Context context) {
        ActivityManager activityManager = (ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> info;
        info = activityManager.getRunningTasks(1);
        ActivityManager.RunningTaskInfo runningTaskInfo = info.get(0);
        return runningTaskInfo.topActivity.getClassName();
    }

    public boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (AlwaysOnTopService.class.getName().equals(service.service.getClassName()))
                return true;
        }
        return false;
    }

    public static Object fromJson(String jsonString, Type type) {
        return new Gson().fromJson(jsonString, type);
    }
}

