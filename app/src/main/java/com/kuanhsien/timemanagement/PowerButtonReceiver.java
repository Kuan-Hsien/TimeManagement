package com.kuanhsien.timemanagement;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.kuanhsien.timemanagement.service.MainService;
import com.kuanhsien.timemanagement.utils.Constants;
import com.kuanhsien.timemanagement.utils.Logger;

/**
 * Created by Ken on 2018/10/11.
 *
 * Listen ACTION_BOOT_COMPLETED to open service to continuous listening ACTION_SCREEN_ON intent
 * Listen ACTION_SCREEN_ON to start tracing activity
 */
public class PowerButtonReceiver extends BroadcastReceiver {

    private static final String MSG = "PowerButtonReceiver: ";

    @Override
    public void onReceive(Context context, Intent intent) {

        Logger.d(Constants.TAG, MSG + "onReceive: OK!");

        switch (intent.getAction()) {

            // 當監聽到手機開機時，自動開啟 foreground service 以長期監聽手機 ACTION_SCREEN_ON broadcast
            case Intent.ACTION_BOOT_COMPLETED:
                Logger.d(Constants.TAG, MSG + "onReceive: ACTION_BOOT_COMPLETED");

                startServiceForReceiver(context);

                break;

            // 當監聽到手機解鎖時，開啟追蹤時間的 activity
            case Intent.ACTION_SCREEN_ON:
                Logger.d(Constants.TAG, MSG + "onReceive: ACTION_SCREEN_ON");

                Intent intentActivity = new Intent(context, MainActivity.class);
                context.startActivity(intentActivity);

                break;

//            case Intent.ACTION_SCREEN_OFF:
//                Logger.d(Constants.TAG, MSG + "onReceive: ACTION_SCREEN_OFF");
//                break;
//            case Intent.ACTION_USER_PRESENT:
//                Logger.d(Constants.TAG, MSG + "onReceive: ACTION_USER_PRESENT");
//                break;
//            case Intent.ACTION_AIRPLANE_MODE_CHANGED:
//                Logger.d(Constants.TAG, MSG + "onReceive: ACTION_AIRPLANE_MODE_CHANGED");
//                break;
//            case Intent.ACTION_BATTERY_CHANGED:
//                Logger.d(Constants.TAG, MSG + "onReceive: ACTION_BATTERY_CHANGED");
//                break;
//            case Intent.ACTION_BATTERY_LOW:
//                Logger.d(Constants.TAG, MSG + "onReceive: ACTION_BATTERY_LOW");
//                break;
//            case Intent.ACTION_BATTERY_OKAY:
//                Logger.d(Constants.TAG, MSG + "onReceive: ACTION_BATTERY_OKAY");
//                break;
//            case "com.kuanhsien.app.ACTION_BROADCAST_SERVICE":
//                Logger.d(Constants.TAG, MSG + "onReceive: ACTION_BROADCAST_SERVICE");
//                break;

            default:
                Logger.d(Constants.TAG, MSG + "onReceive: no match action code");
                break;
        }

    }


    // ****** Service ******
    // Service to register receiver for ACTION_SCREEN_ON intent (to present lock-screen effect)

    private void startServiceForReceiver(Context context) {

        Intent intentService = new Intent(context, MainService.class);

        // after Android 8, need to start service at foreground
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Logger.d(Constants.TAG, MSG + "start foreground service");
            context.startForegroundService(intentService);

        } else {

            Logger.d(Constants.TAG, MSG + "start background service");
            context.startService(intentService);
        }
    }

    private void stopServiceForReceiver(Context context) {
        context.stopService(new Intent(context, MainService.class));    // 停止更新時間服務
    }
}
