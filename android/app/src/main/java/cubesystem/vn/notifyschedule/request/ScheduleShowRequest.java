package cubesystem.vn.notifyschedule.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import cubesystem.vn.notifyschedule.model.Setting;
import cubesystem.vn.notifyschedule.response.ScheduleResponse;

/**
 * Created by congcuong on 2016/01/25.
 */
public class ScheduleShowRequest extends SpringAndroidSpiceRequest<ScheduleResponse> {

    protected int mScheduleId;

    public ScheduleShowRequest(int schedule_id) {
        super(ScheduleResponse.class);

        mScheduleId = schedule_id;
    }

    @Override
    public ScheduleResponse loadDataFromNetwork() throws Exception {

        String url = Setting.hostAppendSub("/schedules/" + mScheduleId);

        return getRestTemplate().getForObject(url, ScheduleResponse.class);

    }
}
