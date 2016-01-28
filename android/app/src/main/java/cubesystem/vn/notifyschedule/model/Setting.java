package cubesystem.vn.notifyschedule.model;

/**
 * Created by congcuong on 2016/01/22.
 */
public class Setting {
    public static String host() {
        return "http://192.168.1.226:8000";
    }

    public static String hostAppendSub(String subUrl) {
        return Setting.host() + subUrl;
    }

    // constant
    public static long NOTIFY_INTERVAL() {
        return  60 * 1000; // 60 seconds
    }

    public static int TIME_OUT() {
        return 5000; // 5 seconds
    }
}
