package rikkei.android.common.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import rikkei.android.common.lib.RkDateTimeUtils;
import rikkei.android.common.lib.RkDialog;
import rikkei.android.common.lib.RkLogger;
import rikkei.android.common.sample.config.Config;
import rikkei.android.customview.lib.RkProgressBar;
import rikkei.android.http.lib.RkHttpCallback;
import rikkei.android.http.lib.RkHttpConnectionHelper;
import rikkei.android.http.lib.RkHttpRequestType;


@SuppressWarnings("UnusedDeclaration")
public class MainActivity extends Activity implements RkDialog.Callback {


    private static String TAG = MainActivity.class.getSimpleName();
    private RkDialog rkDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.test_twitter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DemoTwitterActivity.class));
            }
        });


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DemoFacebookActivity.class));
            }
        });


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//        testRkLogger();
//        testRkDialogWithCustomView();
//        testRkDateTimeUtils();
//        testRkDialogWithCustomAdapter();

//        testHttpStringParamsRequest();
//        testHttpPostStringParamsRequest(RkHttpRequestType.STRING_REQUEST);
//        testHttpPostStringParamsRequest(RkHttpRequestType.JSON_REQUEST);
//        testHTTPPostMulipartParams();
//        testPUTStringParams(RkHttpRequestType.STRING_REQUEST);
//        testPUTStringParams(RkHttpRequestType.JSON_REQUEST);
//        testPUTMultiParams();

    }




    public class TimeConsumingTask extends AsyncTask<Void, String, Void> implements DialogInterface.OnCancelListener {
        RkProgressBar mProgressHUD;

        @Override
        protected void onPreExecute() {

            mProgressHUD = RkProgressBar.show(MainActivity.this, "Connecting", false, this, RkProgressBar.sDefaultResourceID, RkProgressBar.LIGHT_THEME);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                publishProgress("Connecting");
                Thread.sleep(5000);
                publishProgress("Downloading");
                Thread.sleep(10000);
                publishProgress("Done");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            mProgressHUD.updateMessage(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {
            mProgressHUD.dismiss();
            super.onPostExecute(result);
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            this.cancel(true);
            mProgressHUD.dismiss();
        }
    }


    /**
     * This function already tested and passed .
     * in case test with emulator , must change localhost to 10.0.2.2 or use IP of server .
     * my IP is 192.168.11.92 ;
     */

    private void testHttpStringParamsRequest() {

        String url = "http://192.168.11.92/HTTPStringParamsRequestTest.php";
        RkLogger.e("testHttpStringParamsRequest", "url = " + url);

        Map<String, Object> mapParams = new HashMap<>();
        mapParams.put("username", "Pham Xuan Tuyen");
        mapParams.put("password", "123");


        Map<String, String> headers = new HashMap<>();
        headers.put("Accept-Encoding", "gzip");

        RkHttpConnectionHelper.get(url, headers, mapParams, new RkHttpCallback() {
            @Override
            public void onFailure(int code, Exception e) {
                RkLogger.e("onFailure", "response = " + code);
            }

            @Override
            public void onProgressUpdate(int progress) {

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(String response) {
                RkLogger.e("onSuccess", "response = " + response);
            }
        });
    }

    /**
     * This function already tested and passed .( HTTP post json params and HTTP post String params ) .
     * in case test with emulator , must change localhost to 10.0.2.2 or use IP of server .
     * my IP is 192.168.11.92 ;
     */


    private void testHttpPostStringParamsRequest(RkHttpRequestType type) {

        String url = "http://192.168.11.92/HTTPStringParamsPost.php";
        RkLogger.e("testHttpPostStringParamsRequest", "url = " + url);
        Map<String, Object> mapParams = new HashMap<>();
        mapParams.put("username", "tuyenpx");
        mapParams.put("password", "123");
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept-Encoding", "gzip");

        RkHttpConnectionHelper.post(type, url, headers, mapParams, new RkHttpCallback() {
            @Override
            public void onFailure(int code, Exception e) {
                RkLogger.e("onFailure", "response = " + code);
            }

            @Override
            public void onProgressUpdate(int progress) {

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(String response) {
                RkLogger.e("onSuccess", "response = " + response);
            }
        });

    }

    /**
     * This function already tested and passed ,
     * use to post multiparams to server .
     * in case post file to server , must add file to params as ArrayList .
     */


    private void testHTTPPostMulipartParams() {
        String url = "http://192.168.11.92/HTTPStringParamsPost.php";
        RkLogger.e("testHttpPostStringParamsRequest", "url = " + url);
        Map<String, Object> mapParams = new HashMap<>();
        mapParams.put("username", "tuyenpx");
        mapParams.put("password", "123");
        // path file need to upload to server .
        ArrayList<File> mListFileUpload2Server = new ArrayList<>();
        mListFileUpload2Server.add(new File("/storage/sdcard/studio.png"));
        mListFileUpload2Server.add(new File("/storage/sdcard/getUserInfo.php"));
        mapParams.put("file", mListFileUpload2Server);
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept-Encoding", "gzip");

        RkHttpConnectionHelper.post(RkHttpRequestType.MULTIPART_REQUEST, url, headers, mapParams, new RkHttpCallback() {
            @Override
            public void onFailure(int code, Exception e) {
                RkLogger.e("onFailure", "response = " + code);
            }

            @Override
            public void onProgressUpdate(int progress) {
                RkLogger.e("onProgressUpdate", "progress = " + progress);

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(String response) {
                RkLogger.e("onSuccess", "response = " + response);
            }
        });

    }

    /**
     * This function already tested and passed ,
     * use to put params to server .
     */
    private void testPUTStringParams(RkHttpRequestType type) {
        String url = "http://192.168.11.92/HTTPParamPUT.php";
        Map<String, Object> mapParams = new HashMap<>();
        mapParams.put("username", "tuyenpx");
        mapParams.put("password", "123");
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept-Encoding", "gzip");

        RkHttpConnectionHelper.put(type, url, headers, mapParams, new RkHttpCallback() {
            @Override
            public void onFailure(int code, Exception e) {
                RkLogger.e("onFailure", "response = " + code);
            }

            @Override
            public void onProgressUpdate(int progress) {
                RkLogger.e("onProgressUpdate", "progress = " + progress);

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(String response) {
                RkLogger.e("onSuccess", "response = " + response);
            }
        });

    }

    /**
     * This funtion use to test PUT params to server .
     * in case post file to server , must add file to params as ArrayList .
     */

    private void testPUTMultiParams() {
        String url = "http://192.168.11.92/HTTPParamPUT.php";
        Map<String, Object> mapParams = new HashMap<>();
        mapParams.put("username", "tuyenpx");
        mapParams.put("password", "123");

        // path file need to upload to server .

        ArrayList<File> mListFileUpload2Server = new ArrayList<>();
        mListFileUpload2Server.add(new File("/storage/sdcard/getUserInfo.php"));
        mapParams.put("file", mListFileUpload2Server);

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept-Encoding", "gzip");

        RkHttpConnectionHelper.put(RkHttpRequestType.MULTIPART_REQUEST, url, headers, mapParams, new RkHttpCallback() {
            @Override
            public void onFailure(int code, Exception e) {
                RkLogger.e("onFailure", "response = " + code);
            }

            @Override
            public void onProgressUpdate(int progress) {
                RkLogger.e("onProgressUpdate", "progress = " + progress);

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(String response) {
                RkLogger.e("onSuccess", "response = " + response);
            }
        });

    }


    /**
     * This funtion use to test PUT params to server .
     * in case post file to server , must add file to params as ArrayList .
     */

    private void testDeleteParams() {
        String url = "http://192.168.11.92/HTTPParamDelete.php";
        Map<String, Object> mapParams = new HashMap<>();
        mapParams.put("username", "tuyenpx");
        mapParams.put("password", "123");
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept-Encoding", "gzip");

        RkHttpConnectionHelper.delete(url, headers, mapParams, new RkHttpCallback() {
            @Override
            public void onFailure(int code, Exception e) {
                RkLogger.e("onFailure", "response = " + code);
            }

            @Override
            public void onProgressUpdate(int progress) {
                RkLogger.e("onProgressUpdate", "progress = " + progress);

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(String response) {
                RkLogger.e("onSuccess", "response = " + response);
            }
        });

    }


    /**
     * this function use to test RkDialog.java with CustomView ,
     */

    private void testRkDialogWithCustomView() {
        rkDialog = RkDialog.getInstance(this);
        AlertDialog v = rkDialog.show(R.string.title_dialog_test, RkDialog.DEFAULT_RESID, R.string.cancel, R.string.ok, RkDialog.DEFAULT_RESID, R.layout.main_test, false, this);
        Button button = (Button) v.findViewById(R.id.btn1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RkLogger.e("btn1 onclick", "btn1 clicked");
            }
        });
    }

    /**
     * when you used rkDialog , to prevent FC issue when activity re-create again
     * you should release rkDialog in onPause function like bellow .
     */

    @Override
    protected void onPause() {
        super.onPause();
        if (rkDialog != null) {
            rkDialog.release();
            rkDialog = null;
        }
    }

    /**
     * this function to test RkDialog.java with custom ArrayAdapter .
     */
    private void testRkDialogWithCustomAdapter() {
        final ArrayList arrayList = new ArrayList();
        arrayList.add("Ha Noi");
        arrayList.add("Nam Dinh");
        arrayList.add("Thai Binh");
        arrayList.add("Bac Giang");

        ArrayAdapter arrayAdapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_list_item_1, arrayList);
        rkDialog = RkDialog.getInstance(MainActivity.this);
        AlertDialog v = rkDialog.show(R.string.title_dialog_test, RkDialog.DEFAULT_RESID, R.string.cancel, R.string.ok, RkDialog.DEFAULT_RESID, R.layout.layout_test_customview, false, this);
        ListView listView = (ListView) v.findViewById(R.id.listView);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RkLogger.e("onItemClick : ", arrayList.get(i).toString() + " is clicked");
            }
        });


    }

    /**
     * this function use to test RkLogger.java
     */

    private void testRkLogger() {
        Config.initDebug();
        RkLogger.v(TAG, "onCreate");
        RkLogger.d(TAG, "onCreate");
        RkLogger.i(TAG, "onCreate");
        RkLogger.w(TAG, "onCreate");
        RkLogger.e(TAG, "onCreate");
        RkLogger.wtf(TAG, "onCreate");
    }

    /**
     * this function use to test RkDateTimeUtils.java
     */

    private void testRkDateTimeUtils() {
        RkDateTimeUtils mRkDateTimeUtils = RkDateTimeUtils.getInstance();
        Date today = mRkDateTimeUtils.getCurrentDateTime();
        RkLogger.e("mRkDateTimeUtils.getCurrentDateTime()", today.toString());
        String today1 = mRkDateTimeUtils.getCurrentDateTime("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        RkLogger.e("mRkDateTimeUtils.getCurrentDateTime(\"yyyy-MM-dd'T'HH:mm:ss.SSSZ\")", today1);
        long today2 = mRkDateTimeUtils.convertDate2Milliseconds(today);
        RkLogger.e("mRkDateTimeUtils.convertDate2Miliseconds(today)", today2 + "");
        Date tomorrow = mRkDateTimeUtils.addDay(today, 1);
        RkLogger.e("mRkDateTimeUtils.addDay(today, 1)", tomorrow.toString());
        long tomorrow2 = mRkDateTimeUtils.convertDate2Milliseconds(tomorrow);
        RkLogger.e("mRkDateTimeUtils.convertDate2Miliseconds(tomorrow)", tomorrow2 + "");
        boolean istomrrow = mRkDateTimeUtils.isTomorrow(tomorrow2);
        RkLogger.e("mRkDateTimeUtils.isTomorrow(tomorrow2)", istomrrow + "");
        Date nextMonth = mRkDateTimeUtils.addMonth(today, 1);
        RkLogger.e("mRkDateTimeUtils.addMonth(today, 1)", nextMonth.toString());
        Date yesterday = mRkDateTimeUtils.addDay(today, -1);
        RkLogger.e("mRkDateTimeUtils.addDay(today, -1)", yesterday.toString());
        long yesterday2 = mRkDateTimeUtils.convertDate2Milliseconds(yesterday);
        RkLogger.e("mRkDateTimeUtils.convertDate2Miliseconds(yesterday)", yesterday2 + "");
        boolean isyesterday = mRkDateTimeUtils.isYesterday(yesterday2);
        RkLogger.e("mRkDateTimeUtils.isYesterday(yesterday2)", isyesterday + "");
        try {
            String newYesterday = mRkDateTimeUtils.convertDate2Date(yesterday, "yyyy-MM-dd");
            System.out.println("newYesterday " + newYesterday);
        } catch (ParseException e) {
            RkLogger.e("ParseException", e.toString());
        }


        try {
            Date newYesterday2 = mRkDateTimeUtils.convertString2Date("2016-05-08", "yyyy-MM-dd");
            System.out.println("newYesterday2 " + newYesterday2);
        } catch (ParseException e) {
            RkLogger.e("ParseException", e.toString());
        }

        try {
            String newYesterday = mRkDateTimeUtils.convertString2Date("2016-05-08", "yyyy-MM-dd", "dd-MM-yyyy");
            System.out.println("newYesterday " + newYesterday);
        } catch (ParseException e) {
            RkLogger.e("ParseException", e.toString());
        }


        try {
            long time = mRkDateTimeUtils.convertString2Milliseconds("2016-05-05T16:18:18.188+0700", "yyyy-MM-dd'T'HH:mm:ss.SSSZ");

            RkLogger.e("mRkDateTimeUtils.convertString2Milliseconds(\"2016-05-05T16:18:18.188+0700\",\"yyyy-MM-dd'T'HH:mm:ss.SSSZ\")", time + "");
        } catch (ParseException e) {
            RkLogger.e("ParseException", e.toString());
        }


        Date date = mRkDateTimeUtils.convertLong2Date(Long.parseLong("1462683746641"));
        RkLogger.e("mRkDateTimeUtils.convertLong2Date(Long.parseLong(\"1462683746641\")", date.toString());


    }


    @Override
    public void onNegativeButtonClick(DialogInterface dialog, int which) {

        RkLogger.e(TAG, "onNegativeButtonClick");
    }

    @Override
    public void onPositiveButtonClick(DialogInterface dialog, int which) {
        RkLogger.e(TAG, "onPositiveButtonClick");

    }

    @Override
    public void onCancel(DialogInterface dialog) {
        RkLogger.e(TAG, "onCancel");

    }

    public void onToFileDemo(View view) {
        startActivity(new Intent(this, FileUtilActivity.class));
    }
}
