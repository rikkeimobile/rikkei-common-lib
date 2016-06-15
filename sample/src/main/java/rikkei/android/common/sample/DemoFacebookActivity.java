package rikkei.android.common.sample;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;

import org.json.JSONObject;

import java.util.Arrays;

import rikkei.android.common.lib.RkLogger;
import rikkei.android.sns.lib.RkFaceBookUtils;
import rikkei.android.sns.lib.RkFaceBookLoginProperty;

/**
 * Created by tuyenpx on 06/06/2016.
 */
public class DemoFacebookActivity extends Activity implements View.OnClickListener {
    private RkFaceBookUtils mRkFaceBookUtils;
    private CallbackManager callbackManager;


    private Button mLogin;
    private Button mLogOut;
    private Button mGetProfile;
    private Button mGetListCheckIn;
    private Button mPostStatus;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_logout:
                mRkFaceBookUtils.logout();
                setupView(mRkFaceBookUtils.isLogin());
                break;
            case R.id.btn_getlistcheckin:
                getCheckInPlace();
                break;
            case R.id.btn_getprofile:
                getprofile();
                break;
            case R.id.btn_poststatus:
                postStatus();
                break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);

        mLogin = (Button) findViewById(R.id.btn_login);
        mLogin.setOnClickListener(this);

        mLogOut = (Button) findViewById(R.id.btn_logout);
        mLogOut.setOnClickListener(this);

        mGetProfile = (Button) findViewById(R.id.btn_getprofile);
        mGetProfile.setOnClickListener(this);

        mGetListCheckIn = (Button) findViewById(R.id.btn_getlistcheckin);
        mGetListCheckIn.setOnClickListener(this);

        mPostStatus = (Button) findViewById(R.id.btn_poststatus);
        mPostStatus.setOnClickListener(this);
        mRkFaceBookUtils = RkFaceBookUtils.getInstance(this);
        callbackManager = CallbackManager.Factory.create();
        setupView(mRkFaceBookUtils.isLogin());
    }

    private void setupView(boolean isLogin) {
        mLogin.setEnabled(!isLogin);
        mLogOut.setEnabled(isLogin);
        mPostStatus.setEnabled(isLogin);
        mGetListCheckIn.setEnabled(isLogin);
        mGetProfile.setEnabled(isLogin);
    }


    private void login() {

        RkFaceBookLoginProperty loginProperty = new RkFaceBookLoginProperty();
        loginProperty.setReadPermissions(Arrays.asList("email", "public_profile", "user_tagged_places"));
        mRkFaceBookUtils.logIn(loginProperty, callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                setupView(mRkFaceBookUtils.isLogin());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRkFaceBookUtils.release();
    }

    private void getCheckInPlace() {
        mRkFaceBookUtils.getListCheckIn(new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                Log.e("getCheckInPlace ", "getCheckInPlace" + response.toString());
            }
        });
    }


    private void postStatus() {
        mRkFaceBookUtils.postStatus(new ShareLinkContent.Builder()
                .setContentTitle("wow !!!!!! ")
                .setContentDescription(
                        "so exciting picture ! ")
                .setImageUrl(Uri.parse("http://tapchianhdep.com/wp-content/uploads/2016/05/stt-ve-mua-buon-tam-trang-nhat.jpg"))
                .build(), callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                RkLogger.e("tuyenpx", result.toString());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

    }

    private void getprofile() {
        mRkFaceBookUtils.getProfile("id, first_name, last_name, email,gender, birthday, location", new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                RkLogger.e("object", object.toString());
                RkLogger.e("response", response.toString());
            }
        });
    }


}

