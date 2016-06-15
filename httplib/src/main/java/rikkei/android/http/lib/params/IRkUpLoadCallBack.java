package rikkei.android.http.lib.params;

/**
 * Created by tuyenpx on 13/05/2016.
 * this listener use to know about progress status upload file to server .
 */
public interface IRkUpLoadCallBack {
    void onProgress(long transferredBytes, long totalSize);
}
