package cubesystem.vn.notifyschedule.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cubesystem.vn.notifyschedule.R;
import cubesystem.vn.notifyschedule.model.Schedule;
import cubesystem.vn.notifyschedule.model.ScheduleList;



public class ScheduleListAdapter extends ArrayAdapter<Schedule> {

        public interface ScheduleListAdapterEventHandler {
            void onDeleteCell(ScheduleListAdapter scheduleListAdapter, int position);
        }
        protected ScheduleListAdapterEventHandler mEventHandler;

        public void setEventHandle(ScheduleListAdapterEventHandler eventHandle){
            mEventHandler = eventHandle;
        }

        public ScheduleListAdapter(Context context, int resource, ScheduleList scheduleList) {
            super(context, resource, scheduleList);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Schedule schedule = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.schedule_list_item, parent, false);
            }
            // Lookup view for data population
            TextView tvStartTime = (TextView) convertView.findViewById(R.id.schedule_start_time);
            TextView tvEndTime = (TextView) convertView.findViewById(R.id.schedule_end_time);
            TextView tvMessage = (TextView) convertView.findViewById(R.id.schedule_message);
            ImageView ivDelete = (ImageView) convertView.findViewById(R.id.delete);
            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mEventHandler != null){
                        mEventHandler.onDeleteCell(ScheduleListAdapter.this, position);
                    }
                }
            });

            // Populate the data into the template view using the data object
            tvStartTime.setText(schedule.getStart_time());
            tvEndTime.setText(schedule.getEnd_time());
            tvMessage.setText(schedule.getMessage());
            // Return the completed view to render on screen
            return convertView;
        }

    }