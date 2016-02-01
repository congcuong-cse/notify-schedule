package cubesystem.vn.notifyschedule.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cubesystem.vn.notifyschedule.service.TimeService;

public class BootCompleteReceiver extends BroadcastReceiver {

   public static final String TAG = "BootCompleteReceiver";

   @Override
   public void onReceive(Context context, Intent intent) {
       Log.d(TAG, "onReceive");
       context.startService(new Intent(context, TimeService.class));
   }
}