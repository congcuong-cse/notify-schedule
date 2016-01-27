package cubesystem.vn.notifyschedule.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Calendar;

import cubesystem.vn.notifyschedule.view.TimePreference;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Schedule {

    public static int getHour(String time) {
        String[] pieces = time.split(":");

        return (Integer.parseInt(pieces[0]));
    }

    public static int getMinute(String time) {
        String[] pieces = time.split(":");

        return (Integer.parseInt(pieces[1]));
    }

    private int id;
    private String start_time;
    private String end_time;
    private String message;
    private boolean del_flag;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isDel_flag() {
        return del_flag;
    }

    public void setDel_flag(boolean del_flag) {
        this.del_flag = del_flag;
    }

    public String timeRemaining(Calendar calendar) {

        int startTime = Schedule.getHour(start_time) * 3600 + Schedule.getMinute(start_time) * 60;
        int endTime = Schedule.getHour(end_time) * 3600 + Schedule.getMinute(end_time) * 60;
        int currentTime = calendar.get(Calendar.HOUR_OF_DAY) * 3600 + calendar.get(Calendar.MINUTE) * 60;

        if (startTime <= currentTime && currentTime <= endTime) {
            int remaining_sec = endTime - currentTime;
            int remaning_hour = remaining_sec / 3600;
            int remaning_min = remaining_sec % 3600 / 60;
            return String.format("%d:%d", remaning_hour, remaning_min);
        }

        return null;
    }

    public MultiValueMap<String, String> requestParameters() {

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
        parameters.set("start_time", this.getStart_time());
        parameters.set("end_time", this.getEnd_time());
        parameters.set("message", this.getMessage());

        return parameters;
    }


}