package rikkei.android.http.lib.params;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by tuyenpx on 12/05/2016.
 */
public class RkHttpStringParam extends RkHttpParam {

    private static final String STRING_CONTENT_TYPE_REQUEST = "application/x-www-form-urlencoded; charset=%s";

    public RkHttpStringParam() {
        super();
    }


    /**
     * @return body of String http request
     * if encoding is not supported , will be throw UnsupportedEncodingException
     */

    @Override
    public String getBody() throws UnsupportedEncodingException {
        if (requestParams == null || requestParams.size() == 0) {
            return "";
        }
        StringBuilder encodedParams = new StringBuilder();

        for (Map.Entry<String, Object> entry : requestParams.entrySet()) {
            if (null == entry.getValue()) {
                continue;
            }
            encodedParams.append(URLEncoder.encode(entry.getKey(), getParamsEncoding()));
            encodedParams.append('=');
            encodedParams.append(URLEncoder.encode(String.valueOf(entry.getValue()), getParamsEncoding()));
            encodedParams.append('&');
        }
        if (encodedParams.length() > 1) {
            encodedParams.deleteCharAt(encodedParams.length() - 1);
        }
        return encodedParams.toString();
    }

    /**
     * @return content type of HTTP request .
     */

    @Override
    public String getContentType() {
        return String.format(STRING_CONTENT_TYPE_REQUEST, getParamsEncoding());
    }
}
