package cubesystem.vn.notifyschedule.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import cubesystem.vn.notifyschedule.model.Setting;
import cubesystem.vn.notifyschedule.response.ScheduleAllResponse;

//Create a request in its own Java file, it should not an inner class of a Context
public class ScheduleAllRequest extends SpringAndroidSpiceRequest<ScheduleAllResponse> {

    public ScheduleAllRequest() {
        super(ScheduleAllResponse.class);
    }

    @Override
    public ScheduleAllResponse loadDataFromNetwork() throws Exception {

        String url = Setting.hostAppendSub("/schedules");

        return getRestTemplate().getForObject(url, ScheduleAllResponse.class);
    }
}