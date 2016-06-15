package rikkei.android.http.lib;

import java.util.Map;

import rikkei.android.http.lib.params.RkHttpJsonParam;
import rikkei.android.http.lib.params.RkHttpMultipartParam;
import rikkei.android.http.lib.params.RkHttpParam;
import rikkei.android.http.lib.params.RkHttpStringParam;

/**
 * Created by datpt2 on 5/13/2016.
 */
public class RkHttpConnectionHelper {

    private static void request(String method, RkHttpRequestType requestType, String url, Map<String, String> headers, Map<String, Object> params, RkHttpCallback callback) {
        RkHttpParam param = null;
        if (params != null && params.size() > 0) {
            switch (requestType) {
                case STRING_REQUEST:
                    param = new RkHttpStringParam();
                    break;
                case JSON_REQUEST:
                    param = new RkHttpJsonParam();
                    break;
                case MULTIPART_REQUEST:
                    param = new RkHttpMultipartParam();
                    break;
                default:
                    param = new RkHttpStringParam();
                    break;
            }
            for (String key : params.keySet()) {
                param.addToParams(key, params.get(key));
            }
        }

        new RkHttpRequest(method, url, headers, param, callback).execute();
    }

    public static void get(String url, Map<String, String> headers, Map<String, Object> params, RkHttpCallback callback) {
        request(RkHttpRequest.GET, RkHttpRequestType.STRING_REQUEST, url, headers, params, callback);
    }

    public static void post(RkHttpRequestType requestType, String url, Map<String, String> headers, Map<String, Object> params, RkHttpCallback callback) {
        request(RkHttpRequest.POST, requestType, url, headers, params, callback);
    }

    public static void put(RkHttpRequestType requestType, String url, Map<String, String> headers, Map<String, Object> params, RkHttpCallback callback) {
        request(RkHttpRequest.PUT, requestType, url, headers, params, callback);
    }

    public static void delete(String url, Map<String, String> headers, Map<String, Object> params, RkHttpCallback callback) {
        request(RkHttpRequest.DELETE, RkHttpRequestType.STRING_REQUEST, url, headers, params, callback);
    }
}
