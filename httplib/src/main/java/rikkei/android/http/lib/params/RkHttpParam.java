package rikkei.android.http.lib.params;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tuyenpx on 12/05/2016.
 */

public abstract class RkHttpParam {

    /**
     * Default encoding for POST or PUT parameters. See {@link #getParamsEncoding()}.
     */
    private String mEncoding = "UTF-8";
    protected Map<String, Object> requestParams;

    public RkHttpParam() {
        if (requestParams == null) {
            requestParams = new HashMap<>();
        }
    }

    /**
     * @param keyParams : key params need to be added  .
     * @param value     : value of key params need to be added .
     */

    public void addToParams(String keyParams, Object value) {
        requestParams.put(keyParams, value);
    }


    /**
     * @return : list Params need to be added to http request .
     */

    public Map getRequestParams() {
        return requestParams;
    }

    public abstract String getBody() throws UnsupportedEncodingException;

    public abstract String getContentType();

    protected String getParamsEncoding() {
        return mEncoding;
    }

    /**
     * @param encoding used to encode params .
     *                 this function allow user custom encoding value .
     */

    public void setParamEncoding(String encoding) {
        mEncoding = encoding;
    }
}
