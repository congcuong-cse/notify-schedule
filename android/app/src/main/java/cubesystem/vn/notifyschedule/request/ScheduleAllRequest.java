package cubesystem.vn.notifyschedule.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import cubesystem.vn.notifyschedule.response.ScheduleAllResponse;

//Create a request in its own Java file, it should not an inner class of a Context
public class ScheduleAllRequest extends SpringAndroidSpiceRequest<ScheduleAllResponse> {

    public ScheduleAllRequest() {
        super(ScheduleAllResponse.class);
    }

    @Override
    public ScheduleAllResponse loadDataFromNetwork() throws Exception {

        String url = ServiceRequestConstrant.hostAppendSub("/schedules");

        return getRestTemplate().getForObject(url, ScheduleAllResponse.class);
    }

    /**
     * This method generates a unique cache key for this request. In this case
     * our cache key depends just on the keyword.
     *
     * @return
     */
    public String createCacheKey() {
        return "schedules";
    }
}