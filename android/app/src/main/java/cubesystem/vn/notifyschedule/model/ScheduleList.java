package cubesystem.vn.notifyschedule.model;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

//We use ArrayList as base class since the root element is a JSON array
public class ScheduleList extends ArrayList<Schedule> {

    @Override
    public String toString() {
        ArrayList<String> stringList = new ArrayList<String>();
        for (Schedule schedule: this){
            stringList.add(schedule.toString());
        }
        return String.format("%s [%s]", this.getClass().getSimpleName(), StringUtils.join(stringList, ", "));
    }
}