package cubesystem.vn.notifyschedule.request;

/**
 * Created by congcuong on 2016/01/22.
 */
public class ServiceRequestConstrant {
    public static String host(){
        return "http://192.168.11.28:8000";
    }

    public static String hostAppendSub(String subUrl){
        return ServiceRequestConstrant.host() + subUrl;
    }
}
