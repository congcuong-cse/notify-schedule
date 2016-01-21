package cubesystem.vn.notifyschedule.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.sql.Time;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Schedule {

    private int id;
    private String start_time;
    private String end_time;
    private String title;
    private String description;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDel_flag() {
        return del_flag;
    }

    public void setDel_flag(boolean del_flag) {
        this.del_flag = del_flag;
    }

    public String timeRemaining(Time startTime, Time endTime) {
        startTime = Time.valueOf(start_time);
        endTime = Time.valueOf(end_time);


        return null;
    }
}