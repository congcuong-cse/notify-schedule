package cubesystem.vn.notifyschedule.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import cubesystem.vn.notifyschedule.R;
import cubesystem.vn.notifyschedule.model.Schedule;
import cubesystem.vn.notifyschedule.request.ScheduleCreateRequest;
import cubesystem.vn.notifyschedule.request.ScheduleEditRequest;
import cubesystem.vn.notifyschedule.request.ScheduleShowRequest;
import cubesystem.vn.notifyschedule.response.ScheduleResponse;
import cubesystem.vn.notifyschedule.service.JsonSpiceService;
import cubesystem.vn.notifyschedule.view.SetTime;

public class ScheduleActivity extends AppCompatActivity {

    public static final String TAG = "ScheduleActivity";

    protected SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);
    private Schedule mSchedule = new Schedule();
    private State mState = State.CREATE;

    private TextInputLayout textInputLayoutFrom;
    private TextInputLayout textInputLayoutTo;
    private TextInputLayout textInputLayoutMessage;
    private EditText editTextFrom;
    private EditText editTextTo;
    private EditText editTextMessage;
    private SetTime fromTime;
    private SetTime toTime;

    enum State {
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

        textInputLayoutFrom = (TextInputLayout) findViewById(R.id.textInputLayoutFrom);
        textInputLayoutTo = (TextInputLayout) findViewById(R.id.textInputLayoutTo);
        textInputLayoutMessage = (TextInputLayout) findViewById(R.id.textInputLayoutMessage);

        editTextFrom = (EditText) findViewById(R.id.editTextFrom);
        editTextTo = (EditText) findViewById(R.id.editTextTo);
        editTextMessage = (EditText) findViewById(R.id.editTextMessage);

        fromTime = new SetTime(editTextFrom, this);
        toTime = new SetTime(editTextTo, this);

        fromTime.setOnChangeListener(new SetTime.OnChangeListener() {
            @Override
            public void onChange(int newSeconds) {
                textInputLayoutFrom.setError(null);
                textInputLayoutFrom.setErrorEnabled(false);
                if (newSeconds < toTime.getSeconds()){
                    textInputLayoutTo.setError(null);
                    textInputLayoutTo.setErrorEnabled(false);
                }
                else {
                    textInputLayoutTo.setError(getString(R.string.error_endtime_should_greater_than_starttime));
                    textInputLayoutTo.setErrorEnabled(true);
                }
            }
        });

        toTime.setOnChangeListener(new SetTime.OnChangeListener() {
            @Override
            public void onChange(int newSeconds) {
                textInputLayoutTo.setError(null);
                textInputLayoutTo.setErrorEnabled(false);
                if (newSeconds > fromTime.getSeconds()){
                    textInputLayoutFrom.setError(null);
                    textInputLayoutFrom.setErrorEnabled(false);
                }
                else {
                    textInputLayoutFrom.setError(getString(R.string.error_starttime_should_less_than_endtime));
                    textInputLayoutFrom.setErrorEnabled(true);
                }
            }
        });


        int schedule_id = -1;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            schedule_id = extras.getInt("schedule_id");
        }

        if (schedule_id > 0) {
            getScheduleFromServer(schedule_id);
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

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public void finish() {
        setResult(RESULT_OK, null);
        super.finish();
    }

    private void updateState(State s) {
        mState = s;
        Button btnSave = (Button) findViewById(R.id.btnSave);
        switch (mState) {
            case CREATE:
                this.setTitle(R.string.title_activity_schedule_create);
                btnSave.setText(R.string.create);
            case UPDATE:
                this.setTitle(R.string.title_activity_schedule_update);
                btnSave.setText(R.string.update);
        }
    }

    private void updateUI() {
        editTextFrom.setText(mSchedule.getStart_time());
        editTextTo.setText(mSchedule.getEnd_time());
        editTextMessage.setText(mSchedule.getMessage());
    }

    private void getScheduleFromServer(int schedule_id) {
        ScheduleActivity.this.setProgressBarIndeterminateVisibility(true);

        ScheduleShowRequest request = new ScheduleShowRequest(schedule_id);

        spiceManager.execute(request, new GetScheduleListener());
    }


    private void createScheduleToServer() {
        ScheduleActivity.this.setProgressBarIndeterminateVisibility(true);

        ScheduleCreateRequest request = new ScheduleCreateRequest(mSchedule);

        spiceManager.execute(request, new CreateScheduleListener());
    }

    private void updateScheduleToServer() {
        ScheduleActivity.this.setProgressBarIndeterminateVisibility(true);

        ScheduleEditRequest request = new ScheduleEditRequest(mSchedule);

        spiceManager.execute(request, new UpdateScheduleListener());
    }

    private class BtnSaveClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            boolean isValid = true;
            textInputLayoutFrom.setError(null);
            textInputLayoutFrom.setErrorEnabled(false);
            textInputLayoutTo.setError(null);
            textInputLayoutTo.setErrorEnabled(false);
            textInputLayoutMessage.setError(null);
            textInputLayoutMessage.setErrorEnabled(false);

            if (fromTime.getSeconds() < 0) {
                textInputLayoutFrom.setError(getString(R.string.error_should_not_blank));
                textInputLayoutFrom.setErrorEnabled(true);
                isValid = false;
            }

            if (toTime.getSeconds() < 0) {
                textInputLayoutTo.setError(getString(R.string.error_should_not_blank));
                textInputLayoutTo.setErrorEnabled(true);
                isValid = false;
            }

            if (isValid) {

                if (fromTime.getSeconds() >= toTime.getSeconds()) {
                    textInputLayoutFrom.setError(getString(R.string.error_starttime_should_less_than_endtime));
                    textInputLayoutFrom.setErrorEnabled(true);
                    textInputLayoutTo.setError(getString(R.string.error_endtime_should_greater_than_starttime));
                    textInputLayoutTo.setErrorEnabled(true);
                    isValid = false;
                }
            }


            if (editTextMessage.getText().toString().trim().isEmpty()) {
                textInputLayoutMessage.setError(getString(R.string.error_should_not_blank));
                textInputLayoutMessage.setErrorEnabled(true);
                isValid = false;
            }

            if (!isValid) {
                return;
            }

            mSchedule.setStart_time(editTextFrom.getText().toString().trim());
            mSchedule.setEnd_time(editTextTo.getText().toString().trim());
            mSchedule.setMessage(editTextMessage.getText().toString().trim());

            if (mState == State.CREATE) {
                createScheduleToServer();
            } else {
                updateScheduleToServer();
            }
        }
    }

    private class GetScheduleListener implements RequestListener<ScheduleResponse> {

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
                //Toast.makeText(getBaseContext(), "update success", Toast.LENGTH_SHORT).show();
                finish();
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
                //Toast.makeText(getBaseContext(), "create success", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(getBaseContext(), scheduleResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //private class BtnSaveClickHandler implements Click

}
