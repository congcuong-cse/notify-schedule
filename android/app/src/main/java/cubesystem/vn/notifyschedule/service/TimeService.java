package cubesystem.vn.notifyschedule.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
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
import cubesystem.vn.notifyschedule.model.Schedule;
import cubesystem.vn.notifyschedule.request.ScheduleAllRequest;
import cubesystem.vn.notifyschedule.response.ScheduleAllResponse;

public class TimeService extends Service {
    final static String TAG = "TimeService";

    private SpiceManager spiceManager;

    // constant
    public static final long NOTIFY_INTERVAL = 5 * 1000; // 60 seconds

    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;

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
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
    }

    private void performRequest() {

        ScheduleAllRequest request = new ScheduleAllRequest();
        String lastRequestCacheKey = request.createCacheKey();

        spiceManager.execute(request, lastRequestCacheKey, DurationInMillis.ONE_MINUTE, new RequestListener<ScheduleAllResponse>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.e(TAG, spiceException.getMessage());
                Toast.makeText(getBaseContext(), spiceException.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRequestSuccess(ScheduleAllResponse scheduleAllResponse) {
                Log.d(TAG, scheduleAllResponse.toString());

                if (scheduleAllResponse.isSuccess()) {
                    ArrayList<String> messArr = new ArrayList<>();

                    Calendar now = Calendar.getInstance();

                    for (Schedule shSchedule : scheduleAllResponse.getData()) {
                        String remaining = shSchedule.timeRemaining(now);
                        if (remaining != null) {
                            messArr.add(String.format("%s nua la: %s(%s)", remaining, shSchedule.getTitle(), shSchedule.getDescription()));
                        }
                    }

                    if (messArr.size() > 0) {
                        //Toast.makeText(getBaseContext(), StringUtils.join(messArr), Toast.LENGTH_SHORT).show();
                        Context intent = getBaseContext();

                        //PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

                        // build notification
                        // the addAction re-use the same intent to keep the example short
                        Notification n  = new Notification.Builder(intent)
                                .setContentTitle("Title")
                                .setContentText(StringUtils.join(messArr, "\r\n"))
                                .setSmallIcon(R.drawable.notification_template_icon_bg)
                                //.setContentIntent(pIntent)
                                .setAutoCancel(true)
                                //.addAction(new Notification.Action.Builder(R.drawable.notification_template_icon_bg, StringUtils.join(messArr), intent));
                                .build();


                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        notificationManager.notify(0, n);
                    }

                } else {
                    Toast.makeText(getBaseContext(), scheduleAllResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
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