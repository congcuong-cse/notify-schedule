package cubesystem.vn.notifyschedule.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by admin on 1/22/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceResponse<T> {
    private boolean success = true;
    private String code = "";
    private String message = "";
    private T data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return String.format("%s { success: %s, code: %s, message: %s, data: %s}",
                this.getClass().getSimpleName(), this.isSuccess(), this.getCode(), this.getMessage(), this.getData().toString());
    }
}
