package cubesystem.vn.notifyschedule.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import cubesystem.vn.notifyschedule.R;
import cubesystem.vn.notifyschedule.activity.ScheduleListActivity;
import cubesystem.vn.notifyschedule.model.Schedule;
import cubesystem.vn.notifyschedule.model.ScheduleList;
import cubesystem.vn.notifyschedule.model.Setting;
import cubesystem.vn.notifyschedule.request.ScheduleAllRequest;
import cubesystem.vn.notifyschedule.response.ScheduleAllResponse;

public class TimeService extends Service {
    final static String TAG = "TimeService";

    private SpiceManager spiceManager;

    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;

    private ScheduleList mScheduleList;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // service
        if (spiceManager != null){
            spiceManager.shouldStop();
        } else {
            spiceManager = new SpiceManager(JsonSpiceService.class);
        }

        spiceManager.start(this);

        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, Setting.NOTIFY_INTERVAL());
    }

    private void performRequest() {

        ScheduleAllRequest request = new ScheduleAllRequest();

        spiceManager.execute(request, new RequestListener<ScheduleAllResponse>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.e(TAG, spiceException.getMessage());
                //Toast.makeText(getBaseContext(), spiceException.getMessage(), Toast.LENGTH_SHORT).show();
                notificate();
            }

            @Override
            public void onRequestSuccess(ScheduleAllResponse scheduleAllResponse) {
                Log.d(TAG, scheduleAllResponse.toString());

                if (scheduleAllResponse.isSuccess()) {
                    mScheduleList = scheduleAllResponse.getData();
                } else {
                    Log.e(TAG, "ScheduleAllReponse: " + scheduleAllResponse.getMessage());
                    //Toast.makeText(getBaseContext(), scheduleAllResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }

                notificate();

            }
        });
    }

    private void notificate(){
        if (mScheduleList != null){
            Calendar now = Calendar.getInstance();

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancelAll();

            for (Schedule shSchedule : mScheduleList) {

                String remaining = shSchedule.timeRemaining(now);
                if (remaining != null) {

                    //Intent resultIntent = new Intent(getBaseContext(), ScheduleListActivity.class);
                    //PendingIntent resultPendingIntent = PendingIntent.getActivity(getBaseContext(), 0, resultIntent, 0);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
                    Uri alertSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    builder.setSmallIcon(R.mipmap.ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                            .setContentTitle(shSchedule.getMessage())
                            .setContentText(String.format("%s後", remaining))
                            .setContentIntent(null)
                            .setFullScreenIntent(null, true)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setPriority(Notification.PRIORITY_MAX)
                            .setSound(alertSound)
                            .setVisibility(Notification.VISIBILITY_PUBLIC);


                    notificationManager.notify(shSchedule.getId(), builder.getNotification());
                }

            }
        }
    }

    private class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    performRequest();
                }
            });
        }
    }
}