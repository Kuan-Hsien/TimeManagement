package com.kuanhsien.timemanagement.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.service.dreams.DreamService;

import com.kuanhsien.timemanagement.MainActivity;
import com.kuanhsien.timemanagement.R;
import com.kuanhsien.timemanagement.utils.Constants;
import com.kuanhsien.timemanagement.utils.Logger;


public class MainService extends Service {
    private static final String MSG = "MainService: ";

    private AlarmManager mAlarmManager;
    private NotificationManager mNotificationManager;
    private PendingIntent mPendingIntentBroadcast;


    public MainService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d(Constants.TAG, MSG + "onCreate : ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(Constants.TAG, MSG + "onStartCommand : ");

        // if APK ver > 26, start service at foreground (need notification with notification channel)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            // Step1. 初始化 NotificationManager，取得 Notification 服務。
            mNotificationManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);


            // Step1.5 create notification channel for version > Android 8.0(API level 26)
            String strChannelId = "voyage.channel";
            NotificationChannel channelLove = new NotificationChannel(
                    strChannelId, "Voyage Channel", NotificationManager.IMPORTANCE_HIGH);
            channelLove.setDescription("voyage notification");
            channelLove.enableLights(true);
            channelLove.enableVibration(true);

            mNotificationManager.createNotificationChannel(channelLove);




            // Step2. 設定當執行這個通知之後，所要執行的 activity。
            Intent intentNotify = new Intent(this, MainActivity.class);
//            intentNotify.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntentNotify
                    = PendingIntent.getActivity(this, 0, intentNotify, 0);



            //Step3. 透過 Notification.Builder 來建構 notification，
            //並直接使用其.build() 的方法將設定好屬性的 Builder 轉換
            //成 notification，最後開始將顯示通知訊息發送至狀態列上。
            Notification notification
                    = new Notification.Builder(this)
                    .setContentIntent(pendingIntentNotify) // 設置 PendingIntent
                    .setSmallIcon(R.mipmap.ic_launcher) // 設置狀態列裡面的圖示（小圖示）　　
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher)) // 下拉下拉清單裡面的圖示（大圖示）
                    .setTicker("notification on status bar.") // 設置狀態列的顯示的資訊
                    .setWhen(System.currentTimeMillis()) // 設置通知發生時間
                    .setAutoCancel(true) // 設置通知被使用者點擊後是否清除  //notification.flags = Notification.FLAG_AUTO_CANCEL;
                    .setContentTitle("Notification Title") // 設置下拉清單裡的標題
                    .setContentText("Notification Content") // 設置上下文內容
                    .setOngoing(false) // true 使 notification 變為 ongoing，用戶不能手動清除  // notification.flags = Notification.FLAG_ONGOING_EVENT; notification.flags = Notification.FLAG_NO_CLEAR;

//                    .setDefaults(Notification.DEFAULT_ALL) //使用所有默認值，比如聲音，震動，閃屏等等
                    // .setDefaults(Notification.DEFAULT_VIBRATE) //使用默認手機震動提示
                    // .setDefaults(Notification.DEFAULT_SOUND) //使用默認聲音提示
                    // .setDefaults(Notification.DEFAULT_LIGHTS) //使用默認閃光提示
                    // .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND) //使用默認閃光提示 與 默認聲音提示

                    // .setVibrate(vibrate) //自訂震動長度
                    // .setSound(uri) //自訂鈴聲
                    // .setLights(0xff00ff00, 300, 1000) //自訂燈光閃爍 (ledARGB, ledOnMS, ledOffMS)
                    .setChannelId(strChannelId)
                    .build();

            // 把指定ID的通知持久的發送到狀態條上
            mNotificationManager.notify(0, notification);

            Logger.d(Constants.TAG, MSG + "onStartCommand : before startForeground");
            startForeground(110, notification); // start service at foreground
            Logger.d(Constants.TAG, MSG + "onStartCommand : after startForeground");
        }


        // intent to start broadcast
//        ComponentName componentName = new ComponentName("com.kuanhsien.app.sampleservicebroadcastreceiver", "com.kuanhsien.app.sampleservicebroadcastreceiver.MyReceiver");
//
//        Intent broadcastIntent = new Intent()
//                .setComponent(componentName)
//                .putExtra("sender_name", "Start-VoyageBroadcast"); //設定廣播夾帶參數
//
//        mPendingIntentBroadcast = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);





        // Create alarmManager
        Logger.d(Constants.TAG, MSG + "onStartCommand : before alarmManager");

        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10 * 1000, mPendingIntentBroadcast);
        Logger.d(Constants.TAG, MSG + "onStartCommand : after alarmManager");

//        sendBroadcast(serviceIntent); //發送廣播訊息
//        stopSelf();

        return super.onStartCommand(intent, flags, startId);


    }

    @Override
    public IBinder onBind(Intent intent) {
        Logger.d(Constants.TAG, MSG + "onBind : ");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Logger.d(Constants.TAG, MSG + "onUnbind : ");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Logger.d(Constants.TAG, MSG + "onRebind : ");
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        Logger.d(Constants.TAG, MSG + "onDestroy : ");

        mAlarmManager.cancel(mPendingIntentBroadcast);

        // if service run in foreground
        stopForeground(true);



        super.onDestroy();
    }



}
