package rikkei.android.http.lib;

import android.os.AsyncTask;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import rikkei.android.http.lib.params.RkHttpMultipartParam;
import rikkei.android.http.lib.params.RkHttpParam;

/**
 * Created by datpt2 on 5/13/2016.
 */
public class RkHttpRequest extends AsyncTask<Void, Integer, RkHttpResponse> {

    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final int DEFAULT_TIMEOUT = 10000;
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";

    private int mTimeout = DEFAULT_TIMEOUT;
    private String mMethod;
    private String mUrl;
    private Map<String, String> mHeaders;
    private RkHttpParam mParams;
    private RkHttpCallback mCallback;

    public RkHttpRequest(String method, String url, Map<String, String> headers, RkHttpParam params, RkHttpCallback callback) {
        mMethod = method;
        mUrl = url;
        mHeaders = headers;
        mParams = params;
        mCallback = callback;
    }

    private HttpURLConnection openConnection() throws IOException {
        if (TextUtils.isEmpty(mUrl)) {
            return null;
        }
        HttpURLConnection connection;
        if (mParams != null) {
            if ((mMethod.equalsIgnoreCase(GET) || mMethod.equalsIgnoreCase(DELETE)) && !TextUtils.isEmpty(mParams.getBody())) {
                mUrl += "?" + mParams.getBody();
            }
        }
        URL url = new URL(mUrl);
        connection = (HttpURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(HttpURLConnection.getFollowRedirects());
        connection.setRequestMethod(mMethod);
        connection.setConnectTimeout(mTimeout);
        connection.setReadTimeout(mTimeout);
        connection.setUseCaches(false);

        // use caller-provided custom SslSocketFactory, if any, for HTTPS
        if ("https".equals(url.getProtocol())) {
            ((HttpsURLConnection) connection).setSSLSocketFactory((SSLSocketFactory) SSLSocketFactory.getDefault());
        }
        return connection;
    }

    private HttpURLConnection connectionBuilder(HttpURLConnection connection) throws IOException {
        if (connection != null) {
            if (mHeaders != null && mHeaders.size() > 0) {
                for (String key : mHeaders.keySet()) {
                    connection.setRequestProperty(key, mHeaders.get(key));
                }
            }
            return connection;
        } else {
            throw new IOException();
        }
    }

    private RkHttpResponse performRequest(HttpURLConnection connection) throws IOException {
        if (connection != null) {
            connection.setRequestProperty(CONTENT_TYPE, mParams.getContentType());
            if (!mMethod.equalsIgnoreCase(GET) && mParams != null && !mMethod.equalsIgnoreCase(DELETE) ) {
                connection.setDoOutput(true);
                if (mParams instanceof RkHttpMultipartParam) {
                    ((RkHttpMultipartParam) mParams).setBodyToRequest(connection, null);
                } else {
                    OutputStream outputStream = connection.getOutputStream();
                    byte[] byteToPost = mParams.getBody().getBytes("UTF-8");
                    outputStream.write(byteToPost);
                    outputStream.flush();
                    outputStream.close();
                }
            }
            connection.connect();

            // Get status Code
            int status = connection.getResponseCode();
            String result;
            try {
                // Get InputStream
                InputStream is = connection.getInputStream();
                // Convert the InputStream into a string
                result = readIS(is);
            } catch (IOException ioe) {
                ioe.printStackTrace();
                result = null;
            }
            return new RkHttpResponse(status, result);
        } else {
            throw new IOException();
        }
    }

    private String readIS(InputStream stream) throws IOException {
        String charset = "UTF-8";
        BufferedReader r = new BufferedReader(new InputStreamReader(stream, charset));
        StringBuilder total = new StringBuilder();
        String line;

        while ((line = r.readLine()) != null) {
            total.append(line);
        }

        byte[] bytes = total.toString().getBytes();
        return new String(bytes, Charset.forName(charset));
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mCallback != null) {
            mCallback.onStart();
        }
    }

    @Override
    protected RkHttpResponse doInBackground(Void... params) {
        RkHttpResponse response = null;
        try {
            HttpURLConnection connection = openConnection();
            connectionBuilder(connection);
            response = performRequest(connection);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            if (mCallback != null) {
                mCallback.onFailure(-1, ioe);
            }
        }
        return response;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (mCallback != null) {
            mCallback.onProgressUpdate(values[0]);
        }
    }

    @Override
    protected void onPostExecute(RkHttpResponse rkHttpResponse) {
        super.onPostExecute(rkHttpResponse);
        if (rkHttpResponse != null) {
            int status = rkHttpResponse.getStatus();
            String result = rkHttpResponse.getResult();
            if (200 <= status && status < 300) {
                if (mCallback != null) {
                    mCallback.onSuccess(result);
                }
            } else {
                if (mCallback != null) {
                    mCallback.onFailure(status, null);
                }
            }
        }
    }
}
