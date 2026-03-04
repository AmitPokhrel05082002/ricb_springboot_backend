package bt.ricb.ricb_api.dao;

public class ApiResponseMTP<T> {
    private boolean success;
    private String message;
    private T data;
    private Object errors;
    
//    public static <T> ApiResponseMTP<T> success(T data, String message) {
//        return new ApiResponseMTP<>(true, message, data, null);
//    }
//    
//    public static <T> ApiResponseMTP<T> error(String message, Object errors) {
//        return new ApiResponseMTP<>(false, message, null, errors);
//    }
}
