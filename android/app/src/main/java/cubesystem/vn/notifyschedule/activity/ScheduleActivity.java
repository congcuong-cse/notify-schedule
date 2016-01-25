package cubesystem.vn.notifyschedule.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import cubesystem.vn.notifyschedule.R;
import cubesystem.vn.notifyschedule.model.Schedule;
import cubesystem.vn.notifyschedule.request.ScheduleCreateRequest;
import cubesystem.vn.notifyschedule.request.ScheduleEditRequest;
import cubesystem.vn.notifyschedule.request.ScheduleShowRequest;
import cubesystem.vn.notifyschedule.response.ScheduleResponse;
import cubesystem.vn.notifyschedule.service.JsonSpiceService;

public class ScheduleActivity extends AppCompatActivity {

    public static final String TAG = "ScheduleActivity";

    protected SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);
    private Schedule mSchedule = new Schedule();;
    private State mState = State.CREATE;

    private EditText editTextFrom;
    private EditText editTextTo;
    private EditText editTextMessage;

    enum State{
        CREATE,
        UPDATE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new BtnSaveClickListener());

        editTextFrom = (EditText) findViewById(R.id.editTextFrom);
        editTextTo = (EditText) findViewById(R.id.editTextTo);
        editTextMessage = (EditText) findViewById(R.id.editTextMessage);

        String schedule_id = null;
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            schedule_id= extras.getString("schedule_id");
        }

        if (schedule_id != null){
            getScheduleFromServer(schedule_id);
            updateState(State.UPDATE);
        }



        /*
        //region Floating button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //endregion
        */
    }

    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    private void updateState(State s){
        mState = s;
        Button btnSave = (Button) findViewById(R.id.btnSave);
        switch (mState){
            case CREATE:
                this.setTitle(R.string.title_activity_schedule_create);
                btnSave.setText(R.string.create);
            case UPDATE:
                this.setTitle(R.string.title_activity_schedule_update);
                btnSave.setText(R.string.update);
        }
    }

    private void updateUI(){
        editTextFrom.setText(mSchedule.getStart_time());
        editTextTo.setText(mSchedule.getEnd_time());
        editTextMessage.setText(mSchedule.getMessage());
    }

    private void getScheduleFromServer(String schedule_id){
        ScheduleActivity.this.setProgressBarIndeterminateVisibility(true);

        ScheduleShowRequest request = new ScheduleShowRequest(schedule_id);
        String lastRequestCacheKey = request.createCacheKey();

        spiceManager.execute(request, lastRequestCacheKey, DurationInMillis.ONE_MINUTE, new UpdateScheduleListener());
    }


    private void createScheduleToServer(){
        ScheduleActivity.this.setProgressBarIndeterminateVisibility(true);

        ScheduleCreateRequest request = new ScheduleCreateRequest(mSchedule);
        String lastRequestCacheKey = request.createCacheKey();

        spiceManager.execute(request, lastRequestCacheKey, DurationInMillis.ONE_MINUTE, new CreateScheduleListener());
    }

    private void updateScheduleToServer(){
        ScheduleActivity.this.setProgressBarIndeterminateVisibility(true);

        ScheduleEditRequest request = new ScheduleEditRequest(mSchedule);
        String lastRequestCacheKey = request.createCacheKey();

        spiceManager.execute(request, lastRequestCacheKey, DurationInMillis.ONE_MINUTE, new UpdateScheduleListener());
    }

    private class BtnSaveClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            mSchedule.setStart_time(editTextFrom.getText().toString());
            mSchedule.setEnd_time(editTextTo.getText().toString());
            mSchedule.setMessage(editTextMessage.getText().toString());

            if (mState == State.CREATE){
                createScheduleToServer();
            }
            else {
                updateScheduleToServer();
            }
        }
    }

    private class UpdateScheduleListener implements RequestListener<ScheduleResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            //update your UI
            Log.e(TAG, spiceException.getMessage());
            Toast.makeText(getBaseContext(), spiceException.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(ScheduleResponse scheduleResponse) {
            //update your UI
            Log.d(TAG, scheduleResponse.toString());

            if (scheduleResponse.isSuccess()) {
                mSchedule = scheduleResponse.getData();
                updateUI();
            } else {
                Toast.makeText(getBaseContext(), scheduleResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class CreateScheduleListener implements RequestListener<ScheduleResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            //update your UI
            Log.e(TAG, spiceException.getMessage());
            Toast.makeText(getBaseContext(), spiceException.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(ScheduleResponse scheduleResponse) {
            //update your UI
            Log.d(TAG, scheduleResponse.toString());

            if (scheduleResponse.isSuccess()) {
                mSchedule = scheduleResponse.getData();
                updateUI();
                updateState(State.UPDATE);
            } else {
                Toast.makeText(getBaseContext(), scheduleResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //private class BtnSaveClickHandler implements Click

}
