package cubesystem.vn.notifyschedule.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import cubesystem.vn.notifyschedule.response.ScheduleAllResponse;
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

        String url = ServiceRequestConstrant.hostAppendSub("/schedules/" + mScheduleId);

        return getRestTemplate().getForObject(url, ScheduleResponse.class);

    }

    /**
     * This method generates a unique cache key for this request.
     * In this case our cache key depends just on the keyword.
     *
     * @return
     */
    public String createCacheKey() {
        return "schedule_show_" + mScheduleId;
    }
}
