package rikkei.android.http.lib.params;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by tuyenpx on 12/05/2016.
 */
public class RkHttpJsonParam extends RkHttpParam {
    private static final String TAG = RkHttpJsonParam.class.getSimpleName();

    private static final String JSON_CONTENT_TYPE_REQUEST = "application/json; charset=%s";

    public RkHttpJsonParam() {
        super();
    }

    /**
     * @return : body of HTTP Json request .
     * @throws UnsupportedEncodingException : when encoding is not supported
     */

    @Override
    public String getBody() throws UnsupportedEncodingException {
        if (requestParams == null || requestParams.size() == 0) {
            return "";
        }
        JSONObject jsonParams = new JSONObject();
        try {
            for (Map.Entry<String, Object> entry : requestParams.entrySet()) {
                if (null == entry.getValue()) {
                    continue;
                }
                jsonParams.put(URLEncoder.encode(entry.getKey(), getParamsEncoding()), URLEncoder.encode(String.valueOf(entry.getValue()), getParamsEncoding()));
            }
            return jsonParams.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @return content type of HTTP Json Request .
     */

    @Override
    public String getContentType() {
        return String.format(JSON_CONTENT_TYPE_REQUEST, getParamsEncoding());
    }


}
