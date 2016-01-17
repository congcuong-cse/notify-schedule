package cubesystem.vn.notifyschedule.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import cubesystem.vn.notifyschedule.model.ScheduleList;

//Create a request in its own Java file, it should not an inner class of a Context
public class ScheduleRequest extends SpringAndroidSpiceRequest<ScheduleList> {

    public ScheduleRequest() {
        super(ScheduleList.class);
    }

    @Override
    public ScheduleList loadDataFromNetwork() throws Exception {

        String url = "http://192.168.1.226/schedules";

        return getRestTemplate().getForObject(url, ScheduleList.class);
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