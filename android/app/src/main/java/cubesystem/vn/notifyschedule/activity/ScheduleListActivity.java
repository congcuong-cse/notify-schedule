package cubesystem.vn.notifyschedule.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import cubesystem.vn.notifyschedule.R;
import cubesystem.vn.notifyschedule.adapter.ScheduleListAdapter;
import cubesystem.vn.notifyschedule.broadcast_receiver.BootCompleteReceiver;
import cubesystem.vn.notifyschedule.model.Schedule;
import cubesystem.vn.notifyschedule.model.ScheduleList;
import cubesystem.vn.notifyschedule.request.ScheduleAllRequest;
import cubesystem.vn.notifyschedule.request.ScheduleDeleteRequest;
import cubesystem.vn.notifyschedule.response.ScheduleAllResponse;
import cubesystem.vn.notifyschedule.response.ScheduleResponse;
import cubesystem.vn.notifyschedule.service.JsonSpiceService;
import cubesystem.vn.notifyschedule.service.TimeService;

public class ScheduleListActivity extends AppCompatActivity implements ScheduleListAdapter.ScheduleListAdapterEventHandler {

    final static String TAG = "ScheduleListActivity";

    protected SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);
    protected ListView mListView;
    protected ScheduleListAdapter mAdapter;
    protected Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListView = (ListView) findViewById(R.id.schedule_listview);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Schedule selectedSchedule = (Schedule) mListView.getAdapter().getItem(position);
                Intent myIntent = new Intent(ScheduleListActivity.this, ScheduleActivity.class);
                myIntent.putExtra("schedule_id", selectedSchedule.getId());
                startActivity(myIntent);

            }
        });

        startService(new Intent(this, TimeService.class));

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ((SwipeLayout) (mListView.getChildAt(position - mListView.getFirstVisiblePosition()))).open(true);
                return true;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(ScheduleListActivity.this, ScheduleActivity.class);
                startActivity(myIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        performRequest();
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_schedule_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reload) {
            performRequest();
            return true;
        }
        else if (id == R.id.action_create) {
            Intent myIntent = new Intent(ScheduleListActivity.this, ScheduleActivity.class);
            startActivity(myIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateData(ScheduleList scheduleList){
        mAdapter = new ScheduleListAdapter(getBaseContext(), R.layout.schedule_list_item, scheduleList);

        mAdapter.setEventHandle(this);

        mListView.setAdapter(mAdapter);
    }

    private void performRequest() {
        ScheduleListActivity.this.setProgressBarIndeterminateVisibility(true);

        ScheduleAllRequest request = new ScheduleAllRequest();

        spiceManager.execute(request, new ListScheduleRequestListener());
    }

    @Override
    public void onDeleteCell(final ScheduleListAdapter scheduleListAdapter, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleListActivity.this);
        builder.setMessage(R.string.message_delete);
        builder.setCancelable(true);

        builder.setPositiveButton(
                R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteScheduleToServer(position, scheduleListAdapter.getItem(position));
                    }
                });

        builder.setNegativeButton(
                R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder.create();
        alert11.show();
    }

    //inner class of your spiced Activity
    private class ListScheduleRequestListener implements RequestListener<ScheduleAllResponse> {

        @Override
        public void onRequestFailure(SpiceException e) {
            //update your UI
            Log.e(TAG, e.getMessage());
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(ScheduleAllResponse response) {
            //update your UI
            Log.d(TAG, response.toString());

            if (response.isSuccess()) {

                updateData(response.getData());

            } else {
                Toast.makeText(getBaseContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteScheduleToServer(int position, Schedule schedule) {
        this.setProgressBarIndeterminateVisibility(true);

        ScheduleDeleteRequest request = new ScheduleDeleteRequest(schedule);

        spiceManager.execute(request, new DeleteScheduleListener(position, schedule));
    }

    private class DeleteScheduleListener implements RequestListener<ScheduleResponse> {

        private int mPosision;
        private Schedule mSchedule;

        public DeleteScheduleListener(int position, Schedule schedule) {
            mPosision = position;
            mSchedule = schedule;
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            //update your UI
            Log.e(TAG, spiceException.getMessage());
            Toast.makeText(getBaseContext(), spiceException.getMessage(), Toast.LENGTH_SHORT).show();
            //ScheduleListAdapter lvAdapter = (ScheduleListAdapter) mListView.getAdapter();
        }

        @Override
        public void onRequestSuccess(ScheduleResponse scheduleResponse) {
            //update your UI
            Log.d(TAG, scheduleResponse.toString());
            ScheduleListAdapter lvAdapter = (ScheduleListAdapter) mListView.getAdapter();

            if (scheduleResponse.isSuccess()) {
                lvAdapter.remove(lvAdapter.getItem(mPosision));
            } else {
                Toast.makeText(getBaseContext(), scheduleResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
