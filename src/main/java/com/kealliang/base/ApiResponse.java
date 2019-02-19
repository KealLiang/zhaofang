package com.kealliang.base;

/**
 * @author lsr
 * @ClassName ApiResponse
 * @Date 2019-01-31
 * @Desc api返回状态实体
 * @Vertion 1.0
 */
public class ApiResponse {
    private int code;
    private String message;
    private Object data;
    private boolean more;

    public ApiResponse(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ApiResponse() {
        this.code = Status.SUCCESS.getCode();
        this.message = Status.SUCCESS.getMessage();
    }

    public static ApiResponse ofMessage(int code, String message){
        return new ApiResponse(code, message, null);
    }

    public static ApiResponse ofSuccess(Object data){
        return new ApiResponse(Status.SUCCESS.getCode(), Status.SUCCESS.getMessage(), data);
    }

    public static ApiResponse ofStatus(Status status){
        return new ApiResponse(status.getCode(), status.getMessage(), null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isMore() {
        return more;
    }

    public void setMore(boolean more) {
        this.more = more;
    }

    public enum Status{
        SUCCESS(200, "OK"),
        BAD_REQUEST(400, "Bad Request"),
        NOT_FOUND(404, "Not Found"),
        INTERNAL_SERVER_ERROR(500, "Unknown Server Error"),
        NOT_VALID_PARAM(40003, "Not Valid Params"),
        NOT_SUPPORTED_OPERATION(40005, "Not Support Operation"),
        NOT_LOGIN(50001, "Not Login");


        private int code;
        private String message;

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        Status(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
