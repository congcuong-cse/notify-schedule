package cubesystem.vn.notifyschedule.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

import cubesystem.vn.notifyschedule.model.Schedule;
import cubesystem.vn.notifyschedule.response.ScheduleResponse;

/**
 * Created by congcuong on 2016/01/22.
 */
public class ScheduleEditRequest extends SpringAndroidSpiceRequest<ScheduleResponse> {

    private Schedule mSchedule;

    public ScheduleEditRequest(Schedule schedule){
        super(ScheduleResponse.class);

        mSchedule = schedule;
    }

    @Override
    public ScheduleResponse loadDataFromNetwork() throws Exception {

        String url = ServiceRequestConstrant.hostAppendSub("/schedule/edit/" + mSchedule.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<MultiValueMap<String, String>>(mSchedule.requestParameters(), headers);

        return getRestTemplate().postForObject(url, request, ScheduleResponse.class);
    }

    /**
     * This method generates a unique cache key for this request.
     * In this case our cache key depends just on the keyword.
     *
     * @return
     */
    public String createCacheKey() {
        return "schedule_create";
    }
}
